package hu.kristof.nagy.hikebookserver.service.route.userroute;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;

public final class UserRouteDeleteService {
    /**
     * @param userName name of user who requested deletion
     * @param routeName name of route which to delete for the given user
     * @return true if deletion was successful
     */
    public static ResponseResult<Boolean> deleteUserRoute(Firestore db, String userName, String routeName) {
        var routes = db.collection(DbCollections.ROUTE);
        var queryFuture = routes
                .whereEqualTo(DbFields.UserRoute.NAME, userName)
                .whereEqualTo(DbFields.Route.ROUTE_NAME, routeName)
                .get();

        QuerySnapshot querySnapshot = FutureUtil.handleFutureGet(queryFuture::get);
        if (querySnapshot.isEmpty()) {
            // route with given name and userName didn't exist
            // maybe throw exception
            return ResponseResult.success(false);
        } else {
            var queryDocs = querySnapshot.getDocuments();
            return Util.handleListSize(queryDocs, documentSnapshots -> {
                String id = documentSnapshots.get(0).getId();
                FutureUtil.handleFutureGet(() ->
                        routes.document(id)
                                .delete()
                                .get() // wait for write result
                );
                return ResponseResult.success(true);
            });
        }
    }
}
