package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
            return connect(groupName, userName);
        }
    }

    private boolean connect(String groupName, String userName) {
        // TODO: update Group model and move creation logic there or use the object itself for creation
        var data = new HashMap<String, Object>();
        data.put(DbPathConstants.GROUP_NAME, groupName);
        data.put(DbPathConstants.GROUP_MEMBER_NAME, userName);
        try {
            db.collection(DbPathConstants.COLLECTION_GROUP)
                   .add(data)
                   .get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean disconnect(String groupName, String userName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_GROUP)
                .whereEqualTo(DbPathConstants.GROUP_NAME, groupName)
                .whereEqualTo(DbPathConstants.GROUP_MEMBER_NAME, userName)
                .get();
        try {
            String id = queryFuture.get().getDocuments().get(0).getId();
            db.collection(DbPathConstants.COLLECTION_GROUP)
                    .document(id)
                    .delete()
                    .get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}