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
    void testNoChange() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createUserRoute(new UserRoute(userName, routeName, points, ""));

        UserRoute editedUserRoute = new UserRoute(userName, routeName, points, "");
        boolean res = routeEditService.editUserRoute(routeName, editedUserRoute);

        assertFalse(res);
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

        points.remove(0);
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
}
