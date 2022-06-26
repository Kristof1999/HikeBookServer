package hu.kristof.nagy.hikebookserver.service.groups;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.List;
import java.util.stream.Collectors;

public class GroupsMembersListService {
    public static ResponseResult<List<String>> listMembers(Firestore db, String groupName) {
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
