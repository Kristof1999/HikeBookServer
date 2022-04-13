package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;

import java.util.List;

public class RouteServiceUtils {
    public static String POINTS_NOT_UNIQE = "Az útvonal pontjai nem egyediek! " +
            "Kérem, hogy más pontokat használjon.";

    public static String getRouteNameNotUniqueString(String routeName) {
        return "A(z) " + routeName + " nevű útvonal már létezik! " +
                "Kérem, hogy válasszon másik nevet.";
    }

    public static boolean arePointsUniqueForOwner(
            Transaction transaction,
            Firestore db,
            String ownerName,
            List<Point> points,
            String ownerPath
    ) {
        var query = arePointsUniqueForOwnerQuery(db, ownerName, points, ownerPath);
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }

    public static boolean arePointsUniqueForOwner(
            Firestore db,
            String ownerName,
            List<Point> points,
            String ownerPath
    ) {
        var queryFuture =
                arePointsUniqueForOwnerQuery(db, ownerName, points, ownerPath)
                .get();
        return FutureUtil.handleFutureGet(() -> queryFuture.get().isEmpty());
    }

    private static Query arePointsUniqueForOwnerQuery(
            Firestore db,
            String ownerName,
            List<Point> points,
            String ownerPath
    ) {
        return  db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(ownerPath, ownerName)
                .whereEqualTo(DbPathConstants.ROUTE_POINTS, points);
    }

    public static boolean routeNameExistsForOwner(
            Transaction transaction,
            Firestore db,
            String ownerName,
            String routeName,
            String ownerPath
    ) {
        var query = routeNameExistsForOwnerQuery(db,ownerName, routeName, ownerPath);
        var queryFuture = transaction.get(query);
        var queryRes = FutureUtil.handleFutureGet(queryFuture::get);
        return !queryRes.isEmpty();
    }

    public static boolean routeNameExistsForOwner(
            Firestore db,
            String ownerName,
            String routeName,
            String ownerPath
    ) {
        var queryFuture =
                routeNameExistsForOwnerQuery(db, ownerName, routeName, ownerPath)
                .get();
        return FutureUtil.handleFutureGet(() ->
                !queryFuture.get().isEmpty()
        );
    }

    private static Query routeNameExistsForOwnerQuery(
            Firestore db,
            String ownerName,
            String routeName,
            String ownerPath
    ) {
        return db.collection(DbPathConstants.COLLECTION_ROUTE)
                .select(ownerPath,
                        DbPathConstants.ROUTE_NAME)
                .whereEqualTo(ownerPath, ownerName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName);
    }
}
