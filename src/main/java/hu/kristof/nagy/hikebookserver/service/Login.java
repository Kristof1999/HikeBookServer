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

    public boolean loginUser(User user) {
        ApiFuture<QuerySnapshot> future = db.getDb().collection("users")
                .select("name", "pswd")
                .whereEqualTo("name", user.getName())
                .whereEqualTo("pswd", user.getPassword())
                .get();

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
