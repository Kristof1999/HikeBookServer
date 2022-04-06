package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
        var future = db
                .collection(DbPathConstants.COLLECTION_GROUP)
                .select(DbPathConstants.GROUP_NAME)
                .whereEqualTo(DbPathConstants.GROUP_MEMBER_NAME, userName)
                .get();
        // TODO: use generic lamdba to replace future try-catch calls
        try {
            return new ArrayList<>(future.get().getDocuments().stream()
                    .map(queryDocumentSnapshot ->
                            queryDocumentSnapshot.getString(DbPathConstants.GROUP_NAME)
                    )
                    .collect(Collectors.toSet()) // distinct substitute
            );
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
                return new ArrayList<>(future.get().getDocuments().stream()
                        .map(queryDocumentSnapshot ->
                                queryDocumentSnapshot.getString(DbPathConstants.GROUP_NAME)
                        )
                        .collect(Collectors.toSet()) // distinct substitute
                );
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return List.of(); //de lehet inkább hibát kellene dobni
    }

    private List<String> listAllGroups() {
        CollectionReference groups = db
                .collection(DbPathConstants.COLLECTION_GROUP);
        Set<String> res = new HashSet<>(); // distinct substitute
        for (DocumentReference doc: groups.listDocuments()) {
            try {
                String groupName = doc.get().get().getString(DbPathConstants.GROUP_NAME);
                res.add(groupName);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(res);
    }
}
