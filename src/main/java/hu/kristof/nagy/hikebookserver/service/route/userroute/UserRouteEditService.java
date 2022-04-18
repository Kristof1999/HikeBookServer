package hu.kristof.nagy.hikebookserver.service.route.userroute;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.routes.EditedRoute;
import hu.kristof.nagy.hikebookserver.model.routes.EditedUserRoute;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.RouteEdit;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.SimpleRouteUniquenessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserRouteEditService implements RouteEdit {

    @Autowired
    private Firestore db;

    @Override
    public boolean editRoute(EditedRoute route) {
        String oldRouteName = route.getOldRoute().getRouteName();
        String newRouteName = route.getNewRoute().getRouteName();
        String oldDescription = route.getOldRoute().getDescription();
        String newDescription = route.getNewRoute().getDescription();
        List<Point> oldPoints = route.getOldRoute().getPoints();
        List<Point> newPoints = route.getNewRoute().getPoints();
        String ownerName = ((EditedUserRoute) route).getNewUserRoute().getUserName();
        UserRoute userRoute = ((EditedUserRoute) route).getNewUserRoute();

        if (oldRouteName.equals(newRouteName)) {
            if (oldDescription.equals(newDescription)) {
                if (oldPoints.equals(newPoints)) {
                    // nothing changed, no need to save
                    return true;
                } else {
                    return updateRouteWithPointsChange(ownerName, userRoute);
                }
            } else {
                if (oldPoints.equals(newPoints)) {
                    // only the description changed
                    saveChanges(ownerName, newRouteName, userRoute);
                    return true;
                } else {
                    return updateRouteWithPointsChange(ownerName, userRoute);
                }
            }
        } else {
            if (oldDescription.equals(newDescription)) {
                if (oldPoints.equals(newPoints)) {
                    return updateRouteWithNameChange(ownerName, oldRouteName, userRoute);
                } else {
                    return updateRouteWithNameAndPointsChange(ownerName, oldRouteName, userRoute);
                }
            } else {
                if (oldPoints.equals(newPoints)) {
                    return updateRouteWithNameChange(ownerName, oldRouteName, userRoute);
                } else {
                    return updateRouteWithNameAndPointsChange(ownerName, oldRouteName, userRoute);
                }
            }
        }
    }

    private boolean updateRouteWithNameAndPointsChange(
            String ownerName,
            String oldRouteName,
            UserRoute newRoute
    ) {
        newRoute.handleRouteUniqueness(new SimpleRouteUniquenessHandler
                .Builder(db)
        );

        saveChanges(ownerName, oldRouteName, newRoute);
        return true;
    }

    private boolean updateRouteWithNameChange(
            String ownerName,
            String oldRouteName,
            UserRoute newRoute
    ) {
        newRoute.handleRouteNameUniqueness(new SimpleRouteUniquenessHandler
                .Builder(db)
        );

        saveChanges(ownerName, oldRouteName, newRoute);
        return true;
    }

    private boolean updateRouteWithPointsChange(
            String ownerName,
            UserRoute newRoute
    ) {
        newRoute.handleRoutePointsUniqueness(new SimpleRouteUniquenessHandler
                .Builder(db)
        );

        saveChanges(ownerName, newRoute.getRouteName(), newRoute);
        return true;
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
