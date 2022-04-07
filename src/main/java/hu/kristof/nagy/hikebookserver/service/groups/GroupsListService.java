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

    public List<String> listGroups(String userName, boolean isConnectedPage) {
        if (isConnectedPage) {
            return listConnectedGroupNames(userName);
        } else {
            return listNotConnectedGroupNames(userName);
        }
    }

    private List<String> listConnectedGroupNames(String userName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_GROUP)
                .select(DbPathConstants.GROUP_NAME)
                .whereEqualTo(DbPathConstants.GROUP_MEMBER_NAME, userName)
                .get();
        // TODO: use generic lamdba to replace future try-catch calls
        try {
            return new ArrayList<>(queryFuture.get().getDocuments().stream()
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

    private List<String> listNotConnectedGroupNames(String userName) {
        var connectedGroupNames = listConnectedGroupNames(userName);
        if (connectedGroupNames.isEmpty()) {
            return listAllGroups();
        } else {
            var queryFuture = db
                    .collection(DbPathConstants.COLLECTION_GROUP)
                    .select(DbPathConstants.GROUP_NAME)
                    .whereNotIn(DbPathConstants.GROUP_NAME, connectedGroupNames)
                    .get();
            try {
                return new ArrayList<>(queryFuture.get().getDocuments().stream()
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
        var groups = db.collection(DbPathConstants.COLLECTION_GROUP);
        var res = new HashSet<String>(); // distinct substitute
        for (var doc: groups.listDocuments()) {
            try {
                var groupName = doc.get().get().getString(DbPathConstants.GROUP_NAME);
                res.add(groupName);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(res);
    }
}
