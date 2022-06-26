package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupsMembersListService {

    @Autowired
    private Firestore db;

    public ResponseResult<List<String>> listMembers(String groupName) {
        var queryFuture = db
                .collection(DbCollections.GROUP)
                .select(DbFields.Group.MEMBER_NAME)
                .whereEqualTo(DbFields.Group.NAME, groupName)
                .get();
        return ResponseResult.success(
                FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments().stream()
                        .map(queryDocumentSnapshot ->
                                queryDocumentSnapshot.getString(DbFields.Group.MEMBER_NAME)
                        )
                        .collect(Collectors.toList())
                )
        );
    }
}
