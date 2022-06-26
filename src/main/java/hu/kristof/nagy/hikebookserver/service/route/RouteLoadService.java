package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.List;
import java.util.stream.Collectors;

public final class RouteLoadService {
    /**
     * Loads routes associated with the given owner.
     * @param ownerName the owner to load routes for
     * @param ownerPath the path of the owner to use for querying
     */
    public static List<Route> loadRoutes(Firestore db, String ownerName, String ownerPath) {
        var queryFuture =  db
                .collection(DbCollections.ROUTE)
                .select(Route.getSelectPaths())
                .whereEqualTo(ownerPath, ownerName)
                .get();
        return FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments()
                        .stream()
                        .map(Route::from)
                        .collect(Collectors.toList())
        );
    }
}