package hu.kristof.nagy.hikebookserver.service.hike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.User;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.TestUtils;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.authentication.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UpdateAvgSpeedServiceTest {
    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanUsers(db);
    }

    @Test
    void testFirstUpdate() {
        RegisterService.registerUser(db, new User("user", "password", 3.0));

        UpdateAvgSpeedService.updateAvgSpeed(db, "user", 3.5);

        assertEquals(3.25, getSpeed("user"), 0.05);
    }

    @Test
    void testMultipleUpdates() {
        RegisterService.registerUser(db, new User("user", "password", 3.0));

        UpdateAvgSpeedService.updateAvgSpeed(db, "user", 3.5);
        UpdateAvgSpeedService.updateAvgSpeed(db, "user", 3.75);

        assertEquals(3.5, getSpeed("user"), 0.05);
    }

    private double getSpeed(String userName) {
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
        return Objects.requireNonNull(
                userQueryDoc.getDouble(DbFields.User.AVG_SPEED)
        );
    }
}
