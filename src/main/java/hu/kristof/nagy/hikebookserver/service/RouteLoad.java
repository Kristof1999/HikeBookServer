package hu.kristof.nagy.hikebookserver.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.database.GenericTypeIndicator;
import hu.kristof.nagy.hikebookserver.FirestoreInitilizationException;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.Route;
import hu.kristof.nagy.hikebookserver.model.UserRoute;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class RouteLoad {

    @Autowired
    private Firestore db;

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

    // TODO: decode if group routes should be included
    public List<UserRoute> loadRoutes() {
        List<UserRoute> routes = new ArrayList<>();
        for(DocumentReference docRef : db.collection(DbPathConstants.COLLECTION_ROUTE).listDocuments()) {
            try {
                DocumentSnapshot doc = docRef.get().get();
                String userName = doc.get(DbPathConstants.ROUTE_USER_NAME, String.class);
                String routeName = doc.get(DbPathConstants.ROUTE_NAME, String.class);
                List<Point> points = (List<Point>) doc.get(DbPathConstants.ROUTE_POINTS);
                Route route = new Route(routeName, points);
                routes.add(new UserRoute(userName, route));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return routes;
    }
}
