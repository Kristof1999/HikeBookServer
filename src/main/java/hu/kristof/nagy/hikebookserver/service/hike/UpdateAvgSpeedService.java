package hu.kristof.nagy.hikebookserver.service.hike;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
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
        try {
            var userQueryDoc = queryFuture.get()
                    .getDocuments()
                    .get(0);
            double oldAvgSpeed = Objects.requireNonNull(
                    userQueryDoc.getDouble(DbPathConstants.USER_AVG_SPEED)
            );
            double newAvgSpeed = (oldAvgSpeed + avgSpeed) / 2;

            String id = userQueryDoc.getId();
            var userDoc = users.document(id);
            userDoc.update(DbPathConstants.USER_AVG_SPEED, newAvgSpeed)
                    .get(); // wait for write result
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
