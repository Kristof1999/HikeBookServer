package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GroupCreateServiceTest {

    @Autowired
    private GroupCreateService groupCreateService;

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

        boolean res = groupCreateService.createGroup(groupName, userName).getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testNameExists() {
        var groupName = "group";
        var userName = "user";

        groupCreateService.createGroup(groupName, userName);

        assertThrows(IllegalArgumentException.class, () ->
                groupCreateService.createGroup(groupName, userName)
        );
        assertThrows(IllegalArgumentException.class, () ->
                groupCreateService.createGroup(groupName, userName + "diff")
        );
    }
}
