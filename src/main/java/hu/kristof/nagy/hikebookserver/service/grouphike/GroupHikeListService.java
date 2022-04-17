package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.GroupHikeListHelper;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupHikeListService {

    @Autowired
    private Firestore db;

    public List<GroupHikeListHelper> listGroupHikes(String userName, boolean isConnectedPage) {
        if (isConnectedPage) {
            return listConnectedGroupHikes(userName);
        } else {
            return listNotConnectedGroupHikes(userName);
        }
    }

    private List<GroupHikeListHelper> listConnectedGroupHikes(String userName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                .select(DbPathConstants.GROUP_HIKE_NAME,
                        DbPathConstants.GROUP_HIKE_YEAR,
                        DbPathConstants.GROUP_HIKE_MONTH,
                        DbPathConstants.GROUP_HIKE_DAY_OF_MONTH,
                        DbPathConstants.GROUP_HIKE_HOUR_OF_DAY,
                        DbPathConstants.GROUP_HIKE_MINUTE)
                .whereEqualTo(DbPathConstants.GROUP_HIKE_PARTICIPANT_NAME, userName)
                .get();
        return FutureUtil.handleFutureGet(() -> new ArrayList<>(
                queryFuture.get().getDocuments().stream()
                        .map(GroupHikeListHelper::from)
                        .collect(Collectors.toSet()) // distinct substitute
        ));
    }

    private List<GroupHikeListHelper> listNotConnectedGroupHikes(String userName) {
        var connectedGroupHikeNames = listConnectedGroupHikes(userName)
                .stream()
                .map(GroupHikeListHelper::getGroupHikeName)
                .collect(Collectors.toList());
        if (connectedGroupHikeNames.isEmpty()) {
            return listAllGroupHikes();
        } else {
            var queryFuture = db
                    .collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                    .select(DbPathConstants.GROUP_HIKE_NAME,
                            DbPathConstants.GROUP_HIKE_YEAR,
                            DbPathConstants.GROUP_HIKE_MONTH,
                            DbPathConstants.GROUP_HIKE_DAY_OF_MONTH,
                            DbPathConstants.GROUP_HIKE_HOUR_OF_DAY,
                            DbPathConstants.GROUP_HIKE_MINUTE)
                    .whereNotIn(DbPathConstants.GROUP_HIKE_NAME, connectedGroupHikeNames)
                    .get();
            return FutureUtil.handleFutureGet(() -> new ArrayList<>(
                    queryFuture.get().getDocuments().stream()
                            .map(GroupHikeListHelper::from)
                            .collect(Collectors.toSet()) // distinct substitute
            ));
        }
    }

    private List<GroupHikeListHelper> listAllGroupHikes() {
        var groupHikes = db.collection(DbPathConstants.COLLECTION_GROUP_HIKE);
        var res = new HashSet<GroupHikeListHelper>(); // distinct substitute
        for (var doc: groupHikes.listDocuments()) {
            var docFuture = doc.get();
            GroupHikeListHelper helper = FutureUtil.handleFutureGet(() -> {
                var docSnapshot = docFuture.get();
                return GroupHikeListHelper.from(docSnapshot);
            });
            res.add(helper);
        }
        return new ArrayList<>(res);
    }
}
