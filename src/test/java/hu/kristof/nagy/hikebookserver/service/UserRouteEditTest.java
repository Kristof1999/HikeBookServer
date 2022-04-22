package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.PointType;
import hu.kristof.nagy.hikebookserver.model.routes.EditedUserRoute;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.route.userroute.UserRouteCreateService;
import hu.kristof.nagy.hikebookserver.service.route.userroute.UserRouteEditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        UserRoute oldRoute = new UserRoute(routeName, points, "", userName);
        routeCreateService.createRoute(oldRoute);

        UserRoute newRoute = new UserRoute(routeName, points, "", userName);
        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);
        boolean res = routeEditService.editRoute(editedRoute).getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testSameRouteNameDifferentPoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        UserRoute oldRoute = new UserRoute(routeName, points, "", userName);
        routeCreateService.createRoute(oldRoute);

        points.add(new Point(2.0, 2.0, PointType.NEW, ""));
        UserRoute newRoute = new UserRoute(routeName, points, "", userName);
        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);
        boolean res = routeEditService.editRoute(editedRoute).getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testDifferentRouteNameSamePoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        UserRoute oldRoute = new UserRoute(routeName, points, "", userName);
        routeCreateService.createRoute(oldRoute);

        UserRoute newRoute = new UserRoute(routeName + "2", points, "", userName);
        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);
        boolean res = routeEditService.editRoute(editedRoute).getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testDifferentRouteNameDifferentPoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        UserRoute oldRoute = new UserRoute(routeName, points, "", userName);
        routeCreateService.createRoute(oldRoute);

        points.add(new Point(2.0, 2.0, PointType.NEW, ""));
        UserRoute newRoute = new UserRoute(routeName + "2", points, "", userName);
        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);
        boolean res = routeEditService.editRoute(editedRoute).getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testPointsExist() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        UserRoute oldRoute = new UserRoute(routeName, points, "", userName);
        routeCreateService.createRoute(oldRoute);
        String routeName2 = routeName + "2";
        List<Point> points2 = new ArrayList<>();
        for (Point p : points) {
            points2.add(p);
        }
        points2.add(new Point(2.0, 2.0, PointType.NEW, ""));
        routeCreateService.createRoute(
                new UserRoute(routeName2, points2, "", userName)
        );

        points.add(new Point(2.0, 2.0, PointType.NEW, ""));
        UserRoute newRoute = new UserRoute(routeName, points, "", userName);
        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);

        assertThrows(IllegalArgumentException.class,
                () -> routeEditService.editRoute(editedRoute)
        );
    }

    @Test
    void testRouteNameExist() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0, PointType.SET, ""));
        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
        String userName = "asd";
        String routeName = "route";
        UserRoute oldRoute = new UserRoute(routeName, points, "", userName);
        routeCreateService.createRoute(oldRoute);
        String routeName2 = routeName + "2";
        List<Point> points2 = new ArrayList<>();
        for (Point p : points) {
            points2.add(p);
        }
        points2.add(new Point(2.0, 2.0, PointType.NEW, ""));
        routeCreateService.createRoute(
                new UserRoute(routeName2, points2, "", userName)
        );

        UserRoute newRoute = new UserRoute(routeName2, points, "", userName);
        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);

        assertThrows(IllegalArgumentException.class,
                () -> routeEditService.editRoute(editedRoute)
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
