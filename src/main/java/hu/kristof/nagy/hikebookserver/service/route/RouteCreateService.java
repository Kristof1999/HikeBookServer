package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.User;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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
    public boolean createRoute(Route route, String ownerName, String ownerPath) {
        // TODO: handle group route create with transactions
        if (RouteServiceUtils.routeNameExists(
                db, ownerName, route.getRouteName(), ownerPath
        )) {
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(route.getRouteName())
            );
        } else {
            if (RouteServiceUtils.arePointsUnique(
                    db, ownerName, route.getPoints(), ownerPath
            )) {
                Map<String, Object> data = route.toMap();
                FutureUtil.handleFutureGet(() ->
                        db.collection(DbPathConstants.COLLECTION_ROUTE)
                                .add(data)
                                .get() // wait for write result
                );
                return true;
            } else {
                throw new IllegalArgumentException(RouteServiceUtils.POINTS_NOT_UNIQE);
            }
        }
    }
}
