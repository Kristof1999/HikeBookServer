package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.Route;
import hu.kristof.nagy.hikebookserver.service.RouteCreate;
import hu.kristof.nagy.hikebookserver.service.RouteDelete;
import hu.kristof.nagy.hikebookserver.service.RouteEdit;
import hu.kristof.nagy.hikebookserver.service.RouteLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("routes/")
@RestController
public class RouteController {

    @Autowired
    private RouteCreate routeCreate;

    @Autowired
    private RouteLoad routeLoad;

    @Autowired
    private RouteDelete routeDelete;

    @Autowired
    private RouteEdit routeEdit;

    @PutMapping("{userName}/{routeName}")
    public boolean createRoute(
            @PathVariable String userName,
            @PathVariable String routeName,
            @RequestBody List<Point> points
            ) {
        return routeCreate.createRoute(userName, routeName, points);
    }

    @GetMapping("{userName}")
    public List<Route> loadRoutesForUser(@PathVariable String userName) {
        return routeLoad.loadRoutesForUser(userName);
    }

    @GetMapping("{userName}/{routeName}")
    public List<Point> loadPoints(
            @PathVariable String userName,
            @PathVariable String routeName
    ) {
        return routeLoad.loadPoints(userName, routeName);
    }

    @GetMapping("")
    public List<BrowseListItem> listRoutes() {
        return routeLoad.listRoutes();
    }

    @DeleteMapping("{userName}/{routeName}")
    public boolean deleteRoute(
            @PathVariable String userName,
            @PathVariable String routeName
    ) {
        return routeDelete.deleteRoute(userName, routeName);
    }

    @PutMapping("edit/{userName}/{routeName}")
    public boolean editRoute(
            @PathVariable String userName,
            @PathVariable String routeName,
            @RequestBody Route route
    ) {
        return routeEdit.editRoute(userName, routeName, route);
    }
}
