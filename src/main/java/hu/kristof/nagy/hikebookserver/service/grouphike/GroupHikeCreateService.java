package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.DateTime;
import hu.kristof.nagy.hikebookserver.model.GroupHikeCreateHelper;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.GroupHikeRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.Map;

public final class GroupHikeCreateService {
    /**
     * Creates the group hike with the given name, route,
     * and user name as the first participant,
     * if the group hike name is unique.
     */
    public static ResponseResult<Boolean> createGroupHike(
            Firestore db,
            String userName,
            String groupHikeName,
            GroupHikeCreateHelper helper
    ) {
        var transactionFuture = db.runTransaction(transaction -> {
            if (isGroupHikeNameUnique(db, transaction, groupHikeName)) {
                var groupHikeRoute = new GroupHikeRoute(helper.getRoute(), groupHikeName);
                createGroupHikeRoute(db, transaction, groupHikeRoute, helper.getDateTime());
                createGroupHike(db, transaction, userName, groupHikeName, helper.getDateTime());
            } else {
                throw new IllegalArgumentException("A csoport túra neve nem egyedi! Kérem, hogy válasszon másik nevet.");
            }
            return true;
        });
        return ResponseResult.success(FutureUtil.handleFutureGet(transactionFuture::get));
    }

    private static boolean isGroupHikeNameUnique(
            Firestore db,
            Transaction transaction,
            String groupHikeName
    ) {
        var query = db.collection(DbCollections.GROUP_HIKE)
                .whereEqualTo(DbFields.GroupHike.NAME, groupHikeName);
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }

    private static void createGroupHike(
            Firestore db,
            Transaction transaction,
            String participantName,
            String groupHikeName,
            DateTime dateTime
    ) {
        Map<String, Object> data = dateTime.toMap();
        data.put(DbFields.GroupHike.PARTICIPANT_NAME, participantName);
        data.put(DbFields.GroupHike.NAME, groupHikeName);
        data.put(DbFields.GroupHike.DATE_TIME, dateTime.toString());
        var docRef = db.collection(DbCollections.GROUP_HIKE)
                .document();
        transaction.create(docRef, data);
    }

    private static void createGroupHikeRoute(
            Firestore db,
            Transaction transaction,
            GroupHikeRoute groupHikeRoute,
            DateTime dateTime
    ) {
        Map<String, Object> data = groupHikeRoute.toMap();
        var description = (String) data.get(DbFields.Route.DESCRIPTION);
        description = dateTime.toString() + "\n" + description;
        data.put(DbFields.Route.DESCRIPTION, description);
        var docRef = db
                .collection(DbCollections.ROUTE)
                .document();
        // wait for write to finish
        transaction.create(docRef, data);
    }
}
