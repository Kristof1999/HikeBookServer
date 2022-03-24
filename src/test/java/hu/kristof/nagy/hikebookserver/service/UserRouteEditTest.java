package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.EditedUserRoute;
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
        UserRoute oldUserRoute =new UserRoute(userName, routeName, points, "");
        routeCreateService.createUserRoute(oldUserRoute);

        UserRoute newUserRoute = new UserRoute(userName, routeName, points, "");
        EditedUserRoute editedUserRoute = new EditedUserRoute(newUserRoute, oldUserRoute);
        boolean res = routeEditService.editUserRoute(editedUserRoute);

        assertTrue(res);
    }

    @Test
    void testSameRouteNameDifferentPoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        UserRoute oldUserRoute = new UserRoute(userName, routeName, points, "");
        routeCreateService.createUserRoute(oldUserRoute);

        points.add(new Point(2.0, 2.0, PointType.NEW, ""));
        UserRoute newUserRoute = new UserRoute(userName, routeName, points, "");
        EditedUserRoute editedUserRoute = new EditedUserRoute(newUserRoute, oldUserRoute);
        boolean res = routeEditService.editUserRoute(editedUserRoute);

        assertTrue(res);
    }

    @Test
    void testDifferentRouteNameSamePoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        UserRoute oldUserRoute = new UserRoute(userName, routeName, points, "");
        routeCreateService.createUserRoute(oldUserRoute);

        UserRoute newUserRoute = new UserRoute(userName, routeName + "2", points, "");
        EditedUserRoute editedUserRoute = new EditedUserRoute(newUserRoute, oldUserRoute);
        boolean res = routeEditService.editUserRoute(editedUserRoute);

        assertTrue(res);
    }

    @Test
    void testDifferentRouteNameDifferentPoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        UserRoute oldUserRoute = new UserRoute(userName, routeName, points, "");
        routeCreateService.createUserRoute(oldUserRoute);

        points.add(new Point(2.0, 2.0, PointType.NEW, ""));
        UserRoute newUserRoute = new UserRoute(userName, routeName + "2", points, "");
        EditedUserRoute editedUserRoute = new EditedUserRoute(newUserRoute, oldUserRoute);
        boolean res = routeEditService.editUserRoute(editedUserRoute);

        assertTrue(res);
    }

    @Test
    void testPointsExist() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        UserRoute oldUserRoute = new UserRoute(userName, routeName, points, "");
        routeCreateService.createUserRoute(oldUserRoute);
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
        UserRoute newUserRoute = new UserRoute(userName, routeName, points, "");
        EditedUserRoute editedUserRoute = new EditedUserRoute(newUserRoute, oldUserRoute);

        assertThrows(IllegalArgumentException.class,
                () -> routeEditService.editUserRoute(editedUserRoute)
        );
    }

    @Test
    void testRouteNameExist() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        UserRoute oldUserRoute = new UserRoute(userName, routeName, points, "");
        routeCreateService.createUserRoute(oldUserRoute);
        String routeName2 = routeName + "2";
        List<Point> points2 = new ArrayList<>();
        for (Point p : points) {
            points2.add(p);
        }
        points2.add(new Point(2.0, 2.0, PointType.NEW, ""));
        routeCreateService.createUserRoute(
                new UserRoute(userName, routeName2, points2, "")
        );

        UserRoute newUserRoute = new UserRoute(userName, routeName2, points, "");
        EditedUserRoute editedUserRoute = new EditedUserRoute(newUserRoute, oldUserRoute);

        assertThrows(IllegalArgumentException.class,
                () -> routeEditService.editUserRoute(editedUserRoute)
        );
    }

    void testPointTypeChange() {
        //TODO
        //test if we only change the type of one of the points,
        //it returns true
    }

    void testDescriptionChange() {
        // TODO
    }
}
