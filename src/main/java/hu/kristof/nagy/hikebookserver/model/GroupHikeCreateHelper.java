package hu.kristof.nagy.hikebookserver.model;

import hu.kristof.nagy.hikebookserver.model.routes.Route;

public class GroupHikeCreateHelper {
    private DateTime dateTime;
    private Route route;

    public GroupHikeCreateHelper() {
    }

    public GroupHikeCreateHelper(DateTime dateTime, Route route) {
        this.dateTime = dateTime;
        this.route = route;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
