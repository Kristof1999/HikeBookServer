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
        var transactionFuture = db.runTransaction(transaction -> {
            var users = db.collection(DbPathConstants.COLLECTION_USER);
            var query = users
                    .select(DbPathConstants.USER_NAME)
                    .whereEqualTo(DbPathConstants.USER_NAME, user.getName());
            var queryFuture = transaction.get(query);

            return FutureUtil.handleFutureGet(() -> {
                if (queryFuture.get().isEmpty()) {
                    // userName is unique
                    Map<String, Object> data = user.toMap();

                    var docRef = users.document(user.getName());
                    transaction.set(docRef, data);
                    return true;
                } else {
                    return false;
                }
            });
        });

        // wait for write result
        return FutureUtil.handleFutureGet(transactionFuture::get);
    }
}
