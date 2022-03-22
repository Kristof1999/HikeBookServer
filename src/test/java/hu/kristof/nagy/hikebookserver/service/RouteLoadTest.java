package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.PointType;
import hu.kristof.nagy.hikebookserver.model.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@SpringBootTest
public class RouteLoadTest {
    @Autowired
    private RouteLoadService routeLoadService;

    @Autowired
    private RouteCreateService routeCreateService;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanRoutes(db);
    }

    @Test
    void testLoadRoutesForUserEmpty() {
        List<Route> routes = routeLoadService.loadRoutesForUser("asd");

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
        routeCreateService.createRoute(
                new Route(userName, routeName, points, "")
        );

        List<Route> routes = routeLoadService.loadRoutesForUser("asd");

        assertEquals(routeName, routes.get(0).getRouteName());
        assertEquals("", routes.get(0).getDescription());
        List<Point> actualPoints = routes.get(0).getPoints();
        for (int i = 0; i < 2; i++) {
            Point lat = actualPoints.get(i);
            assertEquals(points.get(i).getLatitude(), actualPoints.get(i).getLatitude());
            assertEquals(points.get(i).getLongitude(), actualPoints.get(i).getLongitude());
            assertEquals(points.get(i).getTitle(), actualPoints.get(i).getTitle());
            assertEquals(points.get(i).getType(), actualPoints.get(i).getType());
        }
    }


}
