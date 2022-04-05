package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class GroupsListService {

    @Autowired
    private Firestore db;

    public List<String> listGroups(
            String userName,
            boolean isConnectedPage
    ) {
        if (isConnectedPage) {
            return listConnectedGroups(userName);
        } else {
            return listNotConnectedGroups(userName);
        }
    }

    private List<String> listConnectedGroups(String userName) {
        ApiFuture<DocumentSnapshot> future = db
                .collection(DbPathConstants.COLLECTION_USER)
                .document(userName)
                .collection(DbPathConstants.USER_DETAILS)
                .document(DbPathConstants.USER_DETAILS)
                .get();
        // TODO: use generic lamdba to replace future try-catch calls
        try {
            List<String> strings = (List<String>) future.get()
                    .get(DbPathConstants.USER_DETAILS_GROUP_NAMES);
            if (strings == null) {
                return List.of();
            } else {
                return strings;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    private List<String> listNotConnectedGroups(String userName) {
        List<String> connectedGroups = listConnectedGroups(userName);
        if (connectedGroups.isEmpty()) {
            return listAllGroups();
        } else {
            ApiFuture<QuerySnapshot> future = db
                    .collection(DbPathConstants.COLLECTION_GROUP)
                    .select(DbPathConstants.GROUP_NAME)
                    .whereNotIn(DbPathConstants.GROUP_NAME, connectedGroups)
                    .get();
            try {
                return future.get().getDocuments().stream()
                        .map(queryDocumentSnapshot ->
                                queryDocumentSnapshot.getString(DbPathConstants.GROUP_NAME)
                        )
                        .collect(Collectors.toList());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return List.of(); //de lehet inkább hibát kellene dobni
    }

    private List<String> listAllGroups() {
        CollectionReference groups = db
                .collection(DbPathConstants.COLLECTION_GROUP);
        List<String> res = new ArrayList<>();
        for (DocumentReference doc: groups.listDocuments()) {
            try {
                String groupName = doc.get().get().getString(DbPathConstants.GROUP_NAME);
                res.add(groupName);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}
