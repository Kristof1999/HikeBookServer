package hu.kristof.nagy.hikebookserver.model.routes;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.SimpleRouteUniquenessHandler;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.TransactionRouteUniquenessHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GroupRoute extends Route {
    private String groupName;

    public GroupRoute() {
    }

    public GroupRoute(Route route, String groupName) {
        this(route.getRouteName(), route.getPoints(), route.getDescription(), groupName);
    }

    public GroupRoute(String routeName, List<Point> points, String description, String groupName) {
        super(routeName, points, description);
        this.groupName = groupName;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> data = super.toMap();
        data.put(DbPathConstants.ROUTE_GROUP_NAME, groupName);
        return data;
    }

    public static GroupRoute from(DocumentSnapshot documentSnapshot) {
        return Objects.requireNonNull(
                documentSnapshot.toObject(GroupRoute.class)
        );
    }

    public static String[] getSelectPaths() {
        String[] routeSelectPaths = Route.getSelectPaths();
        String[] groupRouteSelectPaths = Arrays.copyOf(
                routeSelectPaths, routeSelectPaths.length + 1
        );
        groupRouteSelectPaths[groupRouteSelectPaths.length - 1] = DbPathConstants.ROUTE_GROUP_NAME;
        return groupRouteSelectPaths;
    }

    public void handleRouteUniqueness(
            Transaction transaction,
            Firestore db
    ) {
        var handler = new TransactionRouteUniquenessHandler(
                transaction,
                db,
                groupName,
                DbPathConstants.ROUTE_GROUP_NAME,
                routeName,
                points
        );
        handler.handleRouteUniqueness();
    }

    public void handleRouteNameUniqueness(
            Transaction transaction,
            Firestore db
    ) {
        var handler = new TransactionRouteUniquenessHandler(
                transaction,
                db,
                groupName,
                DbPathConstants.ROUTE_GROUP_NAME,
                routeName,
                points
        );
        handler.handleRouteNameUniqueness();
    }

    public void handlePointsUniqueness(
            Transaction transaction,
            Firestore db
    ) {
        var handler = new TransactionRouteUniquenessHandler(
                transaction,
                db,
                groupName,
                DbPathConstants.ROUTE_GROUP_NAME,
                routeName,
                points
        );
        handler.handlePointsUniqueness();
    }

    public String getGroupName() {
        return groupName;
    }
}
