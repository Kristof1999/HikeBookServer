package hu.kristof.nagy.hikebookserver.model;

import hu.kristof.nagy.hikebookserver.data.DbPathConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Route {
    protected String routeName;
    protected List<Point> points;
    protected String description;

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put(DbPathConstants.ROUTE_USER_NAME, getOwnerName());
        data.put(DbPathConstants.ROUTE_NAME, getRouteName());
        data.put(DbPathConstants.ROUTE_POINTS, getPoints());
        data.put(DbPathConstants.ROUTE_DESCRIPTION, getDescription());
        return data;
    }

    abstract String getOwnerName();

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
