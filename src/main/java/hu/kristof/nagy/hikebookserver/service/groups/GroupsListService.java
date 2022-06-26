package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
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
                .collection(DbCollections.GROUP)
                .select(DbFields.Group.NAME)
                .whereEqualTo(DbFields.Group.MEMBER_NAME, userName)
                .get();
        return FutureUtil.handleFutureGet(() -> new ArrayList<>(
                queryFuture.get().getDocuments().stream()
                .map(queryDocumentSnapshot ->
                        queryDocumentSnapshot.getString(DbFields.Group.NAME)
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
                    .collection(DbCollections.GROUP)
                    .select(DbFields.Group.NAME)
                    .whereNotIn(DbFields.Group.NAME, connectedGroupNames)
                    .get();
            return FutureUtil.handleFutureGet(() -> new ArrayList<>(
                    queryFuture.get().getDocuments().stream()
                    .map(queryDocumentSnapshot ->
                            queryDocumentSnapshot.getString(DbFields.Group.NAME)
                    )
                    .collect(Collectors.toSet()) // distinct substitute
            ));
        }
    }

    private List<String> listAllGroups() {
        var groups = db.collection(DbCollections.GROUP);
        var res = new HashSet<String>(); // distinct substitute
        for (var doc: groups.listDocuments()) {
            String groupName = FutureUtil.handleFutureGet(() ->
                    doc.get().get().getString(DbFields.Group.NAME)
            );
            res.add(groupName);
        }
        return new ArrayList<>(res);
    }
}
