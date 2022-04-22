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
public class GroupsMembersListServiceTest {

    @Autowired
    private GroupsMembersListService groupsMembersListService;

    @Autowired
    private GroupCreateService groupCreateService;

    @Autowired
    private GroupsGeneralConnectService groupsGeneralConnectService;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanGroups(db);
    }

    @Test
    void testOneMember() {
        var groupName = "group";
        var userName = "user";
        groupCreateService.createGroup(groupName, userName);

        List<String> res = groupsMembersListService.listMembers(groupName).getSuccessResult();

        assertEquals(1, res.size());
        assertEquals(userName, res.get(0));
    }

    @Test
    void testMultipleMember() {
        var groupName = "group";
        var userName = "user";
        var userName2 = userName + "2";
        groupCreateService.createGroup(groupName, userName);
        groupsGeneralConnectService.generalConnect(groupName, userName2, false);

        List<String> res = groupsMembersListService.listMembers(groupName).getSuccessResult();

        assertEquals(2, res.size());
        assertThat(res, containsInAnyOrder(userName, userName2));
    }

    @Test
    void testEmpty() {
        var groupName = "group";

        List<String> res = groupsMembersListService.listMembers(groupName).getSuccessResult();

        assertEquals(0, res.size());
    }
}
