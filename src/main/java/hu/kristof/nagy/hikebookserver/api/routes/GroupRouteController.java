package hu.kristof.nagy.hikebookserver.api.routes;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.EditedGroupRoute;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
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
    private Firestore db;

    @PutMapping("{groupName}/{routeName}")
    public ResponseResult<Boolean> createGroupRoute(
            @PathVariable String groupName,
            @PathVariable String routeName,
            @RequestBody GroupRoute groupRoute
    ) {
        return GroupRouteCreateService.createRoute(db, groupRoute);
    }

    @GetMapping("{groupName}")
    public ResponseResult<List<GroupRoute>> loadGroupRoutes(
            @PathVariable String groupName
    ) {
        return GroupRouteLoadService.loadGroupRoutes(db, groupName);
    }

    @GetMapping("{groupName}/{routeName}")
    public ResponseResult<GroupRoute> loadUserRoute(
            @PathVariable String groupName,
            @PathVariable String routeName
    ) {
        return GroupRouteLoadService.loadGroupRoute(db, groupName, routeName);
    }

    @DeleteMapping("{groupName}/{routeName}")
    public ResponseResult<Boolean> deleteGroupRoute(
            @PathVariable String groupName,
            @PathVariable String routeName
    ) {
        return GroupRouteDeleteService.deleteGroupRoute(db, groupName, routeName);
    }

    @PutMapping("edit/{groupName}/{oldRouteName}")
    public ResponseResult<Boolean> editGroupRoute(
            @PathVariable String groupName,
            @PathVariable String oldRouteName,
            @RequestBody EditedGroupRoute editedGroupRoute
    ) {
        return GroupRouteEditService.editRoute(db, editedGroupRoute);
    }
}
