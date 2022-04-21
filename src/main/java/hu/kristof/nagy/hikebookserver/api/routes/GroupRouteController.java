package hu.kristof.nagy.hikebookserver.api.routes;

import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.EditedGroupRoute;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.route.grouproute.GroupRouteCreateService;
import hu.kristof.nagy.hikebookserver.service.route.grouproute.GroupRouteDeleteService;
import hu.kristof.nagy.hikebookserver.service.route.grouproute.GroupRouteEditService;
import hu.kristof.nagy.hikebookserver.service.route.grouproute.GroupRouteLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("groups/routes/")
public class GroupRouteController {

    @Autowired
    private GroupRouteCreateService groupRouteCreateService;

    @Autowired
    private GroupRouteLoadService groupRouteLoadService;

    @Autowired
    private GroupRouteDeleteService groupRouteDeleteService;

    @Autowired
    private GroupRouteEditService groupRouteEditService;

    @PutMapping("{groupName}/{routeName}")
    public ResponseResult<Boolean> createGroupRoute(
            @PathVariable String groupName,
            @PathVariable String routeName,
            @RequestBody GroupRoute groupRoute
    ) {
        return groupRouteCreateService.createRoute(groupRoute);
    }

    @GetMapping("{groupName}")
    public List<GroupRoute> loadGroupRoutes(
            @PathVariable String groupName
    ) {
        return groupRouteLoadService.loadGroupRoutes(groupName);
    }

    @GetMapping("{groupName}/{routeName}")
    public GroupRoute loadUserRoute(
            @PathVariable String groupName,
            @PathVariable String routeName
    ) {
        return groupRouteLoadService.loadGroupRoute(groupName, routeName);
    }

    @DeleteMapping("{groupName}/{routeName}")
    public boolean deleteGroupRoute(
            @PathVariable String groupName,
            @PathVariable String routeName
    ) {
        return groupRouteDeleteService.deleteGroupRoute(groupName, routeName);
    }

    @PutMapping("edit/{groupName}/{oldRouteName}")
    public boolean editGroupRoute(
            @PathVariable String groupName,
            @PathVariable String oldRouteName,
            @RequestBody EditedGroupRoute editedGroupRoute
    ) {
        return groupRouteEditService.editRoute(editedGroupRoute);
    }
}
