package hu.kristof.nagy.hikebookserver.model.routes;

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

    public Route getOldRoute() {
        return oldRoute;
    }
}
