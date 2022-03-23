package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.PointType;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import hu.kristof.nagy.hikebookserver.service.userroute.UserRouteCreateService;
import hu.kristof.nagy.hikebookserver.service.userroute.UserRouteDeleteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserRouteDeleteTest {

    @Autowired
    private UserRouteDeleteService routeDeleteService;

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
        String userName = "asd";
        String routeName = "route";
        routeCreateService.createUserRoute(new UserRoute(userName, routeName, points, ""));

        boolean res = routeDeleteService.deleteUserRoute(userName, routeName);

        assertTrue(res);
        // TODO: assert with route loading
    }

    @Test
    void testNonExistent() {
        boolean res = routeDeleteService.deleteUserRoute("","");

        assertFalse(res);
    }
}
