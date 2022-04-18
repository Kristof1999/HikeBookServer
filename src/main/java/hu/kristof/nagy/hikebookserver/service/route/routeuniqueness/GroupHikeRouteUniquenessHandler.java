package hu.kristof.nagy.hikebookserver.service.route.routeuniqueness;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.List;

public class GroupHikeRouteUniquenessHandler extends RouteUniquenessHandler {
    private final String groupHikeName;
    private final Transaction transaction;

    public GroupHikeRouteUniquenessHandler(
            Transaction transaction,
            Firestore db,
            String groupHikeName,
            String routeName,
            List<Point> points
    ) {
        this.transaction = transaction;
        this.db = db;
        this.groupHikeName = groupHikeName;
        this.routeName = routeName;
        this.points = points;
    }

    @Override
    public String getOwnerName() {
        return groupHikeName;
    }

    @Override
    public String getOwnerPath() {
        return DbPathConstants.ROUTE_GROUP_HIKE_NAME;
    }

    @Override
    protected boolean arePointsUnique() {
        var query = arePointsUniqueQuery();
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }

    @Override
    protected boolean isRouteNameUnique() {
        var query = isRouteNameUniqueQuery();
        var queryFuture = transaction.get(query);
        var queryRes = FutureUtil.handleFutureGet(queryFuture::get);
        return queryRes.isEmpty();
    }
}
