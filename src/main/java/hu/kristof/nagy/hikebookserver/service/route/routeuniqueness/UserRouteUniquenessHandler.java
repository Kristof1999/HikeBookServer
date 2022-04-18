package hu.kristof.nagy.hikebookserver.service.route.routeuniqueness;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.List;

public class UserRouteUniquenessHandler extends RouteUniquenessHandler {
    private final String userName;

    public UserRouteUniquenessHandler(
            Firestore db,
            String userName,
            String routeName,
            List<Point> points
    ) {
        this.db = db;
        this.userName = userName;
        this.routeName = routeName;
        this.points = points;
    }

    @Override
    public String getOwnerName() {
        return userName;
    }

    @Override
    public String getOwnerPath() {
        return DbPathConstants.ROUTE_USER_NAME;
    }

    @Override
    public boolean arePointsUnique() {
        var queryFuture = arePointsUniqueQuery().get();
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }

    @Override
    public boolean isRouteNameUnique() {
        var queryFuture = isRouteNameUniqueQuery().get();
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }
}
