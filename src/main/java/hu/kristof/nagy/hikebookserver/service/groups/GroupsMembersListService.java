package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupsMembersListService {

    @Autowired
    private Firestore db;

    public List<String> listMembers(String groupName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_GROUP)
                .select(DbPathConstants.GROUP_MEMBER_NAME)
                .whereEqualTo(DbPathConstants.GROUP_NAME, groupName)
                .get();
        return FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments().stream()
                .map(queryDocumentSnapshot ->
                        queryDocumentSnapshot.getString(DbPathConstants.GROUP_MEMBER_NAME)
                )
                .collect(Collectors.toList())
        );
    }
}
