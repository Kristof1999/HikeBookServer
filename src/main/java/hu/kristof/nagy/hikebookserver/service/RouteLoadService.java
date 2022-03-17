package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.FirestoreInitilizationException;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class RouteLoadService {

    @Autowired
    private Firestore db;

    /**
     * Loads all the routes which belong to the given user.
     * @param userName the user's name for who to load the routes
     * @return list of routes which belong to the given user
     */
    public List<Route> loadRoutesForUser(String userName) {
        ApiFuture<QuerySnapshot> future =  db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_NAME, DbPathConstants.ROUTE_POINTS)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .get();
        try {
            return future.get().getDocuments()
                    .stream()
                    .map(Route::from)
                    .collect(Collectors.toList());
        } catch (InterruptedException | CancellationException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new FirestoreInitilizationException();
    }

    // TODO: decide if group routes should be included
    /**
     * Lists all the routes' name and associated user name.
     */
    public List<BrowseListItem> listRoutes() {
        List<BrowseListItem> routes = new ArrayList<>();
        for(DocumentReference docRef : db.collection(DbPathConstants.COLLECTION_ROUTE).listDocuments()) {
            try {
                DocumentSnapshot doc = docRef.get().get();
                String userName = doc.get(DbPathConstants.ROUTE_USER_NAME, String.class);
                String routeName = doc.get(DbPathConstants.ROUTE_NAME, String.class);
                routes.add(new BrowseListItem(userName, routeName));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return routes;
    }

    /**
     * Loads the points of the given route and user.
     * @param userName name of the user who requested the load
     * @param routeName name of the route for which to load the points
     * @return list of points of the route
     */
    public List<Point> loadPoints(String userName, String routeName) {
        ApiFuture<QuerySnapshot> future = db.collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_POINTS)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
            QueryDocumentSnapshot doc = future.get().getDocuments().get(0);
            List<Point> points = (List<Point>) doc.get(DbPathConstants.ROUTE_POINTS);
            return points;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return List.of();
    }
}