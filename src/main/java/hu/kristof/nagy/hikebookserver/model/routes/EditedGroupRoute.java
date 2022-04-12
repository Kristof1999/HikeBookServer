package hu.kristof.nagy.hikebookserver.model.routes;

public final class EditedGroupRoute extends EditedRoute {
    private GroupRoute newGroupRoute;
    private GroupRoute oldGroupRoute;

    public EditedGroupRoute() {
    }

    public EditedGroupRoute(GroupRoute newGroupRoute, GroupRoute oldGroupRoute) {
        super(newGroupRoute, oldGroupRoute);
        this.newGroupRoute = newGroupRoute;
        this.oldGroupRoute = oldGroupRoute;
    }

    @Override
    public GroupRoute getNewRoute() {
        return newGroupRoute;
    }

    @Override
    public GroupRoute getOldRoute() {
        return oldGroupRoute;
    }

    public GroupRoute getNewGroupRoute() {
        return newGroupRoute;
    }

    public void setNewGroupRoute(GroupRoute newGroupRoute) {
        this.newGroupRoute = newGroupRoute;
    }

    public GroupRoute getOldGroupRoute() {
        return oldGroupRoute;
    }

    public void setOldGroupRoute(GroupRoute oldGroupRoute) {
        this.oldGroupRoute = oldGroupRoute;
    }
}
