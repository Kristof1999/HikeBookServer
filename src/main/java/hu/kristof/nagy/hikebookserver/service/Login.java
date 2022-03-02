package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.UserSource;
import hu.kristof.nagy.hikebookserver.model.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Login {

    @Autowired
    private UserSource userSource;

    public boolean loginUser(UserAuth user) {
        ApiFuture<QuerySnapshot> query = userSource.users.select("name", "pswd")
                .whereEqualTo("name", user.getName())
                .whereEqualTo("pswd", user.getPassword())
                .get();

        try {
            if (query.get().isEmpty()) {
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
