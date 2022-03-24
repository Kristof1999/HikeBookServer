package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.EditedUserRoute;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import hu.kristof.nagy.hikebookserver.service.userroute.UserRouteCreateService;
import hu.kristof.nagy.hikebookserver.service.userroute.UserRouteDeleteService;
import hu.kristof.nagy.hikebookserver.service.userroute.UserRouteEditService;
import hu.kristof.nagy.hikebookserver.service.userroute.UserRouteLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: make UserController, and put userRoute specific methods in them

@RequestMapping("routes/")
@RestController
public class RouteController {

    @Autowired
    private UserRouteCreateService routeCreate;

    @Autowired
    private UserRouteLoadService routeLoad;

    @Autowired
    private UserRouteDeleteService routeDelete;

    @Autowired
    private UserRouteEditService routeEdit;

    @PutMapping("{userName}/{routeName}")
    public boolean createUserRouteForUser(
            @PathVariable String userName,
            @PathVariable String routeName,
            @RequestBody UserRoute route
            ) {
        return routeCreate.createUserRoute(route);
    }

    @GetMapping("{userName}")
    public List<UserRoute> loadUserRoutesForUser(@PathVariable String userName) {
        return routeLoad.loadUserRoutesForUser(userName);
    }

    @GetMapping("{userName}/{routeName}")
    public UserRoute loadUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName
    ) {
        return routeLoad.loadUserRoute(userName, routeName);
    }

    @GetMapping("")
    public List<BrowseListItem> listUserRoutes() {
        return routeLoad.listUserRoutes();
    }

    @DeleteMapping("{userName}/{routeName}")
    public boolean deleteUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName
    ) {
        return routeDelete.deleteUserRoute(userName, routeName);
    }

    @PutMapping("edit/{userName}/{routeName}")
    public boolean editUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName,
            @RequestBody EditedUserRoute editedUserRoute // TODO: try to make this work with Route
    ) {
        return routeEdit.editUserRoute(editedUserRoute);
    }
}
