package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Group;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroupsGeneralConnectService {

    @Autowired
    private Firestore db;

    public boolean generalConnect(
            String groupName,
            String userName,
            boolean isConnectedPage
    ) {
        if (isConnectedPage) {
            return disconnect(groupName, userName);
        } else {
            return connect(new Group(groupName, userName));
        }
    }

    private boolean connect(Group group) {
        Map<String, Object> data = group.toMap();
        return FutureUtil.handleFutureGet(() -> {
            db.collection(DbPathConstants.COLLECTION_GROUP)
                    .add(data)
                    .get();
            return true;
        });
    }

    private boolean disconnect(String groupName, String userName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_GROUP)
                .whereEqualTo(DbPathConstants.GROUP_NAME, groupName)
                .whereEqualTo(DbPathConstants.GROUP_MEMBER_NAME, userName)
                .get();
        return FutureUtil.handleFutureGet(() -> {
            var queryDocs = queryFuture.get().getDocuments();
            if (queryDocs.size() > 1) {
                throw new QueryException("Got more than 1 query document snapshot, but was only expecting 1. " +
                        "Group name: " + groupName + ", member name: " + userName);
            } else if (queryDocs.size() == 0) {
                throw new QueryException("Got no query document snapshot, but was only expecting 1. " +
                        "Group name: " + groupName + ", member name: " + userName);
            } else {
                String id = queryDocs.get(0).getId();
                db.collection(DbPathConstants.COLLECTION_GROUP)
                        .document(id)
                        .delete()
                        .get();
                return true;
            }
        });
    }
}
