package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.DateTime;
import hu.kristof.nagy.hikebookserver.model.GroupHikeCreateHelper;
import hu.kristof.nagy.hikebookserver.model.routes.GroupHikeRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.RouteServiceUtils;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.GroupHikeRouteUniquenessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroupHikeCreateService {
    @Autowired
    private Firestore db;

    public boolean createGroupHike(
            String userName,
            String groupHikeName,
            GroupHikeCreateHelper helper
    ) {
        var transactionFuture = db.runTransaction(transaction -> {
            if (isGroupHikeNameUnique(transaction, groupHikeName)) {
                var groupHikeRoute = new GroupHikeRoute(helper.getRoute(), groupHikeName);
                createGroupHikeRoute(transaction, groupHikeName, groupHikeRoute, helper.getDateTime());
                createGroupHike(transaction, userName, groupHikeName, helper.getDateTime());
            } else {
                throw new IllegalArgumentException("A csoport túra neve nem egyedi! Kérem, hogy válasszon másik nevet.");
            }
            return true;
        });
        return FutureUtil.handleFutureGet(transactionFuture::get);
    }

    private boolean isGroupHikeNameUnique(
            Transaction transaction,
            String groupHikeName
    ) {
        var query = db.collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                .whereEqualTo(DbPathConstants.GROUP_HIKE_NAME, groupHikeName);
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
        data.put(DbPathConstants.GROUP_HIKE_PARTICIPANT_NAME, participantName);
        data.put(DbPathConstants.GROUP_HIKE_NAME, groupHikeName);
        data.put(DbPathConstants.GROUP_HIKE_DATE_TIME, dateTime.toString());
        var docRef = db.collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                .document();
        transaction.create(docRef, data);
    }

    private void createGroupHikeRoute(
            Transaction transaction,
            String groupHikeName,
            GroupHikeRoute groupHikeRoute,
            DateTime dateTime
    ) {
        var handler = new GroupHikeRouteUniquenessHandler(
                transaction,
                db,
                groupHikeName,
                groupHikeRoute.getRouteName(),
                groupHikeRoute.getPoints()
        );
        groupHikeRoute.handleRouteUniqueness(handler);

        Map<String, Object> data = groupHikeRoute.toMap();
        var description = (String) data.get(DbPathConstants.ROUTE_DESCRIPTION);
        description = dateTime.toString() + "\n" + description;
        data.put(DbPathConstants.ROUTE_DESCRIPTION, description);
        var docRef = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .document();
        // wait for write to finish
        transaction.create(docRef, data);
    }
}
