package hu.kristof.nagy.hikebookserver.service.route.userroute;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import hu.kristof.nagy.hikebookserver.service.route.RouteLoadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserRouteLoadService {
    @Autowired
    private Firestore db;

    @Autowired
    private RouteLoadService routeLoadService;

    public List<UserRoute> loadUserRoutes(String userName) {
        return routeLoadService.loadRoutes(userName, DbPathConstants.ROUTE_USER_NAME)
                .stream()
                .map(route -> new UserRoute(route, userName))
                .collect(Collectors.toList());
    }

    public UserRoute loadUserRoute(String userName, String routeName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_POINTS,
                        DbPathConstants.ROUTE_NAME,
                        DbPathConstants.ROUTE_USER_NAME,
                        DbPathConstants.ROUTE_DESCRIPTION)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();

        QueryDocumentSnapshot queryDocumentSnapshot = FutureUtil.handleFutureGet(() -> {
            var queryDocs = queryFuture.get().getDocuments();
            return Util.handleListSize(queryDocs, documentSnapshots ->
                    (QueryDocumentSnapshot) documentSnapshots.get(0)
            );
        });
        return UserRoute.from(queryDocumentSnapshot);
    }

    /**
     * Lists all the routes' name and associated user name.
     */
    public List<BrowseListItem> listUserRoutes() {
        var routes = new ArrayList<BrowseListItem>();
        // TODO: select routes which do not belong to the user who requested the listing
        // use whereNotEqualTo(...)
        for(var docRef : db.collection(DbPathConstants.COLLECTION_ROUTE).listDocuments()) {
            DocumentSnapshot documentSnapshot = FutureUtil.handleFutureGet(() ->
                    docRef.get().get()
            );
            var userName = Objects.requireNonNull(
                    documentSnapshot.getString(DbPathConstants.ROUTE_USER_NAME)
            );
            var routeName = Objects.requireNonNull(
                    documentSnapshot.getString(DbPathConstants.ROUTE_NAME)
            );
            routes.add(new BrowseListItem(userName, routeName));
        }
        return routes;
    }
}
