package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.PointType;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserRouteLoadTest {
    @Autowired
    private UserRouteLoadService routeLoadService;

    @Autowired
    private UserRouteCreateService routeCreateService;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanRoutes(db);
    }

    @Test
    void testLoadRoutesForUserEmpty() {
        List<UserRoute> routes = routeLoadService.loadUserRoutesForUser("asd");

        assertEquals(0, routes.size());
    }

    @Test
    void testLoadRoutesForUserMore() {
        List<Point> points = new ArrayList<>();
        Point p1 = new Point(0.0, 0.0, PointType.SET, "");
        points.add(p1);
        Point p2 = new Point(1.0, 1.0, PointType.NEW, "");
        points.add(p2);
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createUserRoute(
                new UserRoute(userName, routeName, points, "")
        );

        List<UserRoute> routes = routeLoadService.loadUserRoutesForUser("asd");

        assertEquals(routeName, routes.get(0).getRouteName());
        assertEquals("", routes.get(0).getDescription());
        assertEquals(points, routes.get(0).getPoints());
    }


}
