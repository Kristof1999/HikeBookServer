package hu.kristof.nagy.hikebookserver.service.route.routeuniqueness;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.List;

public class SimpleRouteUniquenessHandler extends RouteUniquenessHandler {
    public SimpleRouteUniquenessHandler(
            Firestore db,
            String ownerName,
            String ownerPath,
            String routeName,
            List<Point> points
    ) {
        this.db = db;
        this.ownerName = ownerName;
        this.ownerPath = ownerPath;
        this.routeName = routeName;
        this.points = points;
    }

    @Override
    protected boolean arePointsUnique() {
        var queryFuture = arePointsUniqueQuery().get();
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }

    @Override
    protected boolean isRouteNameUnique() {
        var queryFuture = isRouteNameUniqueQuery().get();
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }
}
