package hu.kristof.nagy.hikebookserver.service.userroute;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserRouteCreateService {

    @Autowired
    private Firestore db;

    /**
     * Before saving the route, checks if the route is unique:
     * the user has no route called routeName,
     * and no route whose points are the same as the points argument.
     * @param route the created route
     * @return true if route is unique
     */
    public boolean createUserRoute(UserRoute route) {
        if (UserRouteServiceUtils.routeNameExistsForUser(db, route.getUserName(), route.getRouteName())) {
            throw new IllegalArgumentException(
                    UserRouteServiceUtils.getRouteNameNotUniqueForUserString(route.getRouteName())
            );
        } else {
            if (UserRouteServiceUtils.arePointsUniqueForUser(db, route.getUserName(), route.getPoints())) {
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
}
