package hu.kristof.nagy.hikebookserver.service.route.userroute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.RouteServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserRouteCreateService {

    @Autowired
    private Firestore db;

    /**
     * Before saving the route, checks if the route is unique:
     * the owner has no route called routeName,
     * and no route whose points are the same as the points argument.
     * @param route the created route
     * @return true if route is unique
     */
    public boolean createUserRoute(UserRoute route) {
        if (RouteServiceUtils.routeNameExistsForOwner(
                db, route.getUserName(), route.getRouteName(), DbPathConstants.ROUTE_USER_NAME)
        ) {
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(route.getRouteName())
            );
        } else {
            if (RouteServiceUtils.arePointsUniqueForOwner(
                    db, route.getUserName(), route.getPoints(), DbPathConstants.ROUTE_USER_NAME)
            ) {
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
