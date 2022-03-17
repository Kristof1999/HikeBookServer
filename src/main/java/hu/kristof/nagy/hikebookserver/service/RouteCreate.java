package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RouteCreate {

    @Autowired
    private Firestore db;

    /**
     * Before saving the route, checks if the route is unique:
     * the user has no route called routeName,
     * and no route whose points are the same as the points argument.
     * @param userName name of user who created the route
     * @param routeName name of the created route
     * @param points points of the created route
     * @return true if route is unique
     */
    public boolean createRoute(String userName, String routeName, List<Point> points) {
        CollectionReference routes = db
                .collection(DbPathConstants.COLLECTION_ROUTE);
        Query query = routes
                .select(DbPathConstants.ROUTE_USER_NAME,
                        DbPathConstants.ROUTE_NAME)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName);
        ApiFuture<QuerySnapshot> future = query.get();
        try {
            if (future.get().isEmpty()) {
                // TODO: test if this multiple querying works
                query = query
                        .select(DbPathConstants.ROUTE_POINTS)
                        .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName);
                if (query.get().get().isEmpty()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put(DbPathConstants.ROUTE_USER_NAME, userName);
                    data.put(DbPathConstants.ROUTE_NAME, routeName);
                    data.put(DbPathConstants.ROUTE_POINTS, points);
                    db.collection(DbPathConstants.COLLECTION_ROUTE)
                            .add(data);
                    return true;
                } else {
                    return false; //már van ilyen (más nevű) út
                }
            } else {
                return false; //név nem egyedi
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
