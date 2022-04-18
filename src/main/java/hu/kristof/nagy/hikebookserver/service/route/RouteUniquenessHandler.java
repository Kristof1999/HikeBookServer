package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;

import java.util.List;

public abstract class RouteUniquenessHandler {
    protected String routeName;
    protected List<Point> points;
    protected Firestore db;

    public abstract String getOwnerName();
    public abstract String getOwnerPath();

    public void handleRouteUniqueness() {
        if (isRouteNameUnique()) {
            if (!arePointsUnique()) {
                throw new IllegalArgumentException(RouteServiceUtils.POINTS_NOT_UNIQE);
            }
        } else {
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(routeName)
            );
        }
    }

    protected abstract boolean arePointsUnique();

    protected Query arePointsUniqueQuery() {
        return  db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(getOwnerPath(), getOwnerName())
                .whereEqualTo(DbPathConstants.ROUTE_POINTS, points);
    }

    protected abstract boolean isRouteNameUnique();

    protected Query isRouteNameUniqueQuery() {
        return db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(getOwnerPath(), getOwnerName())
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName);
    }
}
