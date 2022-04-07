package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.EditedRoute;
import hu.kristof.nagy.hikebookserver.model.Route;
import hu.kristof.nagy.hikebookserver.model.RouteType;
import hu.kristof.nagy.hikebookserver.service.route.RouteCreateService;
import hu.kristof.nagy.hikebookserver.service.route.RouteDeleteService;
import hu.kristof.nagy.hikebookserver.service.route.RouteEditService;
import hu.kristof.nagy.hikebookserver.service.route.RouteLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: make UserController, and put userRoute specific methods in them

@RequestMapping("routes/")
@RestController
public class RouteController {

    @Autowired
    private RouteCreateService routeCreate;

    @Autowired
    private RouteLoadService routeLoad;

    @Autowired
    private RouteDeleteService routeDelete;

    @Autowired
    private RouteEditService routeEdit;

    @PutMapping("{ownerName}/{routeName}")
    public boolean createRoute(
            @PathVariable String ownerName,
            @PathVariable String routeName,
            @RequestBody Route route
            ) {
        return routeCreate.createRoute(route);
    }

    @GetMapping("{ownerName}/{routeType}")
    public List<Route> loadRoutes(
            @PathVariable String ownerName,
            @PathVariable RouteType routeType
    ) {
        return routeLoad.loadRoutes(ownerName, routeType);
    }

    @GetMapping("{ownerName}/{routeName}/{routeType}")
    public Route loadRoute(
            @PathVariable String ownerName,
            @PathVariable String routeName,
            @PathVariable RouteType routeType
    ) {
        return routeLoad.loadRoute(ownerName, routeName, routeType);
    }

    @GetMapping("")
    public List<BrowseListItem> listUserRoutes() {
        return routeLoad.listUserRoutes();
    }

    @DeleteMapping("{ownerName}/{routeName}/{routeType}")
    public boolean deleteRoute(
            @PathVariable String ownerName,
            @PathVariable String routeName,
            @PathVariable RouteType routeType
            ) {
        return routeDelete.deleteRoute(ownerName, routeName, routeType);
    }

    @PutMapping("edit/{ownerName}/{routeName}")
    public boolean editRoute(
            @PathVariable String ownerName,
            @PathVariable String routeName,
            @RequestBody EditedRoute editedUserRoute // TODO: try to make this work with Route
    ) {
        return routeEdit.editRoute(editedUserRoute);
    }
}
