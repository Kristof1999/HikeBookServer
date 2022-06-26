package hu.kristof.nagy.hikebookserver.service.route.userroute;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.EditedUserRoute;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.SimpleRouteUniquenessHandler;

import java.util.List;
import java.util.Map;

public class UserRouteEditService {
    public static ResponseResult<Boolean> editRoute(Firestore db, EditedUserRoute route) {
        String oldRouteName = route.getOldRoute().getRouteName();
        String newRouteName = route.getNewRoute().getRouteName();
        List<Point> oldPoints = route.getOldRoute().getPoints();
        List<Point> newPoints = route.getNewRoute().getPoints();
        String ownerName = route.getNewUserRoute().getUserName();
        UserRoute userRoute = route.getNewUserRoute();

        var uniquenessHandlerBuilder = new SimpleRouteUniquenessHandler.Builder(db);
        if (!oldRouteName.equals(newRouteName)) {
            userRoute.handleRouteNameUniqueness(uniquenessHandlerBuilder);
        }
        if (!oldPoints.equals(newPoints)) {
            userRoute.handleRoutePointsUniqueness(uniquenessHandlerBuilder);
        }
        saveChanges(db, ownerName, oldRouteName, userRoute);
        return ResponseResult.success(true);
    }

    private static void saveChanges(
            Firestore db,
            String ownerName,
            String oldRouteName,
            UserRoute newRoute
    ) {
        var queryFuture = getRouteQuery(db, ownerName, oldRouteName).get();
        var querySnapshot = FutureUtil.handleFutureGet(queryFuture::get);

        var queryDocs = querySnapshot.getDocuments();
        Util.handleListSize(queryDocs, documentSnapshots -> {
            var docRef = getDocToUpdate(db, documentSnapshots);
            Map<String, Object> data = newRoute.toMap();
            return FutureUtil.handleFutureGet(() ->
                    docRef.set(data)
                            .get() // wait for write result
            );
        });
    }

    private static DocumentReference getDocToUpdate(Firestore db, List<? extends DocumentSnapshot> queryDocs) {
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
                .whereEqualTo(DbFields.UserRoute.NAME, ownerName)
                .whereEqualTo(DbFields.Route.ROUTE_NAME, oldRouteName);
    }
}
