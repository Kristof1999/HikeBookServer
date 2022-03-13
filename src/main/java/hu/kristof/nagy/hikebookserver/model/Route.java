package hu.kristof.nagy.hikebookserver.model;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;

import java.util.List;
import java.util.Objects;

public class Route {
    private String routeName;
    private List<Point> points;

    public Route() {
        this("", List.of());
    }

    public Route(String routeName, List<Point> points) {
        this.routeName = routeName;
        this.points = points;
    }

    public static Route from(QueryDocumentSnapshot queryDocumentSnapshot) {
        String routeName = Objects.requireNonNull(
                queryDocumentSnapshot.get(DbPathConstants.ROUTE_NAME)
        ).toString();
        List<Point> points = (List<Point>) Objects.requireNonNull(
                queryDocumentSnapshot.get(DbPathConstants.ROUTE_POINTS)
        );
        return new Route(routeName, points);
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(getRouteName(), route.getRouteName()) && Objects.equals(getPoints(), route.getPoints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRouteName(), getPoints());
    }
}
