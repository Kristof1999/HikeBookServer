package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.DateTime;
import hu.kristof.nagy.hikebookserver.model.GroupHikeCreateHelper;
import hu.kristof.nagy.hikebookserver.model.routes.GroupHikeRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.RouteServiceUtils;
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
                createGroupHikeRoute(transaction, groupHikeName, groupHikeRoute);
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
        data.put(DbPathConstants.GROUP_NAME, groupHikeName);
        var docRef = db.collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                .document();
        transaction.create(docRef, data);
    }

    private void createGroupHikeRoute(
            Transaction transaction,
            String groupHikeName,
            GroupHikeRoute groupHikeRoute
    ) {
        if (RouteServiceUtils.routeNameExistsForOwner(
                transaction, db, groupHikeName, groupHikeRoute.getRouteName(), DbPathConstants.ROUTE_GROUP_HIKE_NAME
        )) {
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(groupHikeRoute.getRouteName())
            );
        } else {
            if (RouteServiceUtils.arePointsUniqueForOwner(
                    transaction, db, groupHikeName, groupHikeRoute.getPoints(), DbPathConstants.ROUTE_GROUP_HIKE_NAME
            )) {
                Map<String, Object> data = groupHikeRoute.toMap();
                var docRef = db
                        .collection(DbPathConstants.COLLECTION_ROUTE)
                        .document();
                // wait for write to finish
                transaction.create(docRef, data);
            } else {
                throw new IllegalArgumentException(RouteServiceUtils.POINTS_NOT_UNIQE);
            }
        }
    }
}
