package hu.kristof.nagy.hikebookserver.service.route.grouproute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.TransactionRouteUniquenessHandler;

import java.util.Map;

public class GroupRouteCreateService {
    public static ResponseResult<Boolean> createRoute(Firestore db,GroupRoute route) {
        var transactionFuture = db.runTransaction(transaction -> {
            route.handleRouteUniqueness(new TransactionRouteUniquenessHandler
                    .Builder(db, transaction)
            );

            Map<String, Object> data = route.toMap();
            var docRef = db
                    .collection(DbCollections.ROUTE)
                    .document();
            // wait for write to finish
            transaction.create(docRef, data);
            return true;
        });
        return ResponseResult.success(FutureUtil.handleFutureGet(transactionFuture::get));
    }
}
