package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Group;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
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
        var queryDocs = FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments()
        );
        return Util.handleListSize(queryDocs, documentSnapshots -> {
            String id = documentSnapshots.get(0).getId();
            FutureUtil.handleFutureGet(() -> db
                    .collection(DbPathConstants.COLLECTION_GROUP)
                    .document(id)
                    .delete()
                    .get()
            );
            return true;
        });
    }
}
