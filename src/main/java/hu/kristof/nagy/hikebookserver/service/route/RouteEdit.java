package hu.kristof.nagy.hikebookserver.service.route;

import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.EditedRoute;

public interface RouteEdit {
    /**
     * Edits the route. If something changes, then it must be unique for the user.
     * @return true if the edited route is unique for the given user
     */
    ResponseResult<Boolean> editRoute(EditedRoute editedRoute);
}
