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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroupHikeCreateService {
    @Autowired
    private Firestore db;

    /**
     * Creates the group hike with the given name, route,
     * and user name as the first participant,
     * if the group hike name is unique.
     */
    public ResponseResult<Boolean> createGroupHike(
            String userName,
            String groupHikeName,
            GroupHikeCreateHelper helper
    ) {
        var transactionFuture = db.runTransaction(transaction -> {
            if (isGroupHikeNameUnique(transaction, groupHikeName)) {
                var groupHikeRoute = new GroupHikeRoute(helper.getRoute(), groupHikeName);
                createGroupHikeRoute(transaction, groupHikeRoute, helper.getDateTime());
                createGroupHike(transaction, userName, groupHikeName, helper.getDateTime());
            } else {
                throw new IllegalArgumentException("A csoport túra neve nem egyedi! Kérem, hogy válasszon másik nevet.");
            }
            return true;
        });
        return ResponseResult.success(FutureUtil.handleFutureGet(transactionFuture::get));
    }

    private boolean isGroupHikeNameUnique(
            Transaction transaction,
            String groupHikeName
    ) {
        var query = db.collection(DbCollections.GROUP_HIKE)
                .whereEqualTo(DbFields.GroupHike.NAME, groupHikeName);
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }

    private void createGroupHike(
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

    private void createGroupHikeRoute(
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
