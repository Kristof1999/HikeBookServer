package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.DateTime;
import hu.kristof.nagy.hikebookserver.model.GroupHikeCreateHelper;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.PointType;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.service.TestUtils;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import hu.kristof.nagy.hikebookserver.service.route.grouphikeroute.GroupHikeRouteLoadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GroupHikeGeneralConnectServiceTest {

    @Autowired
    private GroupHikeGeneralConnectService groupHikeGeneralConnectService;

    @Autowired
    private GroupHikeCreateService groupHikeCreateService;

    @Autowired
    private GroupHikeRouteLoadService groupHikeRouteLoadService;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanGroupHikes(db);
        TestUtils.cleanRoutes(db);
    }

    @Test
    void testConnect() {
        var userName = "user";
        var userName2 = userName + "2";
        var groupHikeName = "groupHike";
        var dateTime = new DateTime(2022, 4, 23, 9, 10);
        var points = new ArrayList<Point>();
        points.add(new Point(0.0, 0.0, PointType.NEW, ""));
        var route = new Route("route", points, "");
        var helper = new GroupHikeCreateHelper(dateTime, route);
        groupHikeCreateService.createGroupHike(userName, groupHikeName, helper);

        boolean res = groupHikeGeneralConnectService
                .generalConnect(groupHikeName, userName2, false, dateTime)
                .getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testConnectToNonexistent() {
        var userName = "user";
        var userName2 = userName + "2";
        var groupHikeName = "groupHike";
        var dateTime = new DateTime(2022, 4, 23, 9, 10);

        assertThrows(IllegalArgumentException.class, () ->
                groupHikeGeneralConnectService
                .generalConnect(groupHikeName, userName2, false, dateTime)
        );
    }

    @Test
    void testDisconnect() {
        var userName = "user";
        var userName2 = userName + "2";
        var groupHikeName = "groupHike";
        var dateTime = new DateTime(2022, 4, 23, 9, 10);
        var points = new ArrayList<Point>();
        points.add(new Point(0.0, 0.0, PointType.NEW, ""));
        var route = new Route("route", points, "");
        var helper = new GroupHikeCreateHelper(dateTime, route);
        groupHikeCreateService.createGroupHike(userName, groupHikeName, helper);
        groupHikeGeneralConnectService
                .generalConnect(groupHikeName, userName2, false, dateTime);

        boolean res = groupHikeGeneralConnectService
                .generalConnect(groupHikeName, userName, true, dateTime)
                .getSuccessResult();
        Route routeRes = groupHikeRouteLoadService.loadGroupHikeRoute(groupHikeName).getSuccessResult();
        boolean res2 = groupHikeGeneralConnectService
                .generalConnect(groupHikeName, userName2, true, dateTime)
                .getSuccessResult();

        assertThrows(QueryException.class, () ->
                groupHikeRouteLoadService.loadGroupHikeRoute(groupHikeName)
        );
        assertTrue(res);
        assertTrue(res2);
        assertEquals(route.getRouteName(), routeRes.getRouteName());
        assertEquals(route.getPoints(), routeRes.getPoints());
        assertEquals(
                dateTime + "\n" + route.getDescription(),
                routeRes.getDescription());
    }
}