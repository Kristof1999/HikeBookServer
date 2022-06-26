package hu.kristof.nagy.hikebookserver.model.routes;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.RouteUniquenessHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GroupRoute extends Route {
    private String groupName;

    public GroupRoute() {
    }

    public GroupRoute(Route route, String groupName) {
        this(route.getRouteName(), route.getPoints(), route.getDescription(), groupName);
    }

    public GroupRoute(String routeName, List<Point> points, String description, String groupName) {
        super(routeName, points, description);
        this.groupName = groupName;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> data = super.toMap();
        data.put(DbFields.GroupRoute.NAME, groupName);
        return data;
    }

    public static GroupRoute from(DocumentSnapshot documentSnapshot) {
        return Objects.requireNonNull(
                documentSnapshot.toObject(GroupRoute.class)
        );
    }

    public static String[] getSelectPaths() {
        String[] routeSelectPaths = Route.getSelectPaths();
        String[] groupRouteSelectPaths = Arrays.copyOf(
                routeSelectPaths, routeSelectPaths.length + 1
        );
        groupRouteSelectPaths[groupRouteSelectPaths.length - 1] = DbFields.GroupRoute.NAME;
        return groupRouteSelectPaths;
    }

    @Override
    public void handleRouteUniqueness(RouteUniquenessHandler.Builder<?> builder) {
        super.handleRouteUniqueness(
                builder.setOwnerName(groupName)
                        .setOwnerPath(DbFields.GroupRoute.NAME)
        );
    }

    @Override
    public void handleRoutePointsUniqueness(RouteUniquenessHandler.Builder<?> builder) {
        super.handleRoutePointsUniqueness(
                builder.setOwnerName(groupName)
                        .setOwnerPath(DbFields.GroupRoute.NAME)
        );
    }

    @Override
    public void handleRouteNameUniqueness(RouteUniquenessHandler.Builder<?> builder) {
        super.handleRouteNameUniqueness(
                builder.setOwnerName(groupName)
                        .setOwnerPath(DbFields.GroupRoute.NAME)
        );
    }

    public String getGroupName() {
        return groupName;
    }
}
