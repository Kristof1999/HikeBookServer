package hu.kristof.nagy.hikebookserver.api.routes;

import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.GroupHikeRoute;
import hu.kristof.nagy.hikebookserver.service.route.grouphikeroute.GroupHikeRouteLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupHikeRouteController {

    @Autowired
    private GroupHikeRouteLoadService groupHikeRouteLoadService;

    @GetMapping("groupHike/routes/{groupHikeName}")
    public ResponseResult<GroupHikeRoute> loadGroupHikeRoute(
            @PathVariable String groupHikeName
    ) {
        return groupHikeRouteLoadService.loadGroupHikeRoute(groupHikeName);
    }
}
