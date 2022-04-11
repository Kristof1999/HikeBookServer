package hu.kristof.nagy.hikebookserver.api.routes;

import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.routes.EditedUserRoute;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.route.UserRouteLoadService;
import hu.kristof.nagy.hikebookserver.service.route.RouteCreateService;
import hu.kristof.nagy.hikebookserver.service.route.RouteDeleteService;
import hu.kristof.nagy.hikebookserver.service.route.RouteEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users/routes/")
public class UserRouteController {

    @Autowired
    private RouteCreateService routeCreate;

    @Autowired
    private UserRouteLoadService userRouteLoadService;

    @Autowired
    private RouteDeleteService routeDelete;

    @Autowired
    private RouteEditService routeEdit;

    @PutMapping("{userName}/{routeName}")
    public boolean createUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName,
            @RequestBody UserRoute userRoute
    ) {
        return routeCreate.createRoute(userRoute, userName, DbPathConstants.ROUTE_USER_NAME);
    }

    @GetMapping("{userName}")
    public List<UserRoute> loadUserRoutes(
            @PathVariable String userName
    ) {
        return userRouteLoadService.loadUserRoutes(userName);
    }

    @GetMapping("{userName}/{routeName}")
    public UserRoute loadUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName
    ) {
        return userRouteLoadService.loadUserRoute(userName, routeName);
    }

    @DeleteMapping("{userName}/{routeName}")
    public boolean deleteUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName
    ) {
        return routeDelete.deleteRoute(userName, DbPathConstants.ROUTE_USER_NAME, routeName);
    }

    @PutMapping("edit/{userName}/{oldRouteName}")
    public boolean editUserRoute(
        @PathVariable String userName,
        @PathVariable String oldRouteName,
        @RequestBody EditedUserRoute editedUserRoute
    ) {
        return routeEdit.editRoute(editedUserRoute, userName, DbPathConstants.ROUTE_USER_NAME);
    }

    @GetMapping("")
    public List<BrowseListItem> listUserRoutes() {
        return userRouteLoadService.listUserRoutes();
    }
}
