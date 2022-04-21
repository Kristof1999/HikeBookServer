package hu.kristof.nagy.hikebookserver.service.route.grouproute;

import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.EditedGroupRoute;
import hu.kristof.nagy.hikebookserver.model.routes.EditedRoute;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.RouteEdit;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.TransactionRouteUniquenessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GroupRouteEditService implements RouteEdit {
    @Autowired
    private Firestore db;

    @Autowired
    private GroupRouteLoadService groupRouteLoadService;

    /**
     * Edits the route. If something changes, then it must be unique for the user.
     * If the route got deleted while editing, then an
     * error message will inform the user.
     * @return true if the edited route is unique for the given user
     */
    @Override
    public ResponseResult<Boolean> editRoute(EditedRoute route) {
        String oldRouteName = route.getOldRoute().getRouteName();
        String newRouteName = route.getNewRoute().getRouteName();
        String oldDescription = route.getOldRoute().getDescription();
        String newDescription = route.getNewRoute().getDescription();
        List<Point> oldPoints = route.getOldRoute().getPoints();
        List<Point> newPoints = route.getNewRoute().getPoints();
        GroupRoute newGroupRoute = ((EditedGroupRoute) route).getNewGroupRoute();

        var transactionFuture = db.runTransaction(transaction -> {
            // check if old route didn't get deleted
            if (!doesGroupRouteExist(transaction, newGroupRoute.getGroupName(), oldRouteName)) {
                throw new IllegalArgumentException("A(z) " + oldRouteName + " nevű" +
                        "útvonal nem elérhető. Kérem, frissítse a csoport oldalt."
                );
            }

            if (oldRouteName.equals(newRouteName)) {
                if (oldDescription.equals(newDescription)) {
                    if (oldPoints.equals(newPoints)) {
                        // nothing changed, no need to save
                        return true;
                    } else {
                        return updateRouteWithPointsChange(transaction, newGroupRoute);
                    }
                } else {
                    if (oldPoints.equals(newPoints)) {
                        // only the description changed
                        saveChanges(
                                transaction, newRouteName, newGroupRoute
                        );
                        return true;
                    } else {
                        return updateRouteWithPointsChange(
                                transaction, newGroupRoute
                        );
                    }
                }
            } else {
                if (oldDescription.equals(newDescription)) {
                    if (oldPoints.equals(newPoints)) {
                        return updateRouteWithNameChange(
                                transaction, oldRouteName, newGroupRoute
                        );
                    } else {
                        return updateRouteWithNameAndPointsChange(
                                transaction, oldRouteName, newGroupRoute
                        );
                    }
                } else {
                    if (oldPoints.equals(newPoints)) {
                        return updateRouteWithNameChange(
                                transaction, oldRouteName, newGroupRoute
                        );
                    } else {
                        return updateRouteWithNameAndPointsChange(
                                transaction, oldRouteName, newGroupRoute
                        );
                    }
                }
            }
        });
        return ResponseResult.success(FutureUtil.handleFutureGet(transactionFuture::get));
    }

    private boolean doesGroupRouteExist(
            Transaction transaction,
            String groupName,
            String routeName
    ) {
        var query = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(DbPathConstants.ROUTE_GROUP_NAME, groupName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName);
        var queryFuture = transaction.get(query);

        return FutureUtil.handleFutureGet(() -> {
            var queryDocs = queryFuture.get().getDocuments();
            // the route might have been deleted when the user wants to load it
            return !queryDocs.isEmpty();
        });
    }

    private boolean updateRouteWithNameAndPointsChange(
            Transaction transaction,
            String oldRouteName,
            GroupRoute newRoute
    ) {
        newRoute.handleRouteUniqueness(new TransactionRouteUniquenessHandler
                .Builder(db, transaction)
        );

        saveChanges(transaction, oldRouteName, newRoute);
        return true;
    }

    private boolean updateRouteWithNameChange(
            Transaction transaction,
            String oldRouteName,
            GroupRoute newRoute
    ) {
        newRoute.handleRouteNameUniqueness(new TransactionRouteUniquenessHandler
                .Builder(db, transaction)
        );

        saveChanges(transaction, oldRouteName, newRoute);
        return true;
    }

    private boolean updateRouteWithPointsChange(
            Transaction transaction,
            GroupRoute newRoute
    ) {
        newRoute.handleRoutePointsUniqueness(new TransactionRouteUniquenessHandler
                .Builder(db, transaction)
        );

        saveChanges(transaction, newRoute.getRouteName(), newRoute);
        return true;
    }

    private void saveChanges(
            Transaction transaction,
            String oldRouteName,
            GroupRoute newRoute
    ) {
        var query = getRouteQuery(
                newRoute.getGroupName(),
                oldRouteName
        );
        var queryFuture = transaction.get(query);

        var queryDocs = FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments()
        );
        Util.handleListSize(queryDocs, documentSnapshots -> {
            var docRef = getDocToUpdate(queryDocs);
            Map<String, Object> data = newRoute.toMap();
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
            String oldRouteName
    ) {
        return db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(DbPathConstants.ROUTE_GROUP_NAME, ownerName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, oldRouteName);
    }
}
