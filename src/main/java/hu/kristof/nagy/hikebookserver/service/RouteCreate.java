package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.CloudDatabase;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RouteCreate {

    @Autowired
    private CloudDatabase db;

    /**
     * Before saving the route, checks if the user has no route called routeName,
     * and no route whose points are the same as the points argument.
     * @param userName name of user who created the route
     * @param routeName name of the created route
     * @param points points of the created route
     * @return true if save is successful
     */
    public boolean createRoute(String userName, String routeName, List<Point> points) {
        ApiFuture<QuerySnapshot> future = db.getDb()
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_USER_NAME,
                        DbPathConstants.ROUTE_NAME,
                        DbPathConstants.ROUTE_POINTS)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .whereEqualTo(DbPathConstants.ROUTE_POINTS, points)
                .get();
        try {
            if (future.get().isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put(DbPathConstants.ROUTE_USER_NAME, userName);
                data.put(DbPathConstants.ROUTE_NAME, routeName);
                data.put(DbPathConstants.ROUTE_POINTS, points);
                db.getDb()
                        .collection(DbPathConstants.COLLECTION_ROUTE)
                        .add(data);
                return true;
            } else {
                return false;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
