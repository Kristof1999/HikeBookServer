package hu.kristof.nagy.hikebookserver.service.hike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;

import java.util.Objects;

public final class UpdateAvgSpeedService {
    public static void updateAvgSpeed(Firestore db, String userName, Double avgSpeed) {
        var users = db.collection(DbCollections.USER);
        var queryFuture = users
                .select(DbFields.User.AVG_SPEED)
                .whereEqualTo(DbFields.User.NAME, userName)
                .get();

        QueryDocumentSnapshot userQueryDoc = FutureUtil.handleFutureGet(() -> {
            var queryDocs = queryFuture.get().getDocuments();
            return Util.handleListSize(queryDocs, documentSnapshots ->
                    (QueryDocumentSnapshot) documentSnapshots.get(0)
            );
        });
        double oldAvgSpeed = Objects.requireNonNull(
                userQueryDoc.getDouble(DbFields.User.AVG_SPEED)
        );
        double newAvgSpeed = (oldAvgSpeed + avgSpeed) / 2;

        String id = userQueryDoc.getId();
        var userDoc = users.document(id);
        FutureUtil.handleFutureGet(() ->
                userDoc.update(DbFields.User.AVG_SPEED, newAvgSpeed)
                        .get() // wait for write result
        );
    }
}
