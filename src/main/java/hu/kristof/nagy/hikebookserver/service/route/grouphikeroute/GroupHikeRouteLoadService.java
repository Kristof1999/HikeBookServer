package hu.kristof.nagy.hikebookserver.service.route.grouphikeroute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.GroupHikeRoute;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupHikeRouteLoadService {

    @Autowired
    private Firestore db;

    public ResponseResult<GroupHikeRoute> loadGroupHikeRoute(String groupHikeName) {
        var queryFuture = db.collection(DbPathConstants.COLLECTION_ROUTE)
                .select(GroupHikeRoute.getSelectPaths())
                .whereEqualTo(DbPathConstants.ROUTE_GROUP_HIKE_NAME, groupHikeName)
                .get();
        var queryDocs = FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments()
        );
        return Util.handleListSize(queryDocs, documentSnapshots ->
                ResponseResult.success(GroupHikeRoute.from(documentSnapshots.get(0)))
        );
    }
}
