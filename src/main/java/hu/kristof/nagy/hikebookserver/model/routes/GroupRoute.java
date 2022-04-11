package hu.kristof.nagy.hikebookserver.model.routes;

import hu.kristof.nagy.hikebookserver.model.Point;

import java.util.List;

public class GroupRoute extends Route {
    private String groupName;

    public GroupRoute() {
    }

    public GroupRoute(String routeName, List<Point> points, String description, String groupName) {
        super(routeName, points, description);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}
