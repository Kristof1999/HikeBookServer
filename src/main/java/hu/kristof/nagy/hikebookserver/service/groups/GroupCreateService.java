package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.service.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class GroupCreateService {

    @Autowired
    private Firestore db;

    public boolean createGroup(String groupName, String userName) {
        if (isNameUnique(groupName)) {
            try {
                saveGroup(groupName);
                saveMemberData(groupName, userName);
                updateUserGroups(groupName, userName);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            throw new IllegalArgumentException("A " + groupName + " már létezik! Kérem, hogy válasszon másikat.");
        }
    }

    private void saveGroup(String groupName) throws ExecutionException, InterruptedException {
        Map<String, Object> data = new HashMap<>();
        data.put(DbPathConstants.GROUP_NAME, groupName);
        db.collection(DbPathConstants.COLLECTION_GROUP)
                .document(groupName)
                .set(data)
                .get();
    }

    private void saveMemberData(String groupName, String userName) throws ExecutionException, InterruptedException {
        Map<String, Object> memberData = new HashMap<>();
        memberData.put(DbPathConstants.GROUP_DETAILS_MEMBER_NAMES, List.of(userName));
        db.collection(DbPathConstants.COLLECTION_GROUP)
                .document(groupName)
                .collection(DbPathConstants.COLLECTION_GROUP_DETAILS)
                .document(DbPathConstants.GROUP_MEMBER_NAMES_DETAIL_DOC_NAME)
                .set(memberData)
                .get();
    }

    private void updateUserGroups(String groupName, String userName) throws ExecutionException, InterruptedException {
        DocumentReference groupNamesDoc = db
                .collection(DbPathConstants.COLLECTION_USER)
                .document(userName)
                .collection(DbPathConstants.COLLECTION_USER_DETAILS)
                .document(DbPathConstants.USER_GROUP_NAMES_DETAIL_DOC_NAME);
        ApiFuture<DocumentSnapshot> future = groupNamesDoc.get();
        List<String> groupNames = (List<String>) future.get().get(DbPathConstants.USER_DETAILS_GROUP_NAMES);
        Map<String, Object> groupNamesData = new HashMap<>();
        if (groupNames == null) {
            groupNames = List.of(groupName);
        } else {
            groupNames.add(groupName);
        }
        groupNamesData.put(DbPathConstants.USER_DETAILS_GROUP_NAMES, groupNames);
        groupNamesDoc.set(groupNamesData)
                .get();
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
