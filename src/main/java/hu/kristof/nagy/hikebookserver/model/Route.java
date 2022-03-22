package hu.kristof.nagy.hikebookserver.model;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;

import java.util.*;

public class Route {
    private String routeName;
    private List<Point> points;
    private String description;

    public Route() {
        this("", List.of(), "");
    }

    public Route(String routeName, List<Point> points, String description) {
        this.routeName = routeName;
        this.points = List.copyOf(points);
        this.description = description;
    }

    // TODO: try to eliminate this dependency, make it less error-prone
    // Note: if you change this, then don't forget to change RouteLoad's
    // loadRoutesForUser select call: if you want to get a field which is
    // not present in the select call, then you'll get a NullPointerException
    public static Route from(QueryDocumentSnapshot queryDocumentSnapshot) {
        String routeName = Objects.requireNonNull(
                queryDocumentSnapshot.get(DbPathConstants.ROUTE_NAME)
        ).toString();
        List<Point> points = (List<Point>) Objects.requireNonNull(
                queryDocumentSnapshot.get(DbPathConstants.ROUTE_POINTS)
        );
        String description = (String) Objects.requireNonNull(
                queryDocumentSnapshot.get(DbPathConstants.ROUTE_DESCRIPTION)
        );
        return new Route(routeName, points, description);
    }

    public static Map<String, Object> toMap(
            String userName, String routeName,
            List<Point> points, String description
            ) {
        Map<String, Object> data = new HashMap<>();
        data.put(DbPathConstants.ROUTE_USER_NAME, userName);
        data.put(DbPathConstants.ROUTE_NAME, routeName);
        data.put(DbPathConstants.ROUTE_POINTS, points);
        data.put(DbPathConstants.ROUTE_DESCRIPTION, description);
        return data;
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
        Collections.copy(this.points, points);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return getRouteName().equals(route.getRouteName()) && getPoints().equals(route.getPoints()) && getDescription().equals(route.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRouteName(), getPoints(), getDescription());
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
