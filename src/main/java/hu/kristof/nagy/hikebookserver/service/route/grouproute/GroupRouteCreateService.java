package hu.kristof.nagy.hikebookserver.service.route.grouproute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.routes.GroupRoute;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.GroupRouteUniquenessHandler;
import hu.kristof.nagy.hikebookserver.service.route.RouteCreate;
import hu.kristof.nagy.hikebookserver.service.route.RouteServiceUtils;
import hu.kristof.nagy.hikebookserver.service.route.UserRouteUniquenessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroupRouteCreateService implements RouteCreate {

    @Autowired
    private Firestore db;

    @Override
    public boolean createRoute(Route route) {
        var transactionFuture = db.runTransaction(transaction -> {
            var handler = new GroupRouteUniquenessHandler(
                    transaction,
                    db,
                    ((GroupRoute) route).getGroupName(),
                    route.getRouteName(),
                    route.getPoints()
            );
            route.handleRouteUniqueness(handler);

            Map<String, Object> data = route.toMap();
            var docRef = db
                    .collection(DbPathConstants.COLLECTION_ROUTE)
                    .document();
            // wait for write to finish
            transaction.create(docRef, data);
            return true;
        });
        return FutureUtil.handleFutureGet(transactionFuture::get);
    }
}
