package hu.kristof.nagy.hikebookserver.service.route.grouproute;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import hu.kristof.nagy.hikebookserver.service.route.RouteLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupRouteLoadService {
    @Autowired
    private Firestore db;

    @Autowired
    private RouteLoadService routeLoadService;

    public List<GroupRoute> loadGroupRoutes(String groupName) {
        return routeLoadService.loadRoutes(groupName, DbPathConstants.ROUTE_GROUP_NAME)
                .stream()
                .map(route -> new GroupRoute(route, groupName))
                .collect(Collectors.toList());
    }

    public GroupRoute loadGroupRoute(String groupName, String routeName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(GroupRoute.getSelectPaths())
                .whereEqualTo(DbPathConstants.ROUTE_GROUP_NAME, groupName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();

        QueryDocumentSnapshot queryDocumentSnapshot = FutureUtil.handleFutureGet(() -> {
            var queryDocs = queryFuture.get().getDocuments();
            return Util.handleListSize(queryDocs, documentSnapshots ->
                    (QueryDocumentSnapshot) documentSnapshots.get(0)
            );
        });
        return GroupRoute.from(queryDocumentSnapshot);
    }
}
