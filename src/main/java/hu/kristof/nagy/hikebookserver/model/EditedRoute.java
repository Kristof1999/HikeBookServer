package hu.kristof.nagy.hikebookserver.model;

public class EditedRoute {
    private Route newRoute;
    private Route oldRoute;

    public EditedRoute() {
    }

    public EditedRoute(Route newRoute, Route oldRoute) {
        this.newRoute = newRoute;
        this.oldRoute = oldRoute;
    }

    public Route getNewRoute() {
        return newRoute;
    }

    public void setNewRoute(Route newRoute) {
        this.newRoute = newRoute;
    }

    public Route getOldRoute() {
        return oldRoute;
    }

    public void setOldRoute(Route oldRoute) {
        this.oldRoute = oldRoute;
    }
}
