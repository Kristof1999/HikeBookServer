package hu.kristof.nagy.hikebookserver.model.routes;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.RouteType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Route {
    private String routeName;
    private List<Point> points;
    private String description;

    public Route() {
    }

    public Route(String routeName, List<Point> points, String description) {
        this.routeName = routeName;
        this.points = points;
        this.description = description;
    }

    public Map<String, Object> toMap() {
        var data = new HashMap<String, Object>();
        data.put(DbPathConstants.ROUTE_NAME, getRouteName());
        data.put(DbPathConstants.ROUTE_POINTS, getPoints());
        data.put(DbPathConstants.ROUTE_DESCRIPTION, getDescription());
        return data;
    }

    public static Route from(DocumentSnapshot documentSnapshot) {
        var routeName = Objects.requireNonNull(
                documentSnapshot.getString(DbPathConstants.ROUTE_NAME)
        );
        var points= Objects.requireNonNull(
                (List<Point>) documentSnapshot.get(DbPathConstants.ROUTE_POINTS)
        );
        var description = Objects.requireNonNull(
                documentSnapshot.getString(DbPathConstants.ROUTE_DESCRIPTION)
        );
        return new Route(routeName, points, description);
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public List<Point> getPoints() {
        return List.copyOf(points);
    }

    public void setPoints(List<Point> points) {
        this.points = List.copyOf(points);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
