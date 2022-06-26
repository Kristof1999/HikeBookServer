package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.service.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GroupsListServiceTest {
    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanGroups(db);
    }

    @Test
    void testListConnectedOne() {
        var groupName = "group";
        var userName = "user";
        GroupCreateService.createGroup(db, groupName, userName);

        List<String> res = GroupsListService.listGroups(db, userName, true).getSuccessResult();

        assertEquals(1, res.size());
        assertEquals(groupName, res.get(0));
    }

    @Test
    void testListConnectedMultipleGroups() {
        var groupName = "group";
        var groupName2 = groupName + "2";
        var userName = "user";
        GroupCreateService.createGroup(db, groupName, userName);
        GroupCreateService.createGroup(db, groupName2, userName);

        List<String> res = GroupsListService.listGroups(db, userName, true).getSuccessResult();

        assertEquals(2, res.size());
        assertThat(res, containsInAnyOrder(groupName, groupName2));
    }

    @Test
    void testListConnectedMultipleUsers() {
        var groupName = "group";
        var userName = "user";
        var userName2 = userName + "2";
        GroupCreateService.createGroup(db, groupName, userName);
        GroupsGeneralConnectService.generalConnect(db, groupName, userName2, false);

        List<String> res = GroupsListService.listGroups(db, userName, true).getSuccessResult();

        assertEquals(1, res.size());
        assertEquals(groupName, res.get(0));
    }

    @Test
    void testListAll() {
        var groupName = "group";
        var userName = "user";
        var userName2 = userName + "2";
        GroupCreateService.createGroup(db, groupName, userName);

        List<String> res = GroupsListService.listGroups(db, userName2, false).getSuccessResult();

        assertEquals(1, res.size());
        assertEquals(groupName, res.get(0));
    }

    @Test
    void testListAllMultipleGroups() {
        var groupName = "group";
        var groupName2 = groupName + "2";
        var userName = "user";
        var userName2 = userName + "2";
        GroupCreateService.createGroup(db, groupName, userName);
        GroupCreateService.createGroup(db, groupName2, userName);

        List<String> res = GroupsListService.listGroups(db, userName2, false).getSuccessResult();

        assertEquals(2, res.size());
        assertThat(res, containsInAnyOrder(groupName, groupName2));
    }

    @Test
    void testListAllMultipleUsers() {
        var groupName = "group";
        var userName = "user";
        var userName2 = userName + "2";
        var userName3 = userName + "3";
        GroupCreateService.createGroup(db, groupName, userName);
        GroupsGeneralConnectService.generalConnect(db, groupName, userName2, false);

        List<String> res = GroupsListService.listGroups(db, userName3, false).getSuccessResult();

        assertEquals(1, res.size());
        assertEquals(groupName, res.get(0));
    }

    @Test
    void testListNotConnected() {
        var groupName = "group";
        var groupName2 = groupName + "2";
        var userName = "user";
        var userName2 = userName + "2";
        GroupCreateService.createGroup(db, groupName, userName);
        GroupCreateService.createGroup(db, groupName2, userName2);

        List<String> res = GroupsListService.listGroups(db, userName2, false).getSuccessResult();

        assertEquals(1, res.size());
        assertEquals(groupName, res.get(0));
    }

    @Test
    void testListNotConnectedMultipleGroups() {
        var groupName = "group";
        var groupName2 = groupName + "2";
        var groupName3 = groupName + "3";
        var userName = "user";
        var userName2 = userName + "2";
        GroupCreateService.createGroup(db, groupName, userName);
        GroupCreateService.createGroup(db, groupName2, userName);
        GroupCreateService.createGroup(db, groupName3, userName2);

        List<String> res = GroupsListService.listGroups(db, userName2, false).getSuccessResult();

        assertEquals(2, res.size());
        assertThat(res, containsInAnyOrder(groupName, groupName2));
    }

    @Test
    void testListNotConnectedMultipleUser() {
        var groupName = "group";
        var groupName2 = groupName + "2";
        var userName = "user";
        var userName2 = userName + "2";
        var userName3 = userName + "3";
        GroupCreateService.createGroup(db, groupName, userName);
        GroupsGeneralConnectService.generalConnect(db, groupName, userName2, false);
        GroupCreateService.createGroup(db, groupName2, userName3);

        List<String> res = GroupsListService.listGroups(db, userName3, false).getSuccessResult();

        assertEquals(1, res.size());
        assertEquals(groupName, res.get(0));
    }
}
