package hu.kristof.nagy.hikebookserver.model;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Route {
    private String ownerName;
    private RouteType routeType;
    private String routeName;
    private List<Point> points;
    private String description;

    public Route() {
    }

    public Route(
            String ownerName,
            RouteType routeType,
            String routeName,
            List<Point> points,
            String description
    ) {
        this.ownerName = ownerName;
        this.routeType = routeType;
        this.routeName = routeName;
        this.points = points;
        this.description = description;
    }

    public Map<String, Object> toMap() {
        var data = new HashMap<String, Object>();
        data.put(getOwnerDatabasePath(routeType), getOwnerName());
        data.put(DbPathConstants.ROUTE_NAME, getRouteName());
        data.put(DbPathConstants.ROUTE_POINTS, getPoints());
        data.put(DbPathConstants.ROUTE_DESCRIPTION, getDescription());
        return data;
    }

    public static Route from(DocumentSnapshot documentSnapshot, RouteType routeType) {
        String ownerpath = getOwnerDatabasePath(routeType);
        var owneName = Objects.requireNonNull(
                documentSnapshot.getString(ownerpath)
        );
        var routeName = Objects.requireNonNull(
                documentSnapshot.getString(DbPathConstants.ROUTE_NAME)
        );
        var points= Objects.requireNonNull(
                (List<Point>) documentSnapshot.get(DbPathConstants.ROUTE_POINTS)
        );
        var description = Objects.requireNonNull(
                documentSnapshot.getString(DbPathConstants.ROUTE_DESCRIPTION)
        );
        return new Route(owneName, routeType, routeName, points, description);
    }

    public String getOwnerName() {
        return ownerName;
    }

    public static String getOwnerDatabasePath(RouteType routeType) {
        switch (routeType) {
            case USER: return DbPathConstants.ROUTE_USER_NAME;
            case GROUP: return DbPathConstants.ROUTE_GROUP_NAME;
            case GROUP_HIKE: return DbPathConstants.ROUTE_GROUP_HIKE_NAME;
            default: throw new IllegalArgumentException("Route type " + routeType + " is not allowed.");
        }
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

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }
}
