package hu.kristof.nagy.hikebookserver.service.route.grouphikeroute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.GroupHikeRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;

public final class GroupHikeRouteLoadService {
    public static ResponseResult<GroupHikeRoute> loadGroupHikeRoute(Firestore db, String groupHikeName) {
        var queryFuture = db.collection(DbCollections.ROUTE)
                .select(GroupHikeRoute.getSelectPaths())
                .whereEqualTo(DbFields.GroupHikeRoute.NAME, groupHikeName)
                .get();
        var queryDocs = FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments()
        );
        return Util.handleListSize(queryDocs, documentSnapshots ->
                ResponseResult.success(GroupHikeRoute.from(documentSnapshots.get(0)))
        );
    }
}
