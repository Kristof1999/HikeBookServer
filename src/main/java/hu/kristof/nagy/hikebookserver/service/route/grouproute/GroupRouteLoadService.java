package hu.kristof.nagy.hikebookserver.service.route.grouproute;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.RouteLoadService;

import java.util.List;
import java.util.stream.Collectors;

public final class GroupRouteLoadService {
    /**
     * Loads group routes associated with the given group's name.
     */
    public static ResponseResult<List<GroupRoute>> loadGroupRoutes(Firestore db, String groupName) {
        return ResponseResult.success(
                RouteLoadService.loadRoutes(db, groupName, DbFields.GroupRoute.NAME)
                .stream()
                .map(route -> new GroupRoute(route, groupName))
                .collect(Collectors.toList())
        );
    }

    /**
     * Loads the given route with the specified route name and group name.
     */
    public static ResponseResult<GroupRoute> loadGroupRoute(Firestore db, String groupName, String routeName) {
        var queryFuture = db
                .collection(DbCollections.ROUTE)
                .select(GroupRoute.getSelectPaths())
                .whereEqualTo(DbFields.GroupRoute.NAME, groupName)
                .whereEqualTo(DbFields.Route.ROUTE_NAME, routeName)
                .get();

        QueryDocumentSnapshot queryDocumentSnapshot = FutureUtil.handleFutureGet(() -> {
            var queryDocs = queryFuture.get().getDocuments();
            return Util.handleListSize(queryDocs, documentSnapshots ->
                    (QueryDocumentSnapshot) documentSnapshots.get(0)
            );
        });
        return ResponseResult.success(GroupRoute.from(queryDocumentSnapshot));
    }
}
