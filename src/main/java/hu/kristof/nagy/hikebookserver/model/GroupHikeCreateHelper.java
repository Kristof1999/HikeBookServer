package hu.kristof.nagy.hikebookserver.model;

import hu.kristof.nagy.hikebookserver.model.routes.GroupHikeRoute;

public class GroupHikeCreateHelper {
    private DateTime dateTime;
    private GroupHikeRoute route;

    public GroupHikeCreateHelper() {
    }

    public GroupHikeCreateHelper(DateTime dateTime, GroupHikeRoute route) {
        this.dateTime = dateTime;
        this.route = route;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public GroupHikeRoute getRoute() {
        return route;
    }

    public void setRoute(GroupHikeRoute route) {
        this.route = route;
    }
}
