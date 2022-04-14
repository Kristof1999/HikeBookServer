package hu.kristof.nagy.hikebookserver.service.route.grouphikeroute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupHikeRouteLoadService {

    @Autowired
    private Firestore db;

    public Route loadGroupHikeRoute(String groupHikeName) {
        var queryFuture = db.collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_NAME,
                        DbPathConstants.ROUTE_DESCRIPTION,
                        DbPathConstants.ROUTE_POINTS)
                .whereEqualTo(DbPathConstants.ROUTE_GROUP_HIKE_NAME, groupHikeName)
                .get();
        // TODO: check uniqueness of query result
        return FutureUtil.handleFutureGet(() ->
                Route.from(queryFuture.get().getDocuments().get(0))
        );
    }
}
