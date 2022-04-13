package hu.kristof.nagy.hikebookserver.service.route.grouproute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.RouteServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroupRouteCreateService {

    @Autowired
    private Firestore db;

    public boolean createGroupRoute(GroupRoute route) {
        var transactionFuture = db.runTransaction(transaction -> {
            if (RouteServiceUtils.routeNameExistsForOwner(
                    transaction, db, route.getGroupName(), route.getGroupName(), DbPathConstants.ROUTE_GROUP_NAME
            )) {
                throw new IllegalArgumentException(
                        RouteServiceUtils.getRouteNameNotUniqueString(route.getRouteName())
                );
            } else {
                if (RouteServiceUtils.arePointsUniqueForOwner(
                        transaction, db, route.getGroupName(), route.getPoints(), DbPathConstants.ROUTE_GROUP_NAME
                )) {
                    Map<String, Object> data = route.toMap();
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
        });
        return FutureUtil.handleFutureGet(transactionFuture::get);
    }
}
