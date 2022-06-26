package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.DateTime;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;

import java.util.Map;

public final class GroupHikeGeneralConnectService {
    /**
     * Connects or disconnects the given user to the given group hike
     * based on whether the user is at the connected page or not.
     * If everyone left the given group hike, then the
     * associated route will be deleted.
     * If someone tries to connect to a deleted group hike, he/she
     * will receive a message that the group hike does no longer exist.
     * @param groupHikeName name of group hike to join/leave
     * @param userName name of user who wants to join/leave the given group hike
     * @param isConnectedPage boolean that tells whether the user is at the connected page or not
     * @param dateTime date and time of the group hike
     * @return true if joining/leaving was successful
     */
    public static ResponseResult<Boolean> generalConnect(
            Firestore db,
            String groupHikeName,
            String userName,
            boolean isConnectedPage,
            DateTime dateTime
    ) {
        if (isConnectedPage) {
            return ResponseResult.success(disconnect(db, groupHikeName, userName));
        } else {
            return ResponseResult.success(connect(db, groupHikeName, userName, dateTime));
        }
    }

    private static boolean connect(Firestore db, String groupHikeName, String userName, DateTime dateTime) {
        var transactionFuture = db.runTransaction(transaction -> {
            if (participantNumber(db, transaction, groupHikeName) == 0)
                throw new IllegalArgumentException("A csoportos túra véget ért/megszűnt.");

            Map<String, Object> data = dateTime.toMap();
            data.put(DbFields.GroupHike.DATE_TIME, dateTime.toString());
            data.put(DbFields.GroupHike.NAME, groupHikeName);
            data.put(DbFields.GroupHike.PARTICIPANT_NAME, userName);
            return FutureUtil.handleFutureGet(() -> {
                var docRef = db.collection(DbCollections.GROUP_HIKE)
                        .document();
                transaction.create(docRef, data);
                return true;
            });
        });

        return FutureUtil.handleFutureGet(transactionFuture::get);
    }

    private static boolean disconnect(Firestore db, String groupHikeName, String userName) {
        var transactionFuture = db.runTransaction(transaction -> {
            var query = db
                    .collection(DbCollections.GROUP_HIKE)
                    .whereEqualTo(DbFields.GroupHike.NAME, groupHikeName)
                    .whereEqualTo(DbFields.GroupHike.PARTICIPANT_NAME, userName);
            var queryFuture = transaction.get(query);
            var queryDocs = FutureUtil.handleFutureGet(() ->
                    queryFuture.get().getDocuments()
            );
            return Util.handleListSize(queryDocs, documentSnapshots -> {
                if (participantNumber(db, transaction, groupHikeName) == 1) {
                    deleteGroupHikeRoute(db, transaction, groupHikeName);
                }

                String id = documentSnapshots.get(0).getId();
                var docRef = db.collection(DbCollections.GROUP_HIKE)
                        .document(id);
                transaction.delete(docRef);
                return true;
            });
        });
        return FutureUtil.handleFutureGet(transactionFuture::get);
    }

    private static void deleteGroupHikeRoute(Firestore db, Transaction transaction, String groupHikeName) {
        var query = db.collection(DbCollections.ROUTE)
                .whereEqualTo(DbFields.GroupHikeRoute.NAME, groupHikeName);
        var queryFuture = transaction.get(query);
        var queryDocs = FutureUtil.handleFutureGet(() -> queryFuture.get().getDocuments());
        Util.handleListSize(queryDocs, documentSnapshots -> {
            String id = documentSnapshots.get(0).getId();
            var docRef = db.collection(DbCollections.ROUTE)
                    .document(id);
            return transaction.delete(docRef);
        });
    }

    private static int participantNumber(Firestore db, Transaction transaction, String groupHikeName) {
        var query = db.collection(DbCollections.GROUP_HIKE)
                .whereEqualTo(DbFields.GroupHike.NAME, groupHikeName);
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() -> queryFuture.get().getDocuments().size());
    }
}
