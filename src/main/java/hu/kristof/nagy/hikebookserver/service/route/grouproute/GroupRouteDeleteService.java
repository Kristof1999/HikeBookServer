package hu.kristof.nagy.hikebookserver.service.route.grouproute;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupRouteDeleteService {

    @Autowired
    private Firestore db;

    /**
     * Deletes the group route with the given group name and route name.
     */
    public ResponseResult<Boolean> deleteGroupRoute(String groupName, String routeName) {
        var transactionFuture = db.runTransaction(transaction -> {
            var routes = db.collection(DbCollections.ROUTE);
            var query = routes
                    .whereEqualTo(DbFields.GroupRoute.NAME, groupName)
                    .whereEqualTo(DbFields.Route.ROUTE_NAME, routeName);
            var queryFuture = transaction.get(query);
            QuerySnapshot querySnapshot = FutureUtil.handleFutureGet(queryFuture::get);

            if (querySnapshot.isEmpty()) {
                // route with given name and userName didn't exist
                return false;
            } else {
                var queryDocs = querySnapshot.getDocuments();
                if (queryDocs.size() == 0) {
                    throw new IllegalArgumentException("Az adott útvonal már törölve lett! Kérem, hogy frissítse az oldalt.");
                }
                return Util.handleListSize(queryDocs, documentSnapshots -> {
                    String id = documentSnapshots.get(0).getId();
                    var docRef = routes.document(id);
                    // wait for delete to finish
                    transaction.delete(docRef);
                    return true;
                });
            }
        });
        return ResponseResult.success(FutureUtil.handleFutureGet(transactionFuture::get));
    }
}
