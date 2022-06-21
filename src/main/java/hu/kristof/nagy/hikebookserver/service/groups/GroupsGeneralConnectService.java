package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Group;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import hu.kristof.nagy.hikebookserver.service.route.grouproute.GroupRouteDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroupsGeneralConnectService {

    @Autowired
    private Firestore db;

    public ResponseResult<Boolean> generalConnect(
            String groupName,
            String userName,
            boolean isConnectedPage
    ) {
        if (isConnectedPage) {
            return ResponseResult.success(disconnect(groupName, userName));
        } else {
            return ResponseResult.success(connect(new Group(groupName, userName)));
        }
    }

    private boolean connect(Group group) {
        Map<String, Object> data = group.toMap();
        if (groupExists(group.getGroupName())) {
            if (hasNotConnected(group.getGroupName(), group.getMemberName())) {
                return FutureUtil.handleFutureGet(() -> {
                    db.collection(DbPathConstants.COLLECTION_GROUP)
                            .add(data)
                            .get();
                    return true;
                });
            } else {
                throw new IllegalArgumentException("Ehhez a csoporthoz már csatlakoztál.");
            }
        } else {
            throw new IllegalArgumentException("Ez a csoport: " + group.getGroupName() + " nem létezik.");
        }
    }

    private boolean groupExists(String groupName) {
        var queryFuture = db.collection(DbPathConstants.COLLECTION_GROUP)
                .whereEqualTo(DbPathConstants.GROUP_NAME, groupName)
                .get();
        return FutureUtil.handleFutureGet(() -> !queryFuture.get().isEmpty());
    }

    private boolean hasNotConnected(String groupName, String userName) {
        var queryFuture = db.collection(DbPathConstants.COLLECTION_GROUP)
                .whereEqualTo(DbPathConstants.GROUP_NAME, groupName)
                .whereEqualTo(DbPathConstants.GROUP_MEMBER_NAME, userName)
                .get();
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }

    private boolean disconnect(String groupName, String userName) {
        var futureResult = db.runTransaction(transaction -> {
            var query = db
                    .collection(DbPathConstants.COLLECTION_GROUP)
                    .whereEqualTo(DbPathConstants.GROUP_NAME, groupName)
                    .whereEqualTo(DbPathConstants.GROUP_MEMBER_NAME, userName);
            var queryFuture = transaction.get(query);
            var queryDocs = FutureUtil.handleFutureGet(() ->
                    queryFuture.get().getDocuments()
            );

            return Util.handleListSize(queryDocs, documentSnapshots -> {
                String id = documentSnapshots.get(0).getId();
                var docRef = db
                        .collection(DbPathConstants.COLLECTION_GROUP)
                        .document(id);

                if (getNumberOfMembers(groupName, transaction) == 1) {
                    deleteGroupRoutes(groupName, transaction);
                }

                transaction.delete(docRef);
                return true;
            });
        });

        return FutureUtil.handleFutureGet(futureResult::get);
    }

    private int getNumberOfMembers(String groupName, Transaction transaction) {
        var query = db
                .collection(DbPathConstants.COLLECTION_GROUP)
                .whereEqualTo(DbPathConstants.GROUP_NAME, groupName);
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() -> queryFuture
                .get()
                .getDocuments()
        ).size();
    }

    private void deleteGroupRoutes(String groupName, Transaction transaction) {
        var query = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(DbPathConstants.ROUTE_GROUP_NAME, groupName);
        var queryFuture = transaction.get(query);
        var groupRoutes = FutureUtil.handleFutureGet(() -> queryFuture
                .get()
                .getDocuments()
        );
        groupRoutes.forEach(queryDocumentSnapshot ->
                transaction.delete(queryDocumentSnapshot.getReference())
        );
    }
}
