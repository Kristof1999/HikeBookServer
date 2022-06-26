package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.Group;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.Map;

public final class GroupCreateService {
    /**
     * Creates a group with the given group name, and
     * user name as the first member,
     * if the group name is unique.
     * @return true if creation was successful
     */
    public static ResponseResult<Boolean> createGroup(Firestore db, String groupName, String userName) {
        var transactionFuture = db.runTransaction(transaction -> {
            if (isNameUnique(db, transaction, groupName)) {
                save(db, transaction, new Group(groupName, userName));
                return true;
            } else {
                throw new IllegalArgumentException("A " + groupName + " már létezik! Kérem, hogy válasszon másikat.");
            }
        });
        return ResponseResult.success(FutureUtil.handleFutureGet(transactionFuture::get));
    }

    private static void save(Firestore db, Transaction transaction, Group group) {
        Map<String, Object> data = group.toMap();
        var docRef = db.collection(DbCollections.GROUP)
                .document();
        transaction.create(docRef, data);
    }

    private static boolean isNameUnique(Firestore db, Transaction transaction, String name) {
        var query = db
                .collection(DbCollections.GROUP)
                .whereEqualTo(DbFields.Group.NAME, name);
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() ->
                queryFuture.get().isEmpty()
        );
    }
}
