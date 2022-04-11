package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.model.RouteType;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouteDeleteService {

    @Autowired
    private Firestore db;

    /**
     * @param ownerName name of user who requested deletion
     * @param routeName name of route which to delete for the given user
     * @return true if deletion was successful
     */
    public boolean deleteRoute(String ownerName, String ownerPath, String routeName) {
        var routes = db.collection(DbPathConstants.COLLECTION_ROUTE);
        var queryFuture = routes
                .whereEqualTo(ownerPath, ownerName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();

        QuerySnapshot querySnapshot = FutureUtil.handleFutureGet(queryFuture::get);
        if (querySnapshot.isEmpty()) {
            // route with given name and userName didn't
            return false;
        } else {
            String id = querySnapshot.getDocuments().get(0).getId();
            FutureUtil.handleFutureGet(() ->
                    routes.document(id)
                            .delete()
                            .get() // wait for write result
            );
            return true;
        }
    }
}
