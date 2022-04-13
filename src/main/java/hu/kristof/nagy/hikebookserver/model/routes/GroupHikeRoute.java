package hu.kristof.nagy.hikebookserver.model.routes;

import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;

import java.util.List;
import java.util.Map;

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
        data.put(DbPathConstants.ROUTE_GROUP_HIKE_NAME, groupHikeName);
        return data;
    }

    public String getGroupHikeName() {
        return groupHikeName;
    }
}
