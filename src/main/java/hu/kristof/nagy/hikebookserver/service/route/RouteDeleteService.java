package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Route;
import hu.kristof.nagy.hikebookserver.model.RouteType;
import hu.kristof.nagy.hikebookserver.service.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class RouteDeleteService {

    @Autowired
    private Firestore db;

    /**
     * @param ownerName name of user who requested deletion
     * @param routeName name of route which to delete for the given user
     * @return true if deletion was successful
     */
    public boolean deleteRoute(String ownerName, String routeName, RouteType routeType) {
        String ownerPath = Route.getOwnerDatabasePath(routeType);
        var routes = db.collection(DbPathConstants.COLLECTION_ROUTE);
        var queryFuture = routes
                .whereEqualTo(ownerPath, ownerName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
            var querySnapshot = queryFuture.get();
            if (querySnapshot.isEmpty()) {
                // route with given name and userName didn't exist
                return false;
            } else {
                String id = querySnapshot.getDocuments().get(0).getId();
                routes.document(id)
                        .delete()
                        .get(); // wait for write result
                return true;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(Util.GENERIC_ERROR_MSG);
    }
}
