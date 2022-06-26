package hu.kristof.nagy.hikebookserver.service.grouphike;

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
public class GroupHikeParticipantsListService {
    @Autowired
    private Firestore db;

    public ResponseResult<List<String>> listParticipants(String groupHikeName) {
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
