package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.model.GroupRoute;
import hu.kristof.nagy.hikebookserver.service.grouproute.GroupRouteCreateService;
import hu.kristof.nagy.hikebookserver.service.grouproute.GroupRouteLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GroupRouteController {

    @Autowired
    private GroupRouteCreateService groupRouteCreateService;

    @Autowired
    private GroupRouteLoadService groupRouteLoadService;

    @PutMapping("routes/groups/{groupName}/{routeName}")
    public boolean createGroupRouteForGroup(
            @PathVariable String groupName,
            @PathVariable String routeName,
            @RequestBody GroupRoute groupRoute
    ) {
        return groupRouteCreateService.createGroupRoute(groupRoute);
    }

    @GetMapping("routes/groups/{groupName}")
    public List<GroupRoute> loadGroupRoutesForGroup(
            @PathVariable String groupName
    ) {
        return groupRouteLoadService.loadGroupRoutesForGroup(groupName);
    }
}
