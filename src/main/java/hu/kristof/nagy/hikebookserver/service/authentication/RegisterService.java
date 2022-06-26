package hu.kristof.nagy.hikebookserver.service.authentication;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.User;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public final class RegisterService {
    /**
     * Registers the user if there is no other user
     * in the database with the same name.
     * @return true if registration was successful
     */
    public static ResponseResult<Boolean> registerUser(Firestore db, User user) {
        var transactionFuture = db.runTransaction(transaction -> {
            var users = db.collection(DbCollections.USER);
            var query = users
                    .select(DbFields.User.NAME)
                    .whereEqualTo(DbFields.User.NAME, user.getName());
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
        return ResponseResult.success(FutureUtil.handleFutureGet(transactionFuture::get));
    }
}
