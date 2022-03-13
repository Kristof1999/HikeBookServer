package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

public class RouteDelete {

    @Autowired
    private Firestore db;

    public boolean deleteRoute(String userName, String routeName) {
        ApiFuture<QuerySnapshot> future = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
            String id = future.get().getDocuments().get(0).getId();
             db.collection(DbPathConstants.COLLECTION_ROUTE)
                    .document(id)
                    .delete()
                    .get();
             return true;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
