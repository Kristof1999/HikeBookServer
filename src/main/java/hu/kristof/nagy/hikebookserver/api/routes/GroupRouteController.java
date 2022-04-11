package hu.kristof.nagy.hikebookserver.api.routes;

import hu.kristof.nagy.hikebookserver.model.routes.EditedGroupRoute;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("groups/routes/")
public class GroupRouteController {
    @PutMapping("{groupName}/{routeName}")
    public boolean createGroupRoute(
            @PathVariable String groupName,
            @PathVariable String routeName,
            @RequestBody GroupRoute groupRoute
    ) {

    }

    @GetMapping("{groupName}")
    public List<GroupRoute> loadGroupRoutes(
            @PathVariable String groupName
    ) {

    }

    @DeleteMapping("{groupName}/{routeName}")
    public boolean deleteGroupRoute(
            @PathVariable String groupName,
            @PathVariable String routeName
    ) {

    }

    @PutMapping("edit/{groupName}/{routeName}")
    public boolean editGroupRoute(
            @PathVariable String groupName,
            @PathVariable String oldRouteName,
            @RequestBody EditedGroupRoute groupRoute
    ) {

    }
}
