package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.Group;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;

import java.util.Map;

public final class GroupsGeneralConnectService {
    public static ResponseResult<Boolean> generalConnect(
            Firestore db,
            String groupName,
            String userName,
            boolean isConnectedPage
    ) {
        if (isConnectedPage) {
            return ResponseResult.success(disconnect(db, groupName, userName));
        } else {
            return ResponseResult.success(connect(db, new Group(groupName, userName)));
        }
    }

    private static boolean connect(Firestore db, Group group) {
        Map<String, Object> data = group.toMap();
        if (groupExists(db, group.getGroupName())) {
            if (hasNotConnected(db, group.getGroupName(), group.getMemberName())) {
                return FutureUtil.handleFutureGet(() -> {
                    db.collection(DbCollections.GROUP)
                            .add(data)
                            .get();
                    return true;
                });
            } else {
                throw new IllegalArgumentException("Ehhez a csoporthoz már csatlakoztál.");
            }
        } else {
            throw new IllegalArgumentException("Ez a csoport: " + group.getGroupName() + " nem létezik.");
        }
    }

    private static boolean groupExists(Firestore db, String groupName) {
        var queryFuture = db.collection(DbCollections.GROUP)
                .whereEqualTo(DbFields.Group.NAME, groupName)
                .get();
        return FutureUtil.handleFutureGet(() -> !queryFuture.get().isEmpty());
    }

    private static boolean hasNotConnected(Firestore db, String groupName, String userName) {
        var queryFuture = db.collection(DbCollections.GROUP)
                .whereEqualTo(DbFields.Group.NAME, groupName)
                .whereEqualTo(DbFields.Group.MEMBER_NAME, userName)
                .get();
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }

    private static boolean disconnect(Firestore db, String groupName, String userName) {
        var futureResult = db.runTransaction(transaction -> {
            var query = db
                    .collection(DbCollections.GROUP)
                    .whereEqualTo(DbFields.Group.NAME, groupName)
                    .whereEqualTo(DbFields.Group.MEMBER_NAME, userName);
            var queryFuture = transaction.get(query);
            var queryDocs = FutureUtil.handleFutureGet(() ->
                    queryFuture.get().getDocuments()
            );

            return Util.handleListSize(queryDocs, documentSnapshots -> {
                String id = documentSnapshots.get(0).getId();
                var docRef = db
                        .collection(DbCollections.GROUP)
                        .document(id);

                if (getNumberOfMembers(db, groupName, transaction) == 1) {
                    deleteGroupRoutes(db, groupName, transaction);
                }

                transaction.delete(docRef);
                return true;
            });
        });

        return FutureUtil.handleFutureGet(futureResult::get);
    }

    private static int getNumberOfMembers(Firestore db, String groupName, Transaction transaction) {
        var query = db
                .collection(DbCollections.GROUP)
                .whereEqualTo(DbFields.Group.NAME, groupName);
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() -> queryFuture
                .get()
                .getDocuments()
        ).size();
    }

    private static void deleteGroupRoutes(Firestore db, String groupName, Transaction transaction) {
        var query = db
                .collection(DbCollections.ROUTE)
                .whereEqualTo(DbFields.GroupRoute.NAME, groupName);
        var queryFuture = transaction.get(query);
        var groupRoutes = FutureUtil.handleFutureGet(() -> queryFuture
                .get()
                .getDocuments()
        );
        groupRoutes.forEach(queryDocumentSnapshot ->
                transaction.delete(queryDocumentSnapshot.getReference())
        );
    }
}
