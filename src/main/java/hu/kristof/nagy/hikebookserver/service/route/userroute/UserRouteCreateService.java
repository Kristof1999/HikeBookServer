package hu.kristof.nagy.hikebookserver.service.route.userroute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.SimpleRouteUniquenessHandler;

import java.util.Map;

public class UserRouteCreateService {
    public static ResponseResult<Boolean> createRoute(Firestore db, UserRoute route) {
        route.handleRouteUniqueness(new SimpleRouteUniquenessHandler
                .Builder(db)
        );

        Map<String, Object> data = route.toMap();
        FutureUtil.handleFutureGet(() ->
                db.collection(DbCollections.ROUTE)
                        .add(data)
                        .get() // wait for write result
        );
        return ResponseResult.success(true);
    }
}
