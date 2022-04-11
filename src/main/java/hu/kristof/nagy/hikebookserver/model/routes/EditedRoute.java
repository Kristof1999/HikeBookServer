package hu.kristof.nagy.hikebookserver.model.routes;

public class EditedRoute {
    private Route newRoute, oldRoute;

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
