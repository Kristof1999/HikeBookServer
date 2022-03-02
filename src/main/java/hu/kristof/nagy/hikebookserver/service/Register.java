package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.UserSource;
import hu.kristof.nagy.hikebookserver.model.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Register {

    @Autowired
    private UserSource userSource;

    public boolean registerUser(UserAuth user) {
        ApiFuture<QuerySnapshot> query = userSource.users.select("name")
                .whereEqualTo("name", user.getName())
                .get();

        try {
            if (query.get().isEmpty()) {
                DocumentReference document = userSource.users.document(user.getName());
                Map<String, Object> data = new HashMap<>();
                data.put("name", user.getName());
                data.put("pswd", user.getPassword());
                data.put("avgSpeed", 0L);
                document.set(data);
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
