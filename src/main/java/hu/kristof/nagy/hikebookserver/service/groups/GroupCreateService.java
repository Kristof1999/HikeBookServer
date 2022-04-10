package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class GroupCreateService {

    @Autowired
    private Firestore db;

    public boolean createGroup(String groupName, String userName) {
        if (isNameUnique(groupName)) {
            save(groupName, userName);
            return true;
        } else {
            throw new IllegalArgumentException("A " + groupName + " már létezik! Kérem, hogy válasszon másikat.");
        }
    }

    private void save(String groupName, String memberName) {
        var data = new HashMap<String, Object>();
        data.put(DbPathConstants.GROUP_NAME, groupName);
        data.put(DbPathConstants.GROUP_MEMBER_NAME, memberName);
        FutureUtil.handleFutureGet(() ->
                db.collection(DbPathConstants.COLLECTION_GROUP)
                        .add(data)
                        .get()
        );
    }

    private boolean isNameUnique(String name) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_GROUP)
                .whereEqualTo(DbPathConstants.GROUP_NAME, name)
                .get();
        return FutureUtil.handleFutureGet(() ->
                queryFuture.get().isEmpty()
        );
    }
}
