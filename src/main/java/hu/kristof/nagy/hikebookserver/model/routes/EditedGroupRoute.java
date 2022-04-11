package hu.kristof.nagy.hikebookserver.model.routes;

public class EditedGroupRoute {
    private GroupRoute newRoute;
    private GroupRoute oldRoute;

    public EditedGroupRoute() {
    }

    public EditedGroupRoute(GroupRoute newRoute, GroupRoute oldRoute) {
        this.newRoute = newRoute;
        this.oldRoute = oldRoute;
    }

    public GroupRoute getNewRoute() {
        return newRoute;
    }

    public void setNewRoute(GroupRoute newRoute) {
        this.newRoute = newRoute;
    }

    public GroupRoute getOldRoute() {
        return oldRoute;
    }

    public void setOldRoute(GroupRoute oldRoute) {
        this.oldRoute = oldRoute;
    }
}
