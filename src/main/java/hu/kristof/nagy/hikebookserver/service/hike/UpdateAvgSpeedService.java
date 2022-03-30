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
        CollectionReference users = db
                .collection(DbPathConstants.COLLECTION_USER);
        ApiFuture<QuerySnapshot> future = users
                .select(DbPathConstants.USER_AVG_SPEED)
                .whereEqualTo(DbPathConstants.USER_NAME, userName)
                .get();
        try {
            QueryDocumentSnapshot userQueryDoc = future.get()
                    .getDocuments()
                    .get(0);
            double oldAvgSpeed = Objects.requireNonNull(userQueryDoc
                    .getDouble(DbPathConstants.USER_AVG_SPEED));
            double newAvgSpeed = (oldAvgSpeed + avgSpeed) / 2;

            String id = userQueryDoc.getId();
            DocumentReference userDoc = users.document(id);
            userDoc.update(DbPathConstants.USER_AVG_SPEED, newAvgSpeed)
                    .get(); // wait for write result
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
