package hu.kristof.nagy.hikebookserver.service.route.userroute;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.EditedRoute;
import hu.kristof.nagy.hikebookserver.model.routes.EditedUserRoute;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.SimpleRouteUniquenessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserRouteEditService {

    @Autowired
    private Firestore db;

    public ResponseResult<Boolean> editRoute(EditedUserRoute route) {
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
        saveChanges(ownerName, oldRouteName, userRoute);
        return ResponseResult.success(true);
    }

    private void saveChanges(
            String ownerName,
            String oldRouteName,
            UserRoute newRoute
    ) {
        var queryFuture = getRouteQuery(ownerName, oldRouteName).get();
        var querySnapshot = FutureUtil.handleFutureGet(queryFuture::get);

        var queryDocs = querySnapshot.getDocuments();
        Util.handleListSize(queryDocs, documentSnapshots -> {
            var docRef = getDocToUpdate(documentSnapshots);
            Map<String, Object> data = newRoute.toMap();
            return FutureUtil.handleFutureGet(() ->
                    docRef.set(data)
                            .get() // wait for write result
            );
        });
    }

    private DocumentReference getDocToUpdate(List<? extends DocumentSnapshot> queryDocs) {
        String id = queryDocs.get(0).getId();
        return db.collection(DbPathConstants.COLLECTION_ROUTE)
                .document(id);
    }

    private Query getRouteQuery(
            String ownerName,
            String oldRouteName
    ) {
        return db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, ownerName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, oldRouteName);
    }
}
