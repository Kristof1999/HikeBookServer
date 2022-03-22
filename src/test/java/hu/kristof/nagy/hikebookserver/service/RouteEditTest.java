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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RouteEditTest {
    @Autowired
    private RouteEditService routeEditService;

    @Autowired
    private RouteCreateService routeCreateService;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanRoutes(db);
    }

    @Test
    void testSameRouteName() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createRoute(userName, routeName, points, "");

        points.remove(0);
        boolean res = routeEditService.editRoute(userName, routeName, new Route(routeName, points, ""));

        assertTrue(res);
    }

    @Test
    void testDifferentRouteName() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createRoute(userName, routeName, points, "");

        points.remove(0);
        boolean res = routeEditService.editRoute(userName, routeName,
                new Route(routeName + "2", points, "")
        );

        assertTrue(res);
    }

    @Test
    void testNonExistentRoute() {
        boolean res = routeEditService.editRoute("", "",
                new Route("", new ArrayList<>(), "")
        );

        assertFalse(res);
    }

    @Test
    void testSamePoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createRoute(userName, routeName, points, "");

        boolean res = routeEditService.editRoute(userName, routeName,
                new Route(routeName + "2", points, "")
        );

        assertFalse(res);
    }
}