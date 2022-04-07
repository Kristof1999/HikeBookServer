package hu.kristof.nagy.hikebookserver.service.grouproute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Group;
import hu.kristof.nagy.hikebookserver.model.GroupRoute;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import hu.kristof.nagy.hikebookserver.service.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class GroupRouteLoadService {
    @Autowired
    private Firestore db;

    public List<GroupRoute> loadGroupRoutesForGroup(String groupName) {
        var queryFuture =  db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_GROUP_NAME,
                        DbPathConstants.ROUTE_NAME,
                        DbPathConstants.ROUTE_POINTS,
                        DbPathConstants.ROUTE_DESCRIPTION)
                .whereEqualTo(DbPathConstants.ROUTE_GROUP_NAME, groupName)
                .get();
        try {
            return queryFuture.get().getDocuments()
                    .stream()
                    .map(GroupRoute::from)
                    .collect(Collectors.toList());
        } catch (InterruptedException | CancellationException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(Util.GENERIC_ERROR_MSG);
    }
}
