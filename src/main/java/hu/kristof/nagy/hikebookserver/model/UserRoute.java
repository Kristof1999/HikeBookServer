package hu.kristof.nagy.hikebookserver.model;

import java.util.Objects;

public class UserRoute {
    private String userName;
    private Route route;

    public UserRoute() {
        this("", new Route());
    }

    public UserRoute(String userName, Route route) {
        this.userName = userName;
        this.route = route;
    }

    public String getUserName() {
        return userName;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoute userRoute = (UserRoute) o;
        return getUserName().equals(userRoute.getUserName()) && getRoute().equals(userRoute.getRoute());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getRoute());
    }
}
