package hu.kristof.nagy.hikebookserver.service.route.routeuniqueness;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.List;

public class GroupRouteUniquenessHandler extends RouteUniquenessHandler {
    private final String groupName;
    private final Transaction transaction;

    public GroupRouteUniquenessHandler(
            Transaction transaction,
            Firestore db,
            String groupName,
            String routeName,
            List<Point> points
    ) {
        this.transaction = transaction;
        this.db = db;
        this.groupName = groupName;
        this.routeName = routeName;
        this.points = points;
    }

    @Override
    public String getOwnerName() {
        return groupName;
    }

    @Override
    public String getOwnerPath() {
        return DbPathConstants.ROUTE_GROUP_NAME;
    }

    @Override
    public boolean arePointsUnique() {
        var query = arePointsUniqueQuery();
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }

    @Override
    public boolean isRouteNameUnique() {
        var query = isRouteNameUniqueQuery();
        var queryFuture = transaction.get(query);
        var queryRes = FutureUtil.handleFutureGet(queryFuture::get);
        return queryRes.isEmpty();
    }
}
