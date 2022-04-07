package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.EditedRoute;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class RouteEditService {

    @Autowired
    private Firestore db;

    // TODO: update javadoc
    /**
     * Edits the route.
     * @param route the edited route
     * @return true if the edited route is unique for the given user
     */
    public boolean editRoute(EditedRoute route) {
        String oldRouteName = route.getOldRoute().getRouteName();
        String newRouteName = route.getNewRoute().getRouteName();
        String oldDescription = route.getOldRoute().getDescription();
        String newDescription = route.getNewRoute().getDescription();
        List<Point> oldPoints = route.getOldRoute().getPoints();
        List<Point> newPoints = route.getNewRoute().getPoints();

        if (oldRouteName.equals(newRouteName)) {
            if (oldDescription.equals(newDescription)) {
                if (oldPoints.equals(newPoints)) {
                    // nothing changed, no need to save
                    return true;
                } else {
                    return updateUserRouteWithPointsChange(route.getNewRoute());
                }
            } else {
                if (oldPoints.equals(newPoints)) {
                    // only the description changed
                    saveChanges(newRouteName, route.getNewRoute());
                    return true;
                } else {
                    return updateUserRouteWithPointsChange(route.getNewRoute());
                }
            }
        } else {
            if (oldDescription.equals(newDescription)) {
                if (oldPoints.equals(newPoints)) {
                    return updateUserRouteWithNameChange(oldRouteName, route.getNewRoute());
                } else {
                    return updateUserRouteWithNameAndPointsChange(oldRouteName, route.getNewRoute());
                }
            } else {
                if (oldPoints.equals(newPoints)) {
                    return updateUserRouteWithNameChange(oldRouteName, route.getNewRoute());
                } else {
                    return updateUserRouteWithNameAndPointsChange(oldRouteName, route.getNewRoute());
                }
            }
        }
    }

    private boolean updateUserRouteWithNameAndPointsChange(String oldRouteName, Route route) {
        if (RouteServiceUtils.routeNameExists(
                db, route.getOwnerName(), route.getRouteName(), route.getRouteType()
        )) {
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(route.getRouteName())
            );
        } else {
            if (RouteServiceUtils.arePointsUnique(
                    db, route.getOwnerName(), route.getPoints(), route.getRouteType()
            )) {
                saveChanges(oldRouteName, route);
                return true;
            } else {
                throw new IllegalArgumentException(RouteServiceUtils.POINTS_NOT_UNIQE);
            }
        }
    }

    private boolean updateUserRouteWithNameChange(String oldRouteName, Route route) {
        if (RouteServiceUtils.routeNameExists(
                db, route.getOwnerName(), route.getRouteName(), route.getRouteType()
        )) {
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(route.getRouteName())
            );
        } else {
            saveChanges(oldRouteName, route);
            return true;
        }
    }

    private boolean updateUserRouteWithPointsChange(Route route) {
        if (RouteServiceUtils.arePointsUnique(
                db, route.getOwnerName(), route.getPoints(), route.getRouteType()
        )) {
            saveChanges(route.getRouteName(), route);
            return true;
        } else {
            throw new IllegalArgumentException(RouteServiceUtils.POINTS_NOT_UNIQE);
        }
    }

    private void saveChanges(String oldRouteName, Route route) {
        var querySnapshot = getRouteQuerySnapshot(route.getOwnerName(), oldRouteName);
        String id = querySnapshot.getDocuments().get(0).getId();
        try {
            Map<String, Object> data = route.toMap();
            db.collection(DbPathConstants.COLLECTION_ROUTE)
                    .document(id)
                    .set(data)
                    .get(); // wait for write result
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private QuerySnapshot getRouteQuerySnapshot(String userName, String routeName) {
        var routes = db.collection(DbPathConstants.COLLECTION_ROUTE);
        var queryFuture = routes
                .select(DbPathConstants.ROUTE_USER_NAME, DbPathConstants.ROUTE_NAME)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
            var querySnapshot = queryFuture.get();
            if (querySnapshot.isEmpty()) {
                throw new IllegalArgumentException(
                        "Nem létezik útvonal a következő felhasználó névvel: "
                                + userName + ", és útvonal névvel: " + routeName
                );
            } else {
                return querySnapshot;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Valami hiba történt.");
    }
}