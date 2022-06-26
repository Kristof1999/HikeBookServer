package hu.kristof.nagy.hikebookserver.model.routes;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.RouteUniquenessHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Route {
    protected String routeName;
    protected List<Point> points;
    protected String description;

    public Route() {
    }

    public Route(Route route) {
        this(route.getRouteName(), route.getPoints(), route.getDescription());
    }

    public Route(String routeName, List<Point> points, String description) {
        this.routeName = routeName;
        this.points = List.copyOf(points);
        this.description = description;
    }

    public Map<String, Object> toMap() {
        var data = new HashMap<String, Object>();
        data.put(DbFields.Route.ROUTE_NAME, getRouteName());
        data.put(DbFields.Route.POINTS, getPoints());
        data.put(DbFields.Route.DESCRIPTION, getDescription());
        return data;
    }

    public static Route from(DocumentSnapshot documentSnapshot) {
        return Objects.requireNonNull(
                documentSnapshot.toObject(Route.class)
        );
    }

    public static String[] getSelectPaths() {
        return new String[] {
                DbFields.Route.ROUTE_NAME,
                DbFields.Route.POINTS,
                DbFields.Route.DESCRIPTION
        };
    }

    public void handleRouteUniqueness(RouteUniquenessHandler.Builder<?> builder) {
        builder.setPoints(points)
                .setRouteName(routeName)
                .build()
                .handleRouteUniqueness();
    }

    public void handleRoutePointsUniqueness(RouteUniquenessHandler.Builder<?> builder) {
        builder.setPoints(points)
                .setRouteName(routeName)
                .build()
                .handlePointsUniqueness();
    }

    public void handleRouteNameUniqueness(RouteUniquenessHandler.Builder<?> builder) {
        builder.setPoints(points)
                .setRouteName(routeName)
                .build()
                .handleRouteNameUniqueness();
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

    @Override
    public String toString() {
        return "Route{" +
                "routeName='" + routeName + '\'' +
                ", points=" + points +
                ", description='" + description + '\'' +
                '}';
    }
}
