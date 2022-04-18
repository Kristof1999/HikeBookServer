package hu.kristof.nagy.hikebookserver.service.route.routeuniqueness;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.List;

public class TransactionRouteUniquenessHandler extends RouteUniquenessHandler {
    private final Transaction transaction;

    public TransactionRouteUniquenessHandler(Builder builder) {
        this(
                builder.transaction,
                builder.db,
                builder.ownerName,
                builder.ownerPath,
                builder.routeName,
                builder.points
        );
    }

    public TransactionRouteUniquenessHandler(
            Transaction transaction,
            Firestore db,
            String ownerName,
            String ownerPath,
            String routeName,
            List<Point> points
    ) {
        this.transaction = transaction;
        this.db = db;
        this.ownerName = ownerName;
        this.ownerPath = ownerPath;
        this.routeName = routeName;
        this.points = points;
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

    public static class Builder extends RouteUniquenessHandler.Builder<Builder> {
        private final Transaction transaction;

        public Builder(Firestore db, Transaction transaction) {
            this.db = db;
            this.transaction = transaction;
        }

        @Override
        public TransactionRouteUniquenessHandler build() {
            return new TransactionRouteUniquenessHandler(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
