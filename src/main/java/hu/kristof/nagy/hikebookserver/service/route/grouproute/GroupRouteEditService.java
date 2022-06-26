package hu.kristof.nagy.hikebookserver.service.route.grouproute;

import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.EditedGroupRoute;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.TransactionRouteUniquenessHandler;

import java.util.List;
import java.util.Map;

public class GroupRouteEditService {
    /**
     * Edits the route. If something changes, then it must be unique for the user.
     * If the route got deleted while editing, then an
     * error message will inform the user.
     * @return true if the edited route is unique for the given user
     */
    public static ResponseResult<Boolean> editRoute(Firestore db, EditedGroupRoute route) {
        String oldRouteName = route.getOldRoute().getRouteName();
        String newRouteName = route.getNewRoute().getRouteName();
        List<Point> oldPoints = route.getOldRoute().getPoints();
        List<Point> newPoints = route.getNewRoute().getPoints();
        GroupRoute newGroupRoute =  route.getNewGroupRoute();

        var transactionFuture = db.runTransaction(transaction -> {
            // check if old route didn't get deleted
            if (!doesGroupRouteExist(db, transaction, newGroupRoute.getGroupName(), oldRouteName)) {
                throw new IllegalArgumentException("A(z) " + oldRouteName + " nevű" +
                        "útvonal nem elérhető. Kérem, frissítse a csoport oldalt."
                );
            }

            var uniquenessHandlerBuilder = new TransactionRouteUniquenessHandler.Builder(db, transaction);
            if (!oldRouteName.equals(newRouteName)) {
                newGroupRoute.handleRouteNameUniqueness(uniquenessHandlerBuilder);
            }
            if (!oldPoints.equals(newPoints)) {
                newGroupRoute.handleRoutePointsUniqueness(uniquenessHandlerBuilder);
            }
            saveChanges(db, transaction, oldRouteName, newGroupRoute);
            return true;
        });
        return ResponseResult.success(FutureUtil.handleFutureGet(transactionFuture::get));
    }

    private static boolean doesGroupRouteExist(
            Firestore db,
            Transaction transaction,
            String groupName,
            String routeName
    ) {
        var query = db
                .collection(DbCollections.ROUTE)
                .whereEqualTo(DbFields.GroupRoute.NAME, groupName)
                .whereEqualTo(DbFields.Route.ROUTE_NAME, routeName);
        var queryFuture = transaction.get(query);

        return FutureUtil.handleFutureGet(() -> {
            var queryDocs = queryFuture.get().getDocuments();
            // the route might have been deleted when the user wants to load it
            return !queryDocs.isEmpty();
        });
    }

    private static void saveChanges(
            Firestore db,
            Transaction transaction,
            String oldRouteName,
            GroupRoute newRoute
    ) {
        var query = getRouteQuery(
                db,
                newRoute.getGroupName(),
                oldRouteName
        );
        var queryFuture = transaction.get(query);

        var queryDocs = FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments()
        );
        Util.handleListSize(queryDocs, documentSnapshots -> {
            var docRef = getDocToUpdate(db, queryDocs);
            Map<String, Object> data = newRoute.toMap();
            return transaction.set(docRef, data);
        });
    }

    private static DocumentReference getDocToUpdate(Firestore db, List<QueryDocumentSnapshot> queryDocs) {
        String id = queryDocs.get(0).getId();
        return db.collection(DbCollections.ROUTE)
                .document(id);
    }

    private static Query getRouteQuery(
            Firestore db,
            String ownerName,
            String oldRouteName
    ) {
        return db.collection(DbCollections.ROUTE)
                .whereEqualTo(DbFields.GroupRoute.NAME, ownerName)
                .whereEqualTo(DbFields.Route.ROUTE_NAME, oldRouteName);
    }
}
