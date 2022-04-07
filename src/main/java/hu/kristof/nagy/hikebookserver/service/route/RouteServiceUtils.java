package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.Route;
import hu.kristof.nagy.hikebookserver.model.RouteType;
import hu.kristof.nagy.hikebookserver.service.Util;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RouteServiceUtils {
    public static String POINTS_NOT_UNIQE = "Az útvonal pontjai nem egyediek! " +
            "Kérem, hogy más pontokat használjon.";

    public static String getRouteNameNotUniqueString(String routeName) {
        return "A(z) " + routeName + " nevű útvonal már létezik! " +
                "Kérem, hogy válasszon másik nevet.";
    }


    public static boolean arePointsUnique(
            Firestore db,
            String ownerName,
            List<Point> points,
            RouteType routeType
    ) {
        String ownerPath = Route.getOwnerDatabasePath(routeType);
        var queryFuture = db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(ownerPath, ownerName)
                .whereEqualTo(DbPathConstants.ROUTE_POINTS, points)
                .get();
        try {
            return queryFuture.get().isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(Util.GENERIC_ERROR_MSG);
    }

    public static boolean routeNameExists(
            Firestore db,
            String ownerName,
            String routeName,
            RouteType routeType
    ) {
        String ownerPath = Route.getOwnerDatabasePath(routeType);
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(ownerPath,
                        DbPathConstants.ROUTE_NAME)
                .whereEqualTo(ownerPath, ownerName)
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
