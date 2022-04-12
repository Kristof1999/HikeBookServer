package hu.kristof.nagy.hikebookserver.model.routes;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class UserRoute extends Route {
    private final String userName;

    public UserRoute(Route route, String userName) {
        this(route.getRouteName(), route.getPoints(), route.getDescription(), userName);
    }

    public UserRoute(String routeName, List<Point> points, String description, String userName) {
        super(routeName, points, description);
        this.userName = userName;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> data = super.toMap();
        data.put(DbPathConstants.ROUTE_USER_NAME, userName);
        return data;
    }

    public static UserRoute from(DocumentSnapshot documentSnapshot) {
        Route route = Route.from(documentSnapshot);
        var userName = Objects.requireNonNull(
                documentSnapshot.getString(DbPathConstants.ROUTE_USER_NAME)
        );
        return new UserRoute(
                route.getRouteName(), route.getPoints(), route.getDescription(), userName
        );
    }

    public String getUserName() {
        return userName;
    }
}
