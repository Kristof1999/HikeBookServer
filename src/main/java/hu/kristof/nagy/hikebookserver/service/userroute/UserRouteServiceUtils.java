package hu.kristof.nagy.hikebookserver.service.userroute;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.Util;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserRouteServiceUtils {
    public static String POINTS_NOT_UNIQE = "Az útvonal pontjai nem egyediek! " +
            "Kérem, hogy más pontokat használjon.";

    public static String getRouteNameNotUniqueForUserString(String routeName) {
        return "A(z) " + routeName + " nevű útvonal már létezik! " +
                "Kérem, hogy válasszon másik nevet.";
    }


    public static boolean arePointsUniqueForUser(Firestore db, String userName, List<Point> points) {
        ApiFuture<QuerySnapshot> future = db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_POINTS, points)
                .get();
        try {
            return future.get().isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(Util.GENERIC_ERROR_MSG);
    }

    public static boolean routeNameExistsForUser(Firestore db, String userName, String routeName) {
        ApiFuture<QuerySnapshot> future = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(DbPathConstants.ROUTE_USER_NAME,
                        DbPathConstants.ROUTE_NAME)
                .whereEqualTo(DbPathConstants.ROUTE_USER_NAME, userName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName)
                .get();
        try {
            return !future.get().isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(Util.GENERIC_ERROR_MSG);
    }
}
