package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.PointType;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import hu.kristof.nagy.hikebookserver.service.userroute.UserRouteCreateService;
import hu.kristof.nagy.hikebookserver.service.userroute.UserRouteEditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRouteEditTest {
    @Autowired
    private UserRouteEditService routeEditService;

    @Autowired
    private UserRouteCreateService routeCreateService;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanRoutes(db);
    }

    @Test
    void testSameRouteNameSamePoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createUserRoute(new UserRoute(userName, routeName, points, ""));

        UserRoute editedUserRoute = new UserRoute(userName, routeName, points, "");

        assertThrows(IllegalArgumentException.class,
                () -> routeEditService.editUserRoute(routeName, editedUserRoute)
        );
    }

    @Test
    void testSameRouteNameDifferentPoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createUserRoute(new UserRoute(userName, routeName, points, ""));

        points.add(new Point(2.0, 2.0, PointType.NEW, ""));
        UserRoute editedUserRoute = new UserRoute(userName, routeName, points, "");
        boolean res = routeEditService.editUserRoute(routeName, editedUserRoute);

        assertTrue(res);
    }

    @Test
    void testDifferentRouteNameSamePoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createUserRoute(
                new UserRoute(userName, routeName, points, "")
        );

        UserRoute editedUserRoute = new UserRoute(userName, routeName + "2", points, "");
        boolean res = routeEditService.editUserRoute(routeName, editedUserRoute);

        assertTrue(res);
    }

    @Test
    void testDifferentRouteNameDifferentPoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createUserRoute(
                new UserRoute(userName, routeName, points, "")
        );

        points.add(new Point(2.0, 2.0, PointType.NEW, ""));
        UserRoute editedUserRoute = new UserRoute(userName, routeName + "2", points, "");
        boolean res = routeEditService.editUserRoute(routeName, editedUserRoute);

        assertTrue(res);
    }

    @Test
    void testNonExistentRoute() {
        assertThrows(IllegalArgumentException.class,
                () -> routeEditService.editUserRoute("",
                new UserRoute("", "", new ArrayList<>(), "")
        ));
    }

    @Test
    void testPointsExist() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createUserRoute(
                new UserRoute(userName, routeName, points, "")
        );
        String routeName2 = routeName + "2";
        List<Point> points2 = new ArrayList<>();
        for (Point p : points) {
            points2.add(p);
        }
        points2.add(new Point(2.0, 2.0, PointType.NEW, ""));
        routeCreateService.createUserRoute(
                new UserRoute(userName, routeName2, points2, "")
        );

        points.add(new Point(2.0, 2.0, PointType.NEW, ""));
        UserRoute editedUserRoute = new UserRoute(userName, routeName, points, "");

        assertThrows(IllegalArgumentException.class,
                () -> routeEditService.editUserRoute(routeName, editedUserRoute)
        );
    }

    @Test
    void testRouteNameExist() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createUserRoute(
                new UserRoute(userName, routeName, points, "")
        );
        String routeName2 = routeName + "2";
        List<Point> points2 = new ArrayList<>();
        for (Point p : points) {
            points2.add(p);
        }
        points2.add(new Point(2.0, 2.0, PointType.NEW, ""));
        routeCreateService.createUserRoute(
                new UserRoute(userName, routeName2, points2, "")
        );

        UserRoute editedUserRoute = new UserRoute(userName, routeName2, points, "");

        assertThrows(IllegalArgumentException.class,
                () -> routeEditService.editUserRoute(routeName, editedUserRoute)
        );
    }
}
