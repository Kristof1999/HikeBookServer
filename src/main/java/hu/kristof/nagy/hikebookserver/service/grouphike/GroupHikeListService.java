package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.GroupHikeListHelper;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
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

    public ResponseResult<List<GroupHikeListHelper>> listGroupHikes(String userName, boolean isConnectedPage) {
        if (isConnectedPage) {
            return ResponseResult.success(listConnectedGroupHikes(userName));
        } else {
            return ResponseResult.success(listNotConnectedGroupHikes(userName));
        }
    }

    private List<GroupHikeListHelper> listConnectedGroupHikes(String userName) {
        var queryFuture = db
                .collection(DbCollections.GROUP_HIKE)
                .select(GroupHikeListHelper.getSelectPaths())
                .whereEqualTo(DbFields.GroupHike.PARTICIPANT_NAME, userName)
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
                    .collection(DbCollections.GROUP_HIKE)
                    .select(GroupHikeListHelper.getSelectPaths())
                    .whereNotIn(DbFields.GroupHike.NAME, connectedGroupHikeNames)
                    .get();
            return FutureUtil.handleFutureGet(() -> new ArrayList<>(
                    queryFuture.get().getDocuments().stream()
                            .map(GroupHikeListHelper::from)
                            .collect(Collectors.toSet()) // distinct substitute
            ));
        }
    }

    private List<GroupHikeListHelper> listAllGroupHikes() {
        var groupHikes = db.collection(DbCollections.GROUP_HIKE);
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
