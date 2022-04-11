package hu.kristof.nagy.hikebookserver.model.routes;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupRoute extends Route {
    private final String groupName;

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
        Route route = Route.from(documentSnapshot);
        var groupName = Objects.requireNonNull(
                documentSnapshot.getString(DbPathConstants.ROUTE_GROUP_NAME)
        );
        return new GroupRoute(
                route.getRouteName(), route.getPoints(), route.getDescription(), groupName
        );
    }

    public String getGroupName() {
        return groupName;
    }
}
