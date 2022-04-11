package hu.kristof.nagy.hikebookserver.model.routes;

public class EditedUserRoute {
    private UserRoute newRoute;
    private UserRoute oldRoute;

    public EditedUserRoute() {
    }

    public EditedUserRoute(UserRoute newRoute, UserRoute oldRoute) {
        this.newRoute = newRoute;
        this.oldRoute = oldRoute;
    }

    public UserRoute getNewRoute() {
        return newRoute;
    }

    public void setNewRoute(UserRoute newRoute) {
        this.newRoute = newRoute;
    }

    public UserRoute getOldRoute() {
        return oldRoute;
    }

    public void setOldRoute(UserRoute oldRoute) {
        this.oldRoute = oldRoute;
    }
}
