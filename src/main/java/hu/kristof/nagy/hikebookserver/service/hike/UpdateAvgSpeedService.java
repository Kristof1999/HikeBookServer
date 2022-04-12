package hu.kristof.nagy.hikebookserver.service.hike;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
public class UpdateAvgSpeedService {

    @Autowired
    private Firestore db;

    public void updateAvgSpeed(String userName, Double avgSpeed) {
        // TODO: test
        var users = db.collection(DbPathConstants.COLLECTION_USER);
        var queryFuture = users
                .select(DbPathConstants.USER_AVG_SPEED)
                .whereEqualTo(DbPathConstants.USER_NAME, userName)
                .get();

        QueryDocumentSnapshot userQueryDoc = FutureUtil.handleFutureGet(() -> {
            var queryDocs = queryFuture.get().getDocuments();
            if (queryDocs.size() > 1) {
                throw new QueryException("Got more than 1 query document snapshot, but was expecting only 1. " +
                        "User name: " + userName);
            } else if (queryDocs.size() == 0) {
                throw new QueryException("Got no query document snapshot, but was expecting only 1. " +
                        "User name: " + userName);
            } else {
                return queryDocs.get(0);
            }
        });
        double oldAvgSpeed = Objects.requireNonNull(
                userQueryDoc.getDouble(DbPathConstants.USER_AVG_SPEED)
        );
        double newAvgSpeed = (oldAvgSpeed + avgSpeed) / 2;

        String id = userQueryDoc.getId();
        var userDoc = users.document(id);
        FutureUtil.handleFutureGet(() ->
                userDoc.update(DbPathConstants.USER_AVG_SPEED, newAvgSpeed)
                        .get() // wait for write result
        );
    }
}
