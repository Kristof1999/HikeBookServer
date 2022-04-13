package hu.kristof.nagy.hikebookserver.service.route.grouproute;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupRouteDeleteService {

    @Autowired
    private Firestore db;

    public boolean deleteGroupRoute(String groupName, String routeName) {
        var transactionFuture = db.runTransaction(transaction -> {
            var routes = db.collection(DbPathConstants.COLLECTION_ROUTE);
            var query = routes
                    .whereEqualTo(DbPathConstants.ROUTE_GROUP_NAME, groupName)
                    .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName);
            var queryFuture = transaction.get(query);
            QuerySnapshot querySnapshot = FutureUtil.handleFutureGet(queryFuture::get);

            if (querySnapshot.isEmpty()) {
                // route with given name and userName didn't
                return false;
            } else {
                var queryDocs = querySnapshot.getDocuments();
                if (queryDocs.size() > 1) {
                    throw new QueryException("Got more than 1 query document snapshot, but was only expecting 1. " +
                            "Route name: " + routeName + ", owner name:" + groupName);
                } else if (queryDocs.size() == 0) {
                    throw new QueryException("Got no query document snapshot, but was only expecting 1. " +
                            "Route name: " + routeName + ", owner name:" + groupName);
                } else {
                    String id = queryDocs.get(0).getId();
                    var docRef = routes.document(id);
                    // wait for delete to finish
                    transaction.delete(docRef);
                    return true;
                }
            }
        });
        return FutureUtil.handleFutureGet(transactionFuture::get);
    }
}