package hu.kristof.nagy.hikebookserver.service.hike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
            return Util.handleListSize(queryDocs, documentSnapshots ->
                    (QueryDocumentSnapshot) documentSnapshots.get(0)
            );
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
