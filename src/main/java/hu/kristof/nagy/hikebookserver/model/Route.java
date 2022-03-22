package hu.kristof.nagy.hikebookserver.model;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;

import java.util.*;

public class Route {
    private String userOrGroupName;
    private String routeName;
    private List<Point> points;
    private String description;

    public Route() {
        this("","", List.of(), "");
    }

    public Route(String userOrGroupName, String routeName, List<Point> points, String description) {
        this.userOrGroupName = userOrGroupName;
        this.routeName = routeName;
        this.points = List.copyOf(points);
        this.description = description;
    }

    public Route(Route route) {
        this(route.getUserOrGroupName(), route.getRouteName(), route.getPoints(), route.getDescription());
    }

    // TODO: try to eliminate this dependency, make it less error-prone
    // Note: if you change this, then don't forget to change RouteLoad's
    // loadRoutesForUser select call: if you want to get a field which is
    // not present in the select call, then you'll get a NullPointerException
    public static Route from(DocumentSnapshot documentSnapshot) {
        // try using toObject(this)
        String userOrGroupName = Objects.requireNonNull(
                documentSnapshot.getString(DbPathConstants.ROUTE_USER_NAME)
        );
        String routeName = Objects.requireNonNull(
                documentSnapshot.getString(DbPathConstants.ROUTE_NAME)
        );
        List<Point> points = (List<Point>) Objects.requireNonNull(
                documentSnapshot.get(DbPathConstants.ROUTE_POINTS)
        );
        String description = Objects.requireNonNull(
                documentSnapshot.getString(DbPathConstants.ROUTE_DESCRIPTION)
        );
        return new Route(userOrGroupName, routeName, points, description);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put(DbPathConstants.ROUTE_USER_NAME, getUserOrGroupName());
        data.put(DbPathConstants.ROUTE_NAME, getRouteName());
        data.put(DbPathConstants.ROUTE_POINTS, getPoints());
        data.put(DbPathConstants.ROUTE_DESCRIPTION, getDescription());
        return data;
    }

    public String getUserOrGroupName() {
        return userOrGroupName;
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
        return getUserOrGroupName().equals(route.getUserOrGroupName()) &&
                getRouteName().equals(route.getRouteName()) &&
                getPoints().equals(route.getPoints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserOrGroupName(), getRouteName(), getPoints());
    }

    @Override
    public String toString() {
        return "Route{" +
                "userOrGroupName='" + userOrGroupName + '\'' +
                ", routeName='" + routeName + '\'' +
                ", points=" + points +
                ", description='" + description + '\'' +
                '}';
    }
}
