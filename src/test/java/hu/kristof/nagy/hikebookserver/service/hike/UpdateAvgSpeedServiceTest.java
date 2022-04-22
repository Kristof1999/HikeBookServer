package hu.kristof.nagy.hikebookserver.service.hike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
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
    private UpdateAvgSpeedService updateAvgSpeedService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanUsers(db);
    }

    @Test
    void testFirstUpdate() {
        registerService.registerUser(new User("user", "password", 3.0));

        updateAvgSpeedService.updateAvgSpeed("user", 3.5);

        assertEquals(3.25, getSpeed("user"), 0.05);
    }

    @Test
    void testMultipleUpdates() {
        registerService.registerUser(new User("user", "password", 3.0));

        updateAvgSpeedService.updateAvgSpeed("user", 3.5);
        updateAvgSpeedService.updateAvgSpeed("user", 3.75);

        assertEquals(3.5, getSpeed("user"), 0.05);
    }

    private double getSpeed(String userName) {
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
        return Objects.requireNonNull(
                userQueryDoc.getDouble(DbPathConstants.USER_AVG_SPEED)
        );
    }
}
