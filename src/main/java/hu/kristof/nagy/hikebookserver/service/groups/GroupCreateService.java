package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Group;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroupCreateService {

    @Autowired
    private Firestore db;

    public boolean createGroup(String groupName, String userName) {
        // TODO: run as transaction
        if (isNameUnique(groupName)) {
            save(new Group(groupName, userName));
            return true;
        } else {
            throw new IllegalArgumentException("A " + groupName + " már létezik! Kérem, hogy válasszon másikat.");
        }
    }

    private void save(Group group) {
        Map<String, Object> data = group.toMap();
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
