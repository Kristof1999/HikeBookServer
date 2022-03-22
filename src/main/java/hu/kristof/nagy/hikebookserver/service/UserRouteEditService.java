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
     * @param oldRouteName name of route before editing
     * @param route the edited route
     * @return true if the edited route is unique for the given user
     */
    public boolean editUserRoute(String oldRouteName, UserRoute route) {
        // szerepkörök miatt érdemes a userName-t hagyni argumentumként, mivel
        // így a jövőben könnyen megnézhetjük, hogy az adott user-nak van-e engedélye
        // szerkeszteni csoport útvonalat
        if (!oldRouteName.equals(route.getRouteName())) {
            return updateUserRouteWithNameChange(oldRouteName, route);
        } else {
            return updateUserRoute(oldRouteName, route);
        }
    }

    private boolean updateUserRouteWithNameChange(String oldRouteName, UserRoute route) {
        if (routeNameExistsForUser(route.getUserName(), route.getRouteName())) {
            return false;
        } else {
            saveAndWait(oldRouteName, route);
            return true;
        }
    }

    private boolean updateUserRoute(String oldRouteName, UserRoute route) {
        if (arePointsUniqueForUser(route.getUserName(), route.getPoints())) {
            saveAndWait(oldRouteName, route);
            return true;
        } else {
            return false;
        }
    }

    private void saveAndWait(String oldRouteName, UserRoute route) {
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
                throw new IllegalArgumentException("No route exists with userName: " + userName + ", and routeName: " + routeName);
            } else {
                return querySnapshot;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Something went wrong.");
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
