package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;
import hu.kristof.nagy.hikebookserver.data.DbFields;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.List;
import java.util.stream.Collectors;

public final class GroupHikeParticipantsListService {
    public static ResponseResult<List<String>> listParticipants(Firestore db, String groupHikeName) {
        var queryFuture = db
                .collection(DbCollections.GROUP_HIKE)
                .select(DbFields.GroupHike.PARTICIPANT_NAME)
                .whereEqualTo(DbFields.GroupHike.NAME, groupHikeName)
                .get();
        return ResponseResult.success(
                FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments().stream()
                        .map(queryDocumentSnapshot ->
                                queryDocumentSnapshot.getString(DbFields.GroupHike.PARTICIPANT_NAME)
                        )
                        .collect(Collectors.toList())
                )
        );
    }
}
