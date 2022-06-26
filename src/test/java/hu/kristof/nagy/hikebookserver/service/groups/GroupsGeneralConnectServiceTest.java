package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.service.TestUtils;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GroupsGeneralConnectServiceTest {
    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanGroups(db);
    }

    @Test
    void testConnect() {
        var groupName = "group";
        var userName = "user";
        var userName2 = userName + "2";
        GroupCreateService.createGroup(db, groupName, userName);

        boolean res = GroupsGeneralConnectService
                .generalConnect(db, groupName, userName2, false)
                .getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testConnectToNonexistentGroup() {
        var groupName = "group";
        var userName = "user";

        assertThrows(IllegalStateException.class, () ->
                GroupsGeneralConnectService
                .generalConnect(db, groupName, userName, false)
        );
    }

    @Test
    void testConnectToSameGroup() {
        var groupName = "group";
        var userName = "user";
        GroupCreateService.createGroup(db, groupName, userName);

        assertThrows(IllegalStateException.class, () ->
                GroupsGeneralConnectService
                .generalConnect(db, groupName, userName, false)
        );
    }

    @Test
    void testDisconnect() {
        var groupName = "group";
        var userName = "user";
        GroupCreateService.createGroup(db, groupName, userName);

        boolean res = GroupsGeneralConnectService
                .generalConnect(db, groupName, userName, true)
                .getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testDisconnectFromNonexistentGroup() {
        var groupName = "group";
        var userName = "user";

        assertThrows(QueryException.class, () ->
                GroupsGeneralConnectService
                .generalConnect(db, groupName, userName, true)
        );
    }
}
