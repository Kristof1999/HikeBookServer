package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RouteEdit {
    @Autowired
    private Firestore db;

    public boolean editRoute(String userName, String oldRouteName, Route route) {
        if (!oldRouteName.equals(route.getRouteName())) {
            if (routeNameExists(userName, route.getRouteName())) {
                return false;
            } else {
                return updateRoute(userName, oldRouteName, route);
            }
        } else {
            return updateRoute(userName, oldRouteName, route);
        }
    }

    private boolean updateRoute(String userName, String routeName, Route route) {
        ApiFuture<QuerySnapshot> future = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_USER_NAME,
                        DbPathConstants.ROUTE_NAME)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
            String id = future.get().getDocuments().get(0).getId();
            Map<String, Object> data = new HashMap<>();
            data.put(DbPathConstants.ROUTE_USER_NAME, userName);
            data.put(DbPathConstants.ROUTE_NAME, route.getRouteName());
            data.put(DbPathConstants.ROUTE_POINTS, route.getPoints());
            db.collection(DbPathConstants.COLLECTION_ROUTE)
                    .document(id)
                    .set(data);
            return true;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean routeNameExists(String userName, String routeName) {
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
