package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.DateTime;
import hu.kristof.nagy.hikebookserver.model.Group;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GroupHikeGeneralConnectService {
    @Autowired
    private Firestore db;

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
        Map<String, Object> data = dateTime.toMap();
        data.put(DbPathConstants.GROUP_HIKE_DATE_TIME, dateTime.toString());
        data.put(DbPathConstants.GROUP_HIKE_NAME, groupHikeName);
        data.put(DbPathConstants.GROUP_HIKE_PARTICIPANT_NAME, userName);
        return FutureUtil.handleFutureGet(() -> {
            db.collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                    .add(data)
                    .get();
            return true;
        });
    }

    private boolean disconnect(String groupHikeName, String userName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                .whereEqualTo(DbPathConstants.GROUP_HIKE_NAME, groupHikeName)
                .whereEqualTo(DbPathConstants.GROUP_HIKE_PARTICIPANT_NAME, userName)
                .get();
        return FutureUtil.handleFutureGet(() -> {
            var queryDocs = queryFuture.get().getDocuments();
            // TODO: refactor with lambda, like with FutureUtil
            if (queryDocs.size() > 1) {
                throw new QueryException("Got more than 1 query document snapshot, but was only expecting 1. " +
                        "Group name: " + groupHikeName + ", member name: " + userName);
            } else if (queryDocs.size() == 0) {
                throw new QueryException("Got no query document snapshot, but was only expecting 1. " +
                        "Group name: " + groupHikeName + ", member name: " + userName);
            } else {
                String id = queryDocs.get(0).getId();
                db.collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                        .document(id)
                        .delete()
                        .get();
                return true;
            }
        });
    }
}
