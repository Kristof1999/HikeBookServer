package hu.kristof.nagy.hikebookserver.service.authentication;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.User;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class RegisterService {

    @Autowired
    private Firestore db;

    /**
     * Registers the user if there is no other user
     * in the database with the same name.
     * @return true if registration was successful
     */
    public boolean registerUser(User user) {
        var users = db.collection(DbPathConstants.COLLECTION_USER);
        var queryFuture = users
                .select(DbPathConstants.USER_NAME)
                .whereEqualTo(DbPathConstants.USER_NAME, user.getName())
                .get();

        return FutureUtil.handleFutureGet(() -> {
            if (queryFuture.get().isEmpty()) {
                // userName is unique
                var data = new HashMap<String, Object>();
                data.put(DbPathConstants.USER_NAME, user.getName());
                data.put(DbPathConstants.USER_PASSWORD, user.getPassword());
                data.put(DbPathConstants.USER_AVG_SPEED, user.getAvgSpeed());

                users.document(user.getName())
                        .set(data)
                        .get();// wait for write result
                return true;
            } else {
                return false;
            }
        });
    }
}
