package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.service.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GroupsListServiceTest {

    @Autowired
    private GroupsListService groupsListService;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanGroups(db);
    }

    void testListConnectedOne() {

    }

    void testListConnectedMultipleGroups() {

    }

    void testListConnectedMultipleUsers() {

    }

    void testListNotConnectedOne() {

    }

    void testListNotConnectedMultipleGroups() {

    }

    void testListNotConnectedMultipleUsers() {

    }

    void testListAll() {

    }

    void testListAllMultipleUsers() {

    }
}
