package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.RouteCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RouteController {

    @Autowired
    private RouteCreate routeCreate;

    @PutMapping("routes/create/{userName}/{routeName}")
    public boolean createRoute(
            @PathVariable String userName,
            @PathVariable String routeName,
            @RequestBody List<Point> points
            ) {
        return routeCreate.createRoute(userName, routeName, points);
    }
}
