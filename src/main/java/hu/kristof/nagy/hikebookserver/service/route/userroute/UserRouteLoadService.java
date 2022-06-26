package hu.kristof.nagy.hikebookserver.service.route.userroute;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.RouteLoadService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class UserRouteLoadService {
    /**
     * Loads all the routes associated with the given user.
     */
    public static ResponseResult<List<UserRoute>> loadUserRoutes(Firestore db, String userName) {
        return ResponseResult.success(
                RouteLoadService.loadRoutes(db, userName, DbFields.UserRoute.NAME)
                .stream()
                .map(route -> new UserRoute(route, userName))
                .collect(Collectors.toList())
        );
    }

    /**
     * Loads the given route associated with the given user.
     */
    public static ResponseResult<UserRoute> loadUserRoute(Firestore db, String userName, String routeName) {
        var queryFuture = db
                .collection(DbCollections.ROUTE)
                .select(UserRoute.getSelectPaths())
                .whereEqualTo(DbFields.UserRoute.NAME, userName)
                .whereEqualTo(DbFields.Route.ROUTE_NAME, routeName)
                .get();

        QueryDocumentSnapshot queryDocumentSnapshot = FutureUtil.handleFutureGet(() -> {
            var queryDocs = queryFuture.get().getDocuments();
            return Util.handleListSize(queryDocs, documentSnapshots ->
                    (QueryDocumentSnapshot) documentSnapshots.get(0)
            );
        });
        return ResponseResult.success(UserRoute.from(queryDocumentSnapshot));
    }

    /**
     * Lists all the user routes' name and associated user name.
     */
    public static ResponseResult<List<BrowseListItem>> listUserRoutes(Firestore db, String requesterName) {
        var routes = new ArrayList<BrowseListItem>();
        var queryFuture = db.collection(DbCollections.ROUTE)
                .select(BrowseListItem.getSelectPaths())
                .whereNotEqualTo(DbFields.UserRoute.NAME, requesterName)
                .get();
        var queryDocs = FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments()
        );
        for(var documentSnapshot : queryDocs) {
            var userName = Objects.requireNonNull(
                    documentSnapshot.getString(DbFields.UserRoute.NAME)
            );
            var routeName = Objects.requireNonNull(
                    documentSnapshot.getString(DbFields.Route.ROUTE_NAME)
            );
            routes.add(new BrowseListItem(userName, routeName));
        }
        return ResponseResult.success(routes);
    }
}
