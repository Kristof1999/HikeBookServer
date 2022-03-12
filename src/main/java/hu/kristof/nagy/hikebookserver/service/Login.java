package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.CloudDatabase;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

public class Login {

    @Autowired
    private CloudDatabase db;

    /**
     * Logs in the given user if there exists one in the database
     * with the given name and password.
     * @return true if login was successful
     */
    public boolean loginUser(User user) {
        ApiFuture<QuerySnapshot> future = db.getDb()
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
