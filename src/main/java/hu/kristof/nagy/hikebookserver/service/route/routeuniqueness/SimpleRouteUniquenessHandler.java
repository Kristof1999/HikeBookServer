package hu.kristof.nagy.hikebookserver.service.route.routeuniqueness;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.List;

/**
 * A RouteUniquenessHandler which simply checks the uniqueness.
 */
public class SimpleRouteUniquenessHandler extends RouteUniquenessHandler {
    public SimpleRouteUniquenessHandler(Builder builder) {
        this(
                builder.db,
                builder.ownerName,
                builder.ownerPath,
                builder.routeName,
                builder.points
        );
    }

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

    public static class Builder extends RouteUniquenessHandler.Builder<Builder> {
        public Builder(Firestore db) {
            this.db = db;
        }

        @Override
        public SimpleRouteUniquenessHandler build() {
            return new SimpleRouteUniquenessHandler(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
