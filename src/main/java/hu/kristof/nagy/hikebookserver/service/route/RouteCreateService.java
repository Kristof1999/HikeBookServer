package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class RouteCreateService {

    @Autowired
    private Firestore db;

    /**
     * Before saving the route, checks if the route is unique:
     * the owner has no route called routeName,
     * and no route whose points are the same as the points argument.
     * @param route the created route
     * @return true if route is unique
     */
    public boolean createRoute(Route route) {
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
                Map<String, Object> data = route.toMap();
                try {
                    db.collection(DbPathConstants.COLLECTION_ROUTE)
                            .add(data)
                            .get(); // wait for write result
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                throw new IllegalArgumentException(RouteServiceUtils.POINTS_NOT_UNIQE);
            }
        }
    }
}
