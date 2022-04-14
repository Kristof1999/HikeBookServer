package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupHikeParticipantsListService {
    @Autowired
    private Firestore db;

    // TODO: derive common parts with groups members list to a common method
    public List<String> listParticipants(String groupHikeName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_GROUP_HIKE)
                .select(DbPathConstants.GROUP_HIKE_PARTICIPANT_NAME)
                .whereEqualTo(DbPathConstants.GROUP_HIKE_NAME, groupHikeName)
                .get();
        return FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments().stream()
                        .map(queryDocumentSnapshot ->
                                queryDocumentSnapshot.getString(DbPathConstants.GROUP_HIKE_PARTICIPANT_NAME)
                        )
                        .collect(Collectors.toList())
        );
    }
}
