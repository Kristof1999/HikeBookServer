package hu.kristof.nagy.hikebookserver.service.userroute;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UserRouteLoadService {

    @Autowired
    private Firestore db;

    /**
     * Loads all the routes which belong to the given user.
     * @param userName the user's name for who to load the routes
     * @return list of routes which belong to the given user
     */
    public List<UserRoute> loadUserRoutesForUser(String userName) {
        ApiFuture<QuerySnapshot> future =  db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_USER_NAME,
                        DbPathConstants.ROUTE_NAME,
                        DbPathConstants.ROUTE_POINTS,
                        DbPathConstants.ROUTE_DESCRIPTION)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .get();
        try {
            return future.get().getDocuments()
                    .stream()
                    .map(UserRoute::from)
                    .collect(Collectors.toList());
        } catch (InterruptedException | CancellationException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(UserRouteServiceUtils.GENERIC_ERROR_MSG);
    }

    /**
     * Lists all the routes' name and associated user name.
     */
    public List<BrowseListItem> listUserRoutes() {
        List<BrowseListItem> routes = new ArrayList<>();
        // TODO: select routes which do not belong to the user who requested the listing
        // use whereNotEqualTo(...)
        for(DocumentReference docRef : db.collection(DbPathConstants.COLLECTION_ROUTE).listDocuments()) {
            try {
                DocumentSnapshot doc = docRef.get().get();
                String userName = Objects.requireNonNull(
                        doc.getString(DbPathConstants.ROUTE_USER_NAME)
                );
                String routeName = Objects.requireNonNull(
                        doc.getString(DbPathConstants.ROUTE_NAME)
                );
                routes.add(new BrowseListItem(userName, routeName));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return routes;
    }

    /**
     * Loads the specified route.
     * @param userName name of the user who requested the load
     * @param routeName name of the route for which to load the points
     */
    public UserRoute loadUserRoute(String userName, String routeName) {
        ApiFuture<QuerySnapshot> future = db.collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_POINTS,
                        DbPathConstants.ROUTE_NAME,
                        DbPathConstants.ROUTE_USER_NAME,
                        DbPathConstants.ROUTE_DESCRIPTION)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
            QueryDocumentSnapshot doc = future.get().getDocuments().get(0);
            return UserRoute.from(doc);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(UserRouteServiceUtils.GENERIC_ERROR_MSG);
    }
}