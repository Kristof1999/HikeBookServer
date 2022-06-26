package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteLoadService {

    @Autowired
    private Firestore db;

    /**
     * Loads routes associated with the given owner.
     * @param ownerName the owner to load routes for
     * @param ownerPath the path of the owner to use for querying
     */
    public List<Route> loadRoutes(String ownerName, String ownerPath) {
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