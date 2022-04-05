package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.api.client.util.ArrayMap;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
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

    public boolean createGroup(String name) {
        if (isNameUnique(name)) {
            Map<String, Object> data = new HashMap<>();
            data.put(DbPathConstants.GROUP_NAME, name);
            try {
                db.collection(DbPathConstants.COLLECTION_GROUP)
                        .document(name)
                        .set(data)
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            throw new IllegalArgumentException("A " + name + " már létezik! Kérem, hogy válasszon másikat.");
        }
    }

    private boolean isNameUnique(String name) {
        ApiFuture<QuerySnapshot> future = db
                .collection(DbPathConstants.COLLECTION_GROUP)
                .whereEqualTo(DbPathConstants.GROUP_NAME, name)
                .get();
        try {
            return future.get().isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(Util.GENERIC_ERROR_MSG);
    }
}
