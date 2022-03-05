package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.CloudDatabase;
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
        ApiFuture<QuerySnapshot> future = db.getDb().collection("users")
                .select("name", "password")
                .whereEqualTo("name", user.getName())
                .whereEqualTo("password", user.getPassword())
                .get();
        // TODO: test if whereEqualTo uses the User's equals
        try {
            if (future.get().isEmpty()) {
                // TODO: give back more meaningful error messages
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
