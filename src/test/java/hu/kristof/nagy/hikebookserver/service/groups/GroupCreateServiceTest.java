package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.service.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GroupCreateServiceTest {
    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanGroups(db);
    }

    @Test
    void testFirst() {
        var groupName = "group";
        var userName = "user";

        boolean res = GroupCreateService.createGroup(db, groupName, userName).getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testNameExists() {
        var groupName = "group";
        var userName = "user";

        GroupCreateService.createGroup(db, groupName, userName);

        assertThrows(IllegalArgumentException.class, () ->
                GroupCreateService.createGroup(db, groupName, userName)
        );
        assertThrows(IllegalArgumentException.class, () ->
                GroupCreateService.createGroup(db, groupName, userName + "diff")
        );
    }
}
