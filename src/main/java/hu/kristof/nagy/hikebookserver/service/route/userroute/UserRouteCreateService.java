package hu.kristof.nagy.hikebookserver.service.route.userroute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.routeuniqueness.SimpleRouteUniquenessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserRouteCreateService {

    @Autowired
    private Firestore db;

    public ResponseResult<Boolean> createRoute(UserRoute route) {
        route.handleRouteUniqueness(new SimpleRouteUniquenessHandler
                .Builder(db)
        );

        Map<String, Object> data = route.toMap();
        FutureUtil.handleFutureGet(() ->
                db.collection(DbPathConstants.COLLECTION_ROUTE)
                        .add(data)
                        .get() // wait for write result
        );
        return ResponseResult.success(true);
    }
}
