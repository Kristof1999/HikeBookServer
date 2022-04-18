package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
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
        var transactionFuture = db.runTransaction(transaction -> {
            if (isNameUnique(transaction, groupName)) {
                save(transaction, new Group(groupName, userName));
                return true;
            } else {
                throw new IllegalArgumentException("A " + groupName + " már létezik! Kérem, hogy válasszon másikat.");
            }
        });
        return FutureUtil.handleFutureGet(transactionFuture::get);
    }

    private void save(Transaction transaction, Group group) {
        Map<String, Object> data = group.toMap();
        var docRef = db.collection(DbPathConstants.COLLECTION_GROUP)
                .document();
        transaction.create(docRef, data);
    }

    private boolean isNameUnique(Transaction transaction, String name) {
        var query = db
                .collection(DbPathConstants.COLLECTION_GROUP)
                .whereEqualTo(DbPathConstants.GROUP_NAME, name);
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() ->
                queryFuture.get().isEmpty()
        );
    }
}
