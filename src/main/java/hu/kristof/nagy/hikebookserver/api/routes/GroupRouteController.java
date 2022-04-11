package hu.kristof.nagy.hikebookserver.api.routes;

import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.RouteType;
import hu.kristof.nagy.hikebookserver.model.routes.EditedGroupRoute;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRouteLoadService;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.route.RouteCreateService;
import hu.kristof.nagy.hikebookserver.service.route.RouteDeleteService;
import hu.kristof.nagy.hikebookserver.service.route.RouteEditService;
import hu.kristof.nagy.hikebookserver.service.route.RouteLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("groups/routes/")
public class GroupRouteController {

    @Autowired
    private RouteCreateService routeCreate;

    @Autowired
    private GroupRouteLoadService groupRouteLoadService;

    @Autowired
    private RouteDeleteService routeDelete;

    @Autowired
    private RouteEditService routeEdit;

    @PutMapping("{groupName}/{routeName}")
    public boolean createGroupRoute(
            @PathVariable String groupName,
            @PathVariable String routeName,
            @RequestBody GroupRoute groupRoute
    ) {
        return routeCreate.createRoute(groupRoute, groupName, DbPathConstants.ROUTE_GROUP_NAME);
    }

    @GetMapping("{groupName}")
    public List<GroupRoute> loadGroupRoutes(
            @PathVariable String groupName
    ) {
        return groupRouteLoadService.loadGroupRoutes(groupName);
    }

    @DeleteMapping("{groupName}/{routeName}")
    public boolean deleteGroupRoute(
            @PathVariable String groupName,
            @PathVariable String routeName
    ) {
        return routeDelete.deleteRoute(groupName, DbPathConstants.ROUTE_GROUP_NAME, routeName);
    }

    @PutMapping("edit/{groupName}/{routeName}")
    public boolean editGroupRoute(
            @PathVariable String groupName,
            @PathVariable String oldRouteName,
            @RequestBody EditedGroupRoute editedGroupRoute
    ) {
        return routeEdit.editRoute(editedGroupRoute, groupName, DbPathConstants.ROUTE_GROUP_NAME);
    }
}
