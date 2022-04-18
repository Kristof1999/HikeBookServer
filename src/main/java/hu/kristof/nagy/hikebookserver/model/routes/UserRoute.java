package hu.kristof.nagy.hikebookserver.model.routes;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.SimpleRouteUniquenessHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class UserRoute extends Route {
    private String userName;

    public UserRoute() {
    }

    public UserRoute(Route route, String userName) {
        this(route.getRouteName(), route.getPoints(), route.getDescription(), userName);
    }

    public UserRoute(String routeName, List<Point> points, String description, String userName) {
        super(routeName, points, description);
        this.userName = userName;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> data = super.toMap();
        data.put(DbPathConstants.ROUTE_USER_NAME, userName);
        return data;
    }

    public static UserRoute from(DocumentSnapshot documentSnapshot) {
        return Objects.requireNonNull(
                documentSnapshot.toObject(UserRoute.class)
        );
    }

    public static String[] getSelectPaths() {
        String[] routeSelectPaths = Route.getSelectPaths();
        String[] userRouteSelectPaths = Arrays.copyOf(
                routeSelectPaths, routeSelectPaths.length + 1
        );
        userRouteSelectPaths[userRouteSelectPaths.length - 1] = DbPathConstants.ROUTE_USER_NAME;
        return userRouteSelectPaths;
    }

    public void handleRouteUniqueness(Firestore db) {
        var handler = new SimpleRouteUniquenessHandler(
                db,
                userName,
                DbPathConstants.ROUTE_USER_NAME,
                routeName,
                points
        );
        handler.handleRouteUniqueness();
    }

    public void handleRouteNameUniqueness(Firestore db) {
        var handler = new SimpleRouteUniquenessHandler(
                db,
                userName,
                DbPathConstants.ROUTE_USER_NAME,
                routeName,
                points
        );
        handler.handleRouteNameUniqueness();
    }

    public void handlePointsUniqueness(Firestore db) {
        var handler = new SimpleRouteUniquenessHandler(
                db,
                userName,
                DbPathConstants.ROUTE_USER_NAME,
                routeName,
                points
        );
        handler.handlePointsUniqueness();
    }

    public String getUserName() {
        return userName;
    }
}
