package hu.kristof.nagy.hikebookserver.model.routes;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.RouteUniquenessHandler;

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
        data.put(DbFields.UserRoute.NAME, userName);
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
        userRouteSelectPaths[userRouteSelectPaths.length - 1] = DbFields.UserRoute.NAME;
        return userRouteSelectPaths;
    }

    @Override
    public void handleRouteUniqueness(RouteUniquenessHandler.Builder<?> builder) {
        super.handleRouteUniqueness(
                builder.setOwnerName(userName)
                        .setOwnerPath(DbFields.UserRoute.NAME)
        );
    }

    @Override
    public void handleRoutePointsUniqueness(RouteUniquenessHandler.Builder<?> builder) {
        super.handleRoutePointsUniqueness(
                builder.setOwnerName(userName)
                        .setOwnerPath(DbFields.UserRoute.NAME)
        );
    }

    @Override
    public void handleRouteNameUniqueness(RouteUniquenessHandler.Builder<?> builder) {
        super.handleRouteNameUniqueness(
                builder.setOwnerName(userName)
                        .setOwnerPath(DbFields.UserRoute.NAME)
        );
    }

    public String getUserName() {
        return userName;
    }
}
