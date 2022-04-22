package hu.kristof.nagy.hikebookserver.service;

// For local emulator suite: set environment variables:
// FIRESTORE_EMULATOR_HOST=localhost:8000
// GCLOUD_PROJECT=hikebook

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.PointType;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.route.userroute.UserRouteCreateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRouteCreateTest {

    @Autowired
    private UserRouteCreateService routeCreateService;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanRoutes(db);
    }

    @Test
    void testCorrect() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));

        boolean res = routeCreateService.createRoute(
                new UserRoute("route", points, "", "asd")
        ).getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testDuplicateName() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        List<Point> points2 = new ArrayList<>();
        points2.add(new Point(0.0, 0.0, PointType.SET, ""));
        points2.add(new Point(1.0, 0.0, PointType.NEW, ""));

        routeCreateService.createRoute(
                new UserRoute("route", points, "", "asd")
        );

        assertThrows(IllegalArgumentException.class, () ->
            routeCreateService.createRoute(
                    new UserRoute("route", points2, "", "asd")
            )
        );
    }

    @Test
    void testDuplicatePoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));

        routeCreateService.createRoute(
                new UserRoute("route", points, "", "asd")
        );

        assertThrows(IllegalArgumentException.class, () ->
                routeCreateService.createRoute(
                        new UserRoute("route", points, "", "asd")
                )
        );
    }

    @Test
    void testDifferentUserNameSamePoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));

        routeCreateService.createRoute(
                new UserRoute("route", points, "", "asd")
        );
        boolean res = routeCreateService.createRoute(
                new UserRoute("route", points, "", "user")
        ).getSuccessResult();

        assertTrue(res);
    }
}
