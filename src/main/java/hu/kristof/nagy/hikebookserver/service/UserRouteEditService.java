package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
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

    /**
     * Edits the route.
     * @param userName name of user who requested editing
     * @param oldRouteName name of route before editing
     * @param route the edited route
     * @return true if the edited route is unique for the given user
     */
    public boolean editUserRoute(String userName, String oldRouteName, UserRoute route) {
        // szerepkörök miatt érdemes a userName-t hagyni argumentumként, mivel
        // így a jövőben könnyen megnézhetjük, hogy az adott user-nak van-e engedélye
        // szerkeszteni csoport útvonalat
        if (!oldRouteName.equals(route.getRouteName())) {
            // route name changed
            if (routeNameExistsForUser(userName, route.getRouteName())) {
                return false;
            } else {
                return updateUserRoute(userName, oldRouteName, route);
            }
        } else {
            return updateUserRoute(userName, oldRouteName, route);
        }
    }

    private boolean updateUserRoute(String userName, String routeName, UserRoute route) {
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
                // editing a non-existent route
                return false;
            } else {
                if (arePointsUniqueForUser(userName, route.getPoints())) {
                    String id = querySnapshot.getDocuments().get(0).getId();

                    Map<String, Object> data = route.toMap();
                    routes.document(id)
                            .set(data)
                            .get(); // wait for write result
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean arePointsUniqueForUser(String userName, List<Point> points) {
        ApiFuture<QuerySnapshot> future = db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_POINTS, points)
                .get();
        try {
            if (future.get().isEmpty())
                return true;
            else
                return false;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean routeNameExistsForUser(String userName, String routeName) {
        ApiFuture<QuerySnapshot> future = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_USER_NAME,
                        DbPathConstants.ROUTE_NAME)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
           if (future.get().isEmpty())
               return false;
           else
               return true;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
