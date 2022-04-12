package hu.kristof.nagy.hikebookserver.service.route.grouproute;

import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.service.route.RouteLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupRouteLoadService {
    @Autowired
    private RouteLoadService routeLoadService;

    public List<GroupRoute> loadGroupRoutes(String groupName) {
        return routeLoadService.loadRoutes(groupName, DbPathConstants.ROUTE_GROUP_NAME)
                .stream()
                .map(route -> new GroupRoute(route, groupName))
                .collect(Collectors.toList());
    }
}
