package hu.kristof.nagy.hikebookserver.model.routes;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.RouteUniquenessHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupHikeRoute extends Route {
    private String groupHikeName;

    public GroupHikeRoute() {
    }

    public GroupHikeRoute(Route route, String groupHikeName) {
        this(route.getRouteName(), route.getPoints(), route.getDescription(), groupHikeName);
    }

    public GroupHikeRoute(String routeName, List<Point> points, String description, String groupHikeName) {
        super(routeName, points, description);
        this.groupHikeName = groupHikeName;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> data = super.toMap();
        data.put(DbFields.GroupHikeRoute.NAME, groupHikeName);
        return data;
    }

    public static GroupHikeRoute from(DocumentSnapshot documentSnapshot) {
        return Objects.requireNonNull(
                documentSnapshot.toObject(GroupHikeRoute.class)
        );
    }

    public static String[] getSelectPaths() {
        String[] routeSelectPaths = Route.getSelectPaths();
        String[] userRouteSelectPaths = Arrays.copyOf(
                routeSelectPaths, routeSelectPaths.length + 1
        );
        userRouteSelectPaths[userRouteSelectPaths.length - 1] = DbFields.GroupHikeRoute.NAME;
        return userRouteSelectPaths;
    }

    @Override
    public void handleRouteUniqueness(RouteUniquenessHandler.Builder<?> builder) {
        super.handleRouteUniqueness(
                builder.setOwnerName(groupHikeName)
                        .setOwnerPath(DbFields.GroupHikeRoute.NAME)
        );
    }

    public String getGroupHikeName() {
        return groupHikeName;
    }
}
