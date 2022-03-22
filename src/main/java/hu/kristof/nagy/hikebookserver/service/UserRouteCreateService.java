package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserRouteCreateService {

    @Autowired
    private Firestore db;

    /**
     * Before saving the route, checks if the route is unique:
     * the user has no route called routeName,
     * and no route whose points are the same as the points argument.
     * @param route the created route
     * @return true if route is unique
     */
    public boolean createUserRoute(UserRoute route) {
        CollectionReference routes = db
                .collection(DbPathConstants.COLLECTION_ROUTE);
        Query query = routes
                .select(DbPathConstants.ROUTE_USER_NAME,
                        DbPathConstants.ROUTE_NAME)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, route.getUserName())
                .whereEqualTo(DbPathConstants.ROUTE_NAME, route.getRouteName());
        ApiFuture<QuerySnapshot> future = query.get();
        try {
            if (future.get().isEmpty()) {
                // route name is unique
                query = query
                        .select(DbPathConstants.ROUTE_POINTS)
                        .whereEqualTo(DbPathConstants.ROUTE_NAME, route.getRouteName());
                if (query.get().get().isEmpty()) {
                    // route is unique
                    Map<String, Object> data = route.toMap();
                    db.collection(DbPathConstants.COLLECTION_ROUTE)
                            .add(data)
                            .get(); // wait for write result
                    return true;
                } else {
                    return false; // route exists with a different name
                }
            } else {
                return false;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
