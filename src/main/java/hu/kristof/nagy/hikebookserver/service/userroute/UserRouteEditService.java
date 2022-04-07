package hu.kristof.nagy.hikebookserver.service.userroute;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.EditedUserRoute;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserRouteEditService {

    @Autowired
    private Firestore db;

    // TODO: update javadoc
    /**
     * Edits the route.
     * @param route the edited route
     * @return true if the edited route is unique for the given user
     */
    public boolean editUserRoute(EditedUserRoute route) {
        String oldRouteName = route.getOldUserRoute().getRouteName();
        String newRouteName = route.getNewUserRoute().getRouteName();
        String oldDescription = route.getOldUserRoute().getDescription();
        String newDescription = route.getNewUserRoute().getDescription();
        List<Point> oldPoints = route.getOldUserRoute().getPoints();
        List<Point> newPoints = route.getNewUserRoute().getPoints();

        if (oldRouteName.equals(newRouteName)) {
            if (oldDescription.equals(newDescription)) {
                if (oldPoints.equals(newPoints)) {
                    // nothing changed, no need to save
                    return true;
                } else {
                    return updateUserRouteWithPointsChange(route.getNewUserRoute());
                }
            } else {
                if (oldPoints.equals(newPoints)) {
                    // only the description changed
                    saveChanges(newRouteName, route.getNewUserRoute());
                    return true;
                } else {
                    return updateUserRouteWithPointsChange(route.getNewUserRoute());
                }
            }
        } else {
            if (oldDescription.equals(newDescription)) {
                if (oldPoints.equals(newPoints)) {
                    return updateUserRouteWithNameChange(oldRouteName, route.getNewUserRoute());
                } else {
                    return updateUserRouteWithNameAndPointsChange(oldRouteName, route.getNewUserRoute());
                }
            } else {
                if (oldPoints.equals(newPoints)) {
                    return updateUserRouteWithNameChange(oldRouteName, route.getNewUserRoute());
                } else {
                    return updateUserRouteWithNameAndPointsChange(oldRouteName, route.getNewUserRoute());
                }
            }
        }
    }

    private boolean updateUserRouteWithNameAndPointsChange(String oldRouteName, UserRoute route) {
        if (UserRouteServiceUtils.routeNameExistsForUser(db, route.getUserName(), route.getRouteName())) {
            throw new IllegalArgumentException(
                    UserRouteServiceUtils.getRouteNameNotUniqueForUserString(route.getRouteName())
            );
        } else {
            if (UserRouteServiceUtils.arePointsUniqueForUser(db, route.getUserName(), route.getPoints())) {
                saveChanges(oldRouteName, route);
                return true;
            } else {
                throw new IllegalArgumentException(UserRouteServiceUtils.POINTS_NOT_UNIQE);
            }
        }
    }

    private boolean updateUserRouteWithNameChange(String oldRouteName, UserRoute route) {
        if (UserRouteServiceUtils.routeNameExistsForUser(db, route.getUserName(), route.getRouteName())) {
            throw new IllegalArgumentException(
                    UserRouteServiceUtils.getRouteNameNotUniqueForUserString(route.getRouteName())
            );
        } else {
            saveChanges(oldRouteName, route);
            return true;
        }
    }

    private boolean updateUserRouteWithPointsChange(UserRoute route) {
        if (UserRouteServiceUtils.arePointsUniqueForUser(db, route.getUserName(), route.getPoints())) {
            saveChanges(route.getRouteName(), route);
            return true;
        } else {
            throw new IllegalArgumentException(UserRouteServiceUtils.POINTS_NOT_UNIQE);
        }
    }

    private void saveChanges(String oldRouteName, UserRoute route) {
        var querySnapshot = getRouteQuerySnapshot(route.getUserName(), oldRouteName);
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
