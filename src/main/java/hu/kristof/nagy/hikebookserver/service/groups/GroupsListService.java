package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupsListService {

    @Autowired
    private Firestore db;

    public ResponseResult<List<String>> listGroups(String userName, boolean isConnectedPage) {
        if (isConnectedPage) {
            return ResponseResult.success(listConnectedGroupNames(userName));
        } else {
            return ResponseResult.success(listNotConnectedGroupNames(userName));
        }
    }

    private List<String> listConnectedGroupNames(String userName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_GROUP)
                .select(DbPathConstants.GROUP_NAME)
                .whereEqualTo(DbPathConstants.GROUP_MEMBER_NAME, userName)
                .get();
        return FutureUtil.handleFutureGet(() -> new ArrayList<>(
                queryFuture.get().getDocuments().stream()
                .map(queryDocumentSnapshot ->
                        queryDocumentSnapshot.getString(DbPathConstants.GROUP_NAME)
                )
                .collect(Collectors.toSet()) // distinct substitute
        ));
    }

    private List<String> listNotConnectedGroupNames(String userName) {
        var connectedGroupNames = listConnectedGroupNames(userName);
        if (connectedGroupNames.isEmpty()) {
            return listAllGroups();
        } else {
            var queryFuture = db
                    .collection(DbPathConstants.COLLECTION_GROUP)
                    .select(DbPathConstants.GROUP_NAME)
                    .whereNotIn(DbPathConstants.GROUP_NAME, connectedGroupNames)
                    .get();
            return FutureUtil.handleFutureGet(() -> new ArrayList<>(
                    queryFuture.get().getDocuments().stream()
                    .map(queryDocumentSnapshot ->
                            queryDocumentSnapshot.getString(DbPathConstants.GROUP_NAME)
                    )
                    .collect(Collectors.toSet()) // distinct substitute
            ));
        }
    }

    private List<String> listAllGroups() {
        var groups = db.collection(DbPathConstants.COLLECTION_GROUP);
        var res = new HashSet<String>(); // distinct substitute
        for (var doc: groups.listDocuments()) {
            String groupName = FutureUtil.handleFutureGet(() ->
                    doc.get().get().getString(DbPathConstants.GROUP_NAME)
            );
            res.add(groupName);
        }
        return new ArrayList<>(res);
    }
}
