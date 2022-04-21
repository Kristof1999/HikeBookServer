package hu.kristof.nagy.hikebookserver.service.route;

import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.Route;

public interface RouteCreate {
    /**
     * Before saving the route, checks if the route is unique.
     *
     * @param route the created route
     * @return true if route is unique
     */
    ResponseResult<Boolean> createRoute(Route route);
}
