package hu.kristof.nagy.hikebookserver.service.authentication;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.User;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private Firestore db;

    /**
     * Logs in the given user if there exists one in the database
     * with the given name and password.
     * @return true if login was successful
     */
    public ResponseResult<Boolean> loginUser(User user) {
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
