package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.PointType;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.TestUtils;
import hu.kristof.nagy.hikebookserver.service.route.userroute.UserRouteCreateService;
import hu.kristof.nagy.hikebookserver.service.route.userroute.UserRouteLoadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserRouteLoadTest {
    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanRoutes(db);
    }

    @Test
    void testLoadRoutesForUserEmpty() {
        List<UserRoute> routes = UserRouteLoadService.loadUserRoutes(db, "asd").getSuccessResult();

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
        UserRouteCreateService.createRoute(db,
                new UserRoute(routeName, points, "", userName)
        );

        List<UserRoute> routes = UserRouteLoadService.loadUserRoutes(db, "asd").getSuccessResult();

        assertEquals(routeName, routes.get(0).getRouteName());
        assertEquals("", routes.get(0).getDescription());
        assertEquals(points, routes.get(0).getPoints());
    }

    @Test
    void testBrowseList() {
        var points = new ArrayList<Point>();
        var p1 = new Point(0.0, 0.0, PointType.SET, "");
        points.add(p1);
        var p2 = new Point(1.0, 1.0, PointType.NEW, "");
        points.add(p2);
        var userName = "asd";
        var routeName = "route";
        UserRouteCreateService.createRoute(db,
                new UserRoute(routeName, points, "", userName)
        );

        List<BrowseListItem> items = UserRouteLoadService.listUserRoutes(db, "asd").getSuccessResult();
        List<BrowseListItem> items2 = UserRouteLoadService.listUserRoutes(db, "user").getSuccessResult();

        assertEquals(0, items.size());
        assertEquals(1, items2.size());
    }

    @Test
    void testRouteLoad() {
        var points = new ArrayList<Point>();
        var p1 = new Point(0.0, 0.0, PointType.SET, "");
        points.add(p1);
        var p2 = new Point(1.0, 1.0, PointType.NEW, "");
        points.add(p2);
        var userName = "asd";
        var routeName = "route";
        UserRouteCreateService.createRoute(db,
                new UserRoute(routeName, points, "", userName)
        );

        UserRoute userRoute = UserRouteLoadService.loadUserRoute(db, "asd", "route").getSuccessResult();

        assertEquals(userName, userRoute.getUserName());
        assertEquals(routeName, userRoute.getRouteName());
        assertEquals(points, userRoute.getPoints());
        assertEquals("", userRoute.getDescription());
        assertThrows(QueryException.class, () ->
                UserRouteLoadService.loadUserRoute(db, "user", "route")
        );
        assertThrows(QueryException.class, () ->
                UserRouteLoadService.loadUserRoute(db, "asd", "asd")
        );
    }
}
