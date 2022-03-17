package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class LoginService {

    @Autowired
    private Firestore db;

    /**
     * Logs in the given user if there exists one in the database
     * with the given name and password.
     * @return true if login was successful
     */
    public boolean loginUser(User user) {
        ApiFuture<QuerySnapshot> future = db
                .collection(DbPathConstants.COLLECTION_USER)
                .select(DbPathConstants.USER_NAME, DbPathConstants.USER_PASSWORD)
                .whereEqualTo(DbPathConstants.USER_NAME, user.getName())
                .whereEqualTo(DbPathConstants.USER_PASSWORD, user.getPassword())
                .get();

        try {
            if (future.get().isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
