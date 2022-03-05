package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.CloudDatabase;
import hu.kristof.nagy.hikebookserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Register {

    @Autowired
    private CloudDatabase db;

    /**
     * Registers the user if there is no other user
     * in the database with the same name.
     * @return true if registration was successful
     */
    public boolean registerUser(User user) {
        CollectionReference users = db.getDb().collection("users");
        ApiFuture<QuerySnapshot> future = users.select("name")
                .whereEqualTo("name", user.getName())
                .get();

        try {
            if (future.get().isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put(User.NAME, user.getName());
                data.put(User.PASSWORD, user.getPassword());
                data.put(User.AVG_SPEED, user.getAvgSpeed());

                users.document(user.getName()).set(data);
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
