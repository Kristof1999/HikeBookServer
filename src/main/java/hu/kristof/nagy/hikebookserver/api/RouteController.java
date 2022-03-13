package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.Route;
import hu.kristof.nagy.hikebookserver.service.RouteCreate;
import hu.kristof.nagy.hikebookserver.service.RouteDelete;
import hu.kristof.nagy.hikebookserver.service.RouteLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RouteController {

    @Autowired
    private RouteCreate routeCreate;

    @Autowired
    private RouteLoad routeLoad;

    @Autowired
    private RouteDelete routeDelete;

    @PutMapping("routes/create/{userName}/{routeName}")
    public boolean createRoute(
            @PathVariable String userName,
            @PathVariable String routeName,
            @RequestBody List<Point> points
            ) {
        return routeCreate.createRoute(userName, routeName, points);
    }

    @GetMapping("routes/load/{userName}")
    public List<Route> loadRoutesForUser(@PathVariable String userName) {
        return routeLoad.loadRoutesForUser(userName);
    }

    @GetMapping("routes/delete/{userName}/{routeName}")
    public boolean deleteRoute(
            @PathVariable String userName,
            @PathVariable String routeName
    ) {
        return routeDelete.deleteRoute(userName, routeName);
    }
}
