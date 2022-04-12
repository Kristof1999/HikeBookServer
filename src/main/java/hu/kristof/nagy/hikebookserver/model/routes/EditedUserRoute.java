package hu.kristof.nagy.hikebookserver.model.routes;

public final class EditedUserRoute extends EditedRoute {
    private UserRoute newUserRoute;
    private UserRoute oldUserRoute;

    public EditedUserRoute() {
    }

    public EditedUserRoute(UserRoute newUserRoute, UserRoute oldUserRoute) {
        super(newUserRoute, oldUserRoute);
        this.newUserRoute = newUserRoute;
        this.oldUserRoute = oldUserRoute;
    }

    @Override
    public UserRoute getNewRoute() {
        return newUserRoute;
    }

    @Override
    public UserRoute getOldRoute() {
        return oldUserRoute;
    }

    public UserRoute getNewUserRoute() {
        return newUserRoute;
    }

    public void setNewUserRoute(UserRoute newUserRoute) {
        this.newUserRoute = newUserRoute;
    }

    public UserRoute getOldUserRoute() {
        return oldUserRoute;
    }

    public void setOldUserRoute(UserRoute oldUserRoute) {
        this.oldUserRoute = oldUserRoute;
    }
}
