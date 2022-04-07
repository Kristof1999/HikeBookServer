package hu.kristof.nagy.hikebookserver.service.grouproute;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.GroupRoute;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.userroute.UserRouteServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class GroupRouteCreateService {
    @Autowired
    private Firestore db;

    public boolean createGroupRoute(GroupRoute route) {
        if (routeNameExistsForGroup(route.getGroupName(), route.getRouteName())) {
            throw new IllegalArgumentException(
                    UserRouteServiceUtils.getRouteNameNotUniqueForUserString(route.getRouteName())
            );
        } else {
            // TODO: update point uniqueness checking
            if (UserRouteServiceUtils.arePointsUniqueForUser(db, route.getGroupName(), route.getPoints())) {
                Map<String, Object> data = route.toMap();
                try {
                    db.collection(DbPathConstants.COLLECTION_ROUTE)
                            .add(data)
                            .get(); // wait for write result
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                throw new IllegalArgumentException(UserRouteServiceUtils.POINTS_NOT_UNIQE);
            }
        }
    }

    private boolean routeNameExistsForGroup(String groupName, String routeName) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_GROUP_NAME,
                        DbPathConstants.ROUTE_NAME)
                .whereEqualTo(DbPathConstants.ROUTE_GROUP_NAME, groupName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
            return !queryFuture.get().isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(Util.GENERIC_ERROR_MSG);
    }
}
