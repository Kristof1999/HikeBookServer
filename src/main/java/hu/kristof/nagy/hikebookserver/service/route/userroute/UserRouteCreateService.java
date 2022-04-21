package hu.kristof.nagy.hikebookserver.service.route.userroute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.RouteCreate;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.SimpleRouteUniquenessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserRouteCreateService implements RouteCreate {

    @Autowired
    private Firestore db;

    @Override
    public ResponseResult<Boolean> createRoute(Route route) {
        try {
            route.handleRouteUniqueness(new SimpleRouteUniquenessHandler
                    .Builder(db)
            );
        } catch (IllegalArgumentException e) {
            return ResponseResult.fail(e.getMessage());
        }

        Map<String, Object> data = route.toMap();
        FutureUtil.handleFutureGet(() ->
                db.collection(DbPathConstants.COLLECTION_ROUTE)
                        .add(data)
                        .get() // wait for write result
        );
        return ResponseResult.success(true);
    }

}
