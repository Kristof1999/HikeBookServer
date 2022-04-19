package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.DateTime;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.grouphikeroute.GroupHikeRouteLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroupHikeGeneralConnectService {
    @Autowired
    private Firestore db;

    @Autowired
    private GroupHikeRouteLoadService groupHikeRouteLoadService;

    /**
     * Connects or disconnects the given user to the given group hike
     * based on whether the user is at the connected page or not.
     * If everyone left the given group hike, then the
     * associated route will be deleted.
     * If someone tries to connect to a deleted group hike, he/she
     * will receive a message that the group hike does no longer exist.
     * @param groupHikeName name of group hike to join/leave
     * @param userName name of user who wants to join/leave the given group hike
     * @param isConnectedPage boolean that tells whether the user is at the connected page or not
     * @param dateTime date and time of the group hike
     * @return true if joining/leaving was successful
     */
    public boolean generalConnect(
            String groupHikeName,
            String userName,
            boolean isConnectedPage,
            DateTime dateTime
    ) {
        if (isConnectedPage) {
            return disconnect(groupHikeName, userName);
        } else {
            return connect(groupHikeName, userName, dateTime);
        }
    }

    private boolean connect(String groupHikeName, String userName, DateTime dateTime) {
        var transactionFuture = db.runTransaction(transaction -> {
            if (participantNumber(transaction, groupHikeName) == 0)
                throw new IllegalArgumentException("A csoportos túra véget ért/megszűnt.");

            Map<String, Object> data = dateTime.toMap();
            data.put(DbPathConstants.GROUP_HIKE_DATE_TIME, dateTime.toString());
            data.put(DbPathConstants.GROUP_HIKE_NAME, groupHikeName);
            data.put(DbPathConstants.GROUP_HIKE_PARTICIPANT_NAME, userName);
            return FutureUtil.handleFutureGet(() -> {
                var docRef = db.collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                        .document();
                transaction.create(docRef, data);
                return true;
            });
        });

        return FutureUtil.handleFutureGet(transactionFuture::get);
    }

    private boolean disconnect(String groupHikeName, String userName) {
        var transactionFuture = db.runTransaction(transaction -> {
            var query = db
                    .collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                    .whereEqualTo(DbPathConstants.GROUP_HIKE_NAME, groupHikeName)
                    .whereEqualTo(DbPathConstants.GROUP_HIKE_PARTICIPANT_NAME, userName);
            var queryFuture = transaction.get(query);
            var queryDocs = FutureUtil.handleFutureGet(() ->
                    queryFuture.get().getDocuments()
            );
            return Util.handleListSize(queryDocs, documentSnapshots -> {
                if (participantNumber(transaction, groupHikeName) == 1) {
                    deleteGroupHikeRoute(transaction, groupHikeName);
                }

                String id = documentSnapshots.get(0).getId();
                var docRef = db.collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                        .document(id);
                transaction.delete(docRef);
                return true;
            });
        });
        return FutureUtil.handleFutureGet(transactionFuture::get);
    }

    private void deleteGroupHikeRoute(Transaction transaction, String groupHikeName) {
        var query = db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(DbPathConstants.ROUTE_GROUP_HIKE_NAME, groupHikeName);
        var queryFuture = transaction.get(query);
        var queryDocs = FutureUtil.handleFutureGet(() -> queryFuture.get().getDocuments());
        Util.handleListSize(queryDocs, documentSnapshots -> {
            String id = documentSnapshots.get(0).getId();
            var docRef = db.collection(DbPathConstants.COLLECTION_ROUTE)
                    .document(id);
            return transaction.delete(docRef);
        });
    }

    private int participantNumber(Transaction transaction, String groupHikeName) {
        var query = db.collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                .whereEqualTo(DbPathConstants.GROUP_HIKE_NAME, groupHikeName);
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() -> queryFuture.get().getDocuments().size());
    }
}
