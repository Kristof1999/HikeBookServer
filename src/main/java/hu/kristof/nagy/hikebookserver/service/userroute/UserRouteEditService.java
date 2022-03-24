package hu.kristof.nagy.hikebookserver.service.userroute;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserRouteEditService {

    @Autowired
    private Firestore db;

    /**
     * Edits the route.
     * @param oldRouteName name of route before editing
     * @param route the edited route
     * @return true if the edited route is unique for the given user
     */
    public boolean editUserRoute(String oldRouteName, UserRoute route) {
        if (!oldRouteName.equals(route.getRouteName())) {
            return updateUserRouteWithNameChange(oldRouteName, route);
        } else {
            return updateUserRoute(route);
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

    private boolean updateUserRoute(UserRoute route) {
        if (UserRouteServiceUtils.arePointsUniqueForUser(db, route.getUserName(), route.getPoints())) {
            saveChanges(route.getRouteName(), route);
            return true;
        } else {
            throw new IllegalArgumentException(UserRouteServiceUtils.POINTS_NOT_UNIQE);
        }
    }

    private void saveChanges(String oldRouteName, UserRoute route) {
        QuerySnapshot querySnapshot = getRouteQuerySnapshot(route.getUserName(), oldRouteName);
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
        CollectionReference routes = db
                .collection(DbPathConstants.COLLECTION_ROUTE);
        ApiFuture<QuerySnapshot> future = routes
                .select(DbPathConstants.ROUTE_USER_NAME,
                        DbPathConstants.ROUTE_NAME)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
            QuerySnapshot querySnapshot = future.get();
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
