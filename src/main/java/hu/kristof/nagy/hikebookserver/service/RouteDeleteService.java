package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class RouteDeleteService {

    @Autowired
    private Firestore db;

    /**
     * @param userName name of user who requested deletion
     * @param routeName name of route which to delete for the given user
     * @return true if deletion was successful
     */
    public boolean deleteRoute(String userName, String routeName) {
        CollectionReference routes = db
                .collection(DbPathConstants.COLLECTION_ROUTE);
        ApiFuture<QuerySnapshot> future = routes
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
            QuerySnapshot querySnapshot = future.get();
            if (querySnapshot.isEmpty()) {
                return false;
            } else {
                String id = querySnapshot.getDocuments().get(0).getId();
                routes.document(id)
                        .delete()
                        .get();
                return true;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
