package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
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

    public List<String> listGroupHikes(String userName, boolean isConnectedPage) {
        if (isConnectedPage) {
            return listConnectedGroupHikeNames(userName);
        } else {
            return listNotConnectedGroupHikeNames(userName);
        }
    }

    private List<String> listConnectedGroupHikeNames(String userName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                .select(DbPathConstants.GROUP_HIKE_NAME)
                .whereEqualTo(DbPathConstants.GROUP_HIKE_PARTICIPANT_NAME, userName)
                .get();
        return FutureUtil.handleFutureGet(() -> new ArrayList<>(
                queryFuture.get().getDocuments().stream()
                        .map(queryDocumentSnapshot ->
                                queryDocumentSnapshot.getString(DbPathConstants.GROUP_HIKE_NAME)
                        )
                        .collect(Collectors.toSet()) // distinct substitute
        ));
    }

    private List<String> listNotConnectedGroupHikeNames(String userName) {
        var connectedGroupHikeNames = listConnectedGroupHikeNames(userName);
        if (connectedGroupHikeNames.isEmpty()) {
            return listAllGroupHikeNames();
        } else {
            var queryFuture = db
                    .collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                    .select(DbPathConstants.GROUP_HIKE_NAME)
                    .whereNotIn(DbPathConstants.GROUP_HIKE_NAME, connectedGroupHikeNames)
                    .get();
            return FutureUtil.handleFutureGet(() -> new ArrayList<>(
                    queryFuture.get().getDocuments().stream()
                            .map(queryDocumentSnapshot ->
                                    queryDocumentSnapshot.getString(DbPathConstants.GROUP_HIKE_NAME)
                            )
                            .collect(Collectors.toSet()) // distinct substitute
            ));
        }
    }

    private List<String> listAllGroupHikeNames() {
        var groupHikes = db.collection(DbPathConstants.COLLECTION_GROUP_HIKE);
        var res = new HashSet<String>(); // distinct substitute
        for (var doc: groupHikes.listDocuments()) {
            String groupHikeName = FutureUtil.handleFutureGet(() ->
                    doc.get().get().getString(DbPathConstants.GROUP_HIKE_NAME)
            );
            res.add(groupHikeName);
        }
        return new ArrayList<>(res);
    }
}
