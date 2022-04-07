package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.Route;
import hu.kristof.nagy.hikebookserver.model.RouteType;
import hu.kristof.nagy.hikebookserver.service.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class RouteLoadService {

    @Autowired
    private Firestore db;

    /**
     * Loads all the routes which belong to the given user.
     * @param ownerName the user's name for who to load the routes
     * @return list of routes which belong to the given user
     */
    public List<Route> loadRoutes(String ownerName, RouteType routeType) {
        String ownerPath = Route.getOwnerDatabasePath(routeType);
        var queryFuture =  db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(ownerPath,
                        DbPathConstants.ROUTE_NAME,
                        DbPathConstants.ROUTE_POINTS,
                        DbPathConstants.ROUTE_DESCRIPTION)
                .whereEqualTo(ownerPath, ownerName)
                .get();
        try {
            return queryFuture.get().getDocuments()
                    .stream()
                    .map(queryDocumentSnapshot ->
                            Route.from(queryDocumentSnapshot, routeType)
                    )
                    .collect(Collectors.toList());
        } catch (InterruptedException | CancellationException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(Util.GENERIC_ERROR_MSG);
    }

    /**
     * Lists all the routes' name and associated user name.
     */
    public List<BrowseListItem> listUserRoutes() {
        var routes = new ArrayList<BrowseListItem>();
        // TODO: select routes which do not belong to the user who requested the listing
        // use whereNotEqualTo(...)
        for(var docRef : db.collection(DbPathConstants.COLLECTION_ROUTE).listDocuments()) {
            try {
                var documentSnapshot = docRef.get().get();
                var userName = Objects.requireNonNull(
                        documentSnapshot.getString(DbPathConstants.ROUTE_USER_NAME)
                );
                var routeName = Objects.requireNonNull(
                        documentSnapshot.getString(DbPathConstants.ROUTE_NAME)
                );
                routes.add(new BrowseListItem(userName, routeName));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return routes;
    }

    /**
     * Loads the specified route.
     * @param ownerName name of the user who requested the load
     * @param routeName name of the route for which to load the points
     */
    public Route loadRoute(String ownerName, String routeName, RouteType routeType) {
        String ownerPath = Route.getOwnerDatabasePath(routeType);
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_POINTS,
                        DbPathConstants.ROUTE_NAME,
                        ownerPath,
                        DbPathConstants.ROUTE_DESCRIPTION)
                .whereEqualTo(ownerPath, ownerName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
            var queryDocumentSnapshot = queryFuture.get().getDocuments().get(0);
            return Route.from(queryDocumentSnapshot, routeType);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(Util.GENERIC_ERROR_MSG);
    }
}