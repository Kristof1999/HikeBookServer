package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.DateTime;
import hu.kristof.nagy.hikebookserver.model.GroupHikeCreateHelper;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.PointType;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.service.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GroupHikeCreateServiceTest {

    @Autowired
    private GroupHikeCreateService groupHikeCreateService;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanGroupHikes(db);
        TestUtils.cleanRoutes(db);
    }

    @Test
    void testCreate() {
        var userName = "user";
        var groupHikeName = "groupHike";
        var dateTime = new DateTime(2022, 4, 23, 9, 10);
        var points = new ArrayList<Point>();
        points.add(new Point(0.0, 0.0, PointType.NEW, ""));
        var route = new Route("route", points, "");
        var helper = new GroupHikeCreateHelper(dateTime, route);

        boolean res = groupHikeCreateService
                .createGroupHike(userName, groupHikeName, helper)
                .getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testGroupHikeNameExists() {
        var userName = "user";
        var groupHikeName = "groupHike";
        var dateTime = new DateTime(2022, 4, 23, 9, 10);
        var points = new ArrayList<Point>();
        points.add(new Point(0.0, 0.0, PointType.NEW, ""));
        var route = new Route("route", points, "");
        var helper = new GroupHikeCreateHelper(dateTime, route);
        groupHikeCreateService.createGroupHike(userName, groupHikeName, helper);

        assertThrows(IllegalArgumentException.class, () -> groupHikeCreateService
                .createGroupHike(userName, groupHikeName, helper)
        );
    }
}
