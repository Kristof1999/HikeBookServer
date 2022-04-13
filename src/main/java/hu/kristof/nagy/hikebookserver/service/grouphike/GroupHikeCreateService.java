package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.GroupHikeCreateHelper;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
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
        db.runTransaction(transaction -> {
            if (isGroupHikeNameUnique(transaction, groupHikeName)) {
                return createGroupHikeRoute(transaction, groupHikeName, helper);
            }
        });
    }

    public boolean isGroupHikeNameUnique(
            Transaction transaction,
            String groupHikeName
    ) {
        var query = db.collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                .whereEqualTo(DbPathConstants.GROUP_HIKE_NAME, groupHikeName);
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }

    public boolean createGroupHikeRoute(
            Transaction transaction,
            String groupHikeName,
            GroupHikeCreateHelper helper
    ) {
        if (RouteServiceUtils.routeNameExistsForOwner(
                transaction, db, groupHikeName, helper.getRoute().getRouteName(), DbPathConstants.ROUTE_GROUP_NAME
        )) {
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(helper.getRoute().getRouteName())
            );
        } else {
            if (RouteServiceUtils.arePointsUniqueForOwner(
                    transaction, db, groupHikeName, helper.getRoute().getPoints(), DbPathConstants.ROUTE_GROUP_NAME
            )) {
                Map<String, Object> data = helper.getRoute().toMap();
                data.put(DbPathConstants.GROUP_HIKE_DATE, helper.getDateTime());
                var docRef = db
                        .collection(DbPathConstants.COLLECTION_ROUTE)
                        .document();
                // wait for write to finish
                transaction.create(docRef, data);
                return true;
            } else {
                throw new IllegalArgumentException(RouteServiceUtils.POINTS_NOT_UNIQE);
            }
        }
    }
}
