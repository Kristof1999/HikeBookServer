package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public final class GroupsListService {
    public static ResponseResult<List<String>> listGroups(Firestore db, String userName, boolean isConnectedPage) {
        if (isConnectedPage) {
            return ResponseResult.success(listConnectedGroupNames(db, userName));
        } else {
            return ResponseResult.success(listNotConnectedGroupNames(db, userName));
        }
    }

    private static List<String> listConnectedGroupNames(Firestore db, String userName) {
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

    private static List<String> listNotConnectedGroupNames(Firestore db, String userName) {
        var connectedGroupNames = listConnectedGroupNames(db, userName);
        if (connectedGroupNames.isEmpty()) {
            return listAllGroups(db);
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

    private static List<String> listAllGroups(Firestore db) {
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
