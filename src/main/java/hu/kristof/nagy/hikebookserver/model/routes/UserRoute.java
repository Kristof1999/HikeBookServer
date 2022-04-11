package hu.kristof.nagy.hikebookserver.model.routes;

import hu.kristof.nagy.hikebookserver.model.Point;

import java.util.List;

public class UserRoute extends Route {
    private String userName;

    public UserRoute() {
    }

    public UserRoute(String routeName, List<Point> points, String description, String userName) {
        super(routeName, points, description);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
