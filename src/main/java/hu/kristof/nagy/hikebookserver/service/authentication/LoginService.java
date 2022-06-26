package hu.kristof.nagy.hikebookserver.service.authentication;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.User;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

public final class LoginService {

    /**
     * Logs in the given user if there exists one in the database
     * with the given name and password.
     * @return true if login was successful
     */
    public static ResponseResult<Boolean> loginUser(Firestore db, User user) {
        var queryFuture = db
                .collection(DbCollections.USER)
                .whereEqualTo(DbFields.User.NAME, user.getName())
                .whereEqualTo(DbFields.User.PASSWORD, user.getPassword())
                .get();

        return ResponseResult.success(
                FutureUtil.handleFutureGet(() -> {
                    var queryDocs = queryFuture.get().getDocuments();
                    return queryDocs.size() == 1;
                })
        );
    }
}
