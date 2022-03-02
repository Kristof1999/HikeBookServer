package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.UserSource;
import hu.kristof.nagy.hikebookserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Register {

    @Autowired
    private UserSource userSource;

    public boolean registerUser(User user) {
        ApiFuture<QuerySnapshot> future = userSource.users.select("name")
                .whereEqualTo("name", user.getName())
                .get();

        try {
            if (future.get().isEmpty()) {
                userSource.users
                        .document(user.getName())
                        .set(user);
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
