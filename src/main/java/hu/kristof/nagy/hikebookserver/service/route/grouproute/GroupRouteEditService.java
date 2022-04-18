package hu.kristof.nagy.hikebookserver.service.route.grouproute;

import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.routes.*;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import hu.kristof.nagy.hikebookserver.service.route.RouteServiceUtils;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.GroupRouteUniquenessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GroupRouteEditService {
    @Autowired
    private Firestore db;

    public boolean editRoute(
            EditedGroupRoute route
    ) {
        String oldRouteName = route.getOldRoute().getRouteName();
        String newRouteName = route.getNewRoute().getRouteName();
        String oldDescription = route.getOldRoute().getDescription();
        String newDescription = route.getNewRoute().getDescription();
        List<Point> oldPoints = route.getOldRoute().getPoints();
        List<Point> newPoints = route.getNewRoute().getPoints();

        var transactionFuture = db.runTransaction(transaction -> {
            if (oldRouteName.equals(newRouteName)) {
                if (oldDescription.equals(newDescription)) {
                    if (oldPoints.equals(newPoints)) {
                        // nothing changed, no need to save
                        return true;
                    } else {
                        return updateRouteWithPointsChange(transaction, route.getNewGroupRoute());
                    }
                } else {
                    if (oldPoints.equals(newPoints)) {
                        // only the description changed
                        saveChanges(
                                transaction, newRouteName, route.getNewGroupRoute()
                        );
                        return true;
                    } else {
                        return updateRouteWithPointsChange(
                                transaction, route.getNewGroupRoute()
                        );
                    }
                }
            } else {
                if (oldDescription.equals(newDescription)) {
                    if (oldPoints.equals(newPoints)) {
                        return updateRouteWithNameChange(
                                transaction, oldRouteName, route.getNewGroupRoute()
                        );
                    } else {
                        return updateRouteWithNameAndPointsChange(
                                transaction, oldRouteName, route.getNewGroupRoute()
                        );
                    }
                } else {
                    if (oldPoints.equals(newPoints)) {
                        return updateRouteWithNameChange(
                                transaction, oldRouteName, route.getNewGroupRoute()
                        );
                    } else {
                        return updateRouteWithNameAndPointsChange(
                                transaction, oldRouteName, route.getNewGroupRoute()
                        );
                    }
                }
            }
        });
        return FutureUtil.handleFutureGet(transactionFuture::get);
    }

    private boolean updateRouteWithNameAndPointsChange(
            Transaction transaction,
            String oldRouteName,
            GroupRoute newGroupRoute
    ) {
        var handler = new GroupRouteUniquenessHandler(
                transaction,
                db,
                newGroupRoute.getGroupName(),
                newGroupRoute.getRouteName(),
                newGroupRoute.getPoints()
        );
        newGroupRoute.handleRouteUniqueness(handler);

        saveChanges(transaction, oldRouteName, newGroupRoute);
        return true;
    }

    private boolean updateRouteWithNameChange(
            Transaction transaction,
            String oldRouteName,
            GroupRoute newGroupRoute
    ) {
        var handler = new GroupRouteUniquenessHandler(
                transaction,
                db,
                newGroupRoute.getGroupName(),
                newGroupRoute.getRouteName(),
                newGroupRoute.getPoints()
        );


        if (RouteServiceUtils.routeNameExistsForOwner(
                transaction, db, newGroupRoute.getGroupName(), newGroupRoute.getRouteName(), DbPathConstants.ROUTE_GROUP_NAME)
        ) {
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(newGroupRoute.getRouteName())
            );
        } else {
            saveChanges(transaction, oldRouteName, newGroupRoute);
            return true;
        }
    }

    private boolean updateRouteWithPointsChange(
            Transaction transaction,
            GroupRoute groupRoute
    ) {
        if (RouteServiceUtils.arePointsUniqueForOwner(
                transaction, db, groupRoute.getGroupName(), groupRoute.getPoints(), DbPathConstants.ROUTE_GROUP_NAME)
        ) {
            saveChanges(transaction, groupRoute.getRouteName(), groupRoute);
            return true;
        } else {
            throw new IllegalArgumentException(RouteServiceUtils.POINTS_NOT_UNIQE);
        }
    }

    private void saveChanges(
            Transaction transaction,
            String oldRouteName,
            GroupRoute groupRoute
    ) {
        var query = getRouteQuery(
                groupRoute.getGroupName(), DbPathConstants.ROUTE_GROUP_NAME, oldRouteName
        );
        var queryFuture = transaction.get(query);

        var queryDocs = FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments()
        );
        Util.handleListSize(queryDocs, documentSnapshots -> {
            var docRef = getDocToUpdate(queryDocs);
            Map<String, Object> data = groupRoute.toMap();
            return transaction.set(docRef, data);
        });
    }

    private DocumentReference getDocToUpdate(List<QueryDocumentSnapshot> queryDocs) {
        String id = queryDocs.get(0).getId();
        return db.collection(DbPathConstants.COLLECTION_ROUTE)
                .document(id);
    }

    private Query getRouteQuery(
            String ownerName,
            String ownerPath,
            String oldRouteName
    ) {
        return db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(ownerPath, ownerName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, oldRouteName);
    }
}
