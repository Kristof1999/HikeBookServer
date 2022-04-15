package hu.kristof.nagy.hikebookserver.service.authentication;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.User;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class LoginService {

    @Autowired
    private Firestore db;

    /**
     * Logs in the given user if there exists one in the database
     * with the given name and password.
     * @return true if login was successful
     */
    public boolean loginUser(User user) {
        // TODO: introduce new errors: no such user, and: password is incorrect
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_USER)
                .select(DbPathConstants.USER_NAME, DbPathConstants.USER_PASSWORD)
                .whereEqualTo(DbPathConstants.USER_NAME, user.getName())
                .whereEqualTo(DbPathConstants.USER_PASSWORD, user.getPassword())
                .get();

        return FutureUtil.handleFutureGet(() -> {
            var queryDocs = queryFuture.get().getDocuments();
            return queryDocs.size() == 1;
        });
    }
}
