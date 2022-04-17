//package hu.kristof.nagy.hikebookserver.service;
//
//import com.google.cloud.firestore.Firestore;
//import hu.kristof.nagy.hikebookserver.model.*;
//import hu.kristof.nagy.hikebookserver.model.routes.EditedUserRoute;
//import hu.kristof.nagy.hikebookserver.model.routes.Route;
//import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
//import hu.kristof.nagy.hikebookserver.service.route.RouteCreateService;
//import hu.kristof.nagy.hikebookserver.service.route.RouteEditService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class UserRouteEditTest {
//    @Autowired
//    private RouteEditService routeEditService;
//
//    @Autowired
//    private RouteCreateService routeCreateService;
//
//    @Autowired
//    private Firestore db;
//
//    @BeforeEach
//    void setUp() {
//        TestUtils.cleanRoutes(db);
//    }
//
//    @Test
//    void testSameRouteNameSamePoints() {
//        List<Point> points = new ArrayList<>();
//        points.add(new Point(0.0, 0.0, PointType.SET, ""));
//        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
//        String userName = "asd";
//        String routeName = "route";
//        Route oldRoute = new UserRoute(routeName, points, "", userName);
//        routeCreateService.createRoute(oldRoute, );
//
//        Route newRoute = new Route(userName, RouteType.USER,  routeName, points, "");
//        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);
//        boolean res = routeEditService.editRoute(editedRoute);
//
//        assertTrue(res);
//    }
//
//    @Test
//    void testSameRouteNameDifferentPoints() {
//        List<Point> points = new ArrayList<>();
//        points.add(new Point(0.0, 0.0, PointType.SET, ""));
//        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
//        String userName = "asd";
//        String routeName = "route";
//        Route oldRoute = new Route(userName, RouteType.USER,  routeName, points, "");
//        routeCreateService.createRoute(oldRoute);
//
//        points.add(new Point(2.0, 2.0, PointType.NEW, ""));
//        Route newRoute = new Route(userName, RouteType.USER,  routeName, points, "");
//        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);
//        boolean res = routeEditService.editRoute(editedRoute);
//
//        assertTrue(res);
//    }
//
//    @Test
//    void testDifferentRouteNameSamePoints() {
//        List<Point> points = new ArrayList<>();
//        points.add(new Point(0.0, 0.0, PointType.SET, ""));
//        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
//        String userName = "asd";
//        String routeName = "route";
//        Route oldRoute = new Route(userName, RouteType.USER,  routeName, points, "");
//        routeCreateService.createRoute(oldRoute);
//
//        Route newRoute = new Route(userName, RouteType.USER,  routeName + "2", points, "");
//        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);
//        boolean res = routeEditService.editRoute(editedRoute);
//
//        assertTrue(res);
//    }
//
//    @Test
//    void testDifferentRouteNameDifferentPoints() {
//        List<Point> points = new ArrayList<>();
//        points.add(new Point(0.0, 0.0, PointType.SET, ""));
//        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
//        String userName = "asd";
//        String routeName = "route";
//        Route oldRoute = new Route(userName, RouteType.USER,  routeName, points, "");
//        routeCreateService.createRoute(oldRoute);
//
//        points.add(new Point(2.0, 2.0, PointType.NEW, ""));
//        Route newRoute = new Route(userName, RouteType.USER,  routeName + "2", points, "");
//        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);
//        boolean res = routeEditService.editRoute(editedRoute);
//
//        assertTrue(res);
//    }
//
//    @Test
//    void testPointsExist() {
//        List<Point> points = new ArrayList<>();
//        points.add(new Point(0.0, 0.0, PointType.SET, ""));
//        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
//        String userName = "asd";
//        String routeName = "route";
//        Route oldRoute = new Route(userName, RouteType.USER,  routeName, points, "");
//        routeCreateService.createRoute(oldRoute);
//        String routeName2 = routeName + "2";
//        List<Point> points2 = new ArrayList<>();
//        for (Point p : points) {
//            points2.add(p);
//        }
//        points2.add(new Point(2.0, 2.0, PointType.NEW, ""));
//        routeCreateService.createRoute(
//                new Route(userName, RouteType.USER,  routeName2, points2, "")
//        );
//
//        points.add(new Point(2.0, 2.0, PointType.NEW, ""));
//        Route newRoute = new Route(userName, RouteType.USER,  routeName, points, "");
//        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);
//
//        assertThrows(IllegalArgumentException.class,
//                () -> routeEditService.editRoute(editedRoute)
//        );
//    }
//
//    @Test
//    void testRouteNameExist() {
//        List<Point> points = new ArrayList<>();
//        points.add(new Point(0.0, 0.0, PointType.SET, ""));
//        points.add(new Point(1.0, 1.0, PointType.NEW, ""));
//        String userName = "asd";
//        String routeName = "route";
//        Route oldRoute = new Route(userName, RouteType.USER, routeName, points, "");
//        routeCreateService.createRoute(oldRoute);
//        String routeName2 = routeName + "2";
//        List<Point> points2 = new ArrayList<>();
//        for (Point p : points) {
//            points2.add(p);
//        }
//        points2.add(new Point(2.0, 2.0, PointType.NEW, ""));
//        routeCreateService.createRoute(
//                new Route(userName, RouteType.USER,  routeName2, points2, "")
//        );
//
//        Route newRoute = new Route(userName, RouteType.USER,  routeName2, points, "");
//        EditedUserRoute editedRoute = new EditedUserRoute(newRoute, oldRoute);
//
//        assertThrows(IllegalArgumentException.class,
//                () -> routeEditService.editRoute(editedRoute)
//        );
//    }
//
//    void testPointTypeChange() {
//        //TODO
//        //test if we only change the type of one of the points,
//        //it returns true
//    }
//
//    void testDescriptionChange() {
//        // TODO
//    }
//}
