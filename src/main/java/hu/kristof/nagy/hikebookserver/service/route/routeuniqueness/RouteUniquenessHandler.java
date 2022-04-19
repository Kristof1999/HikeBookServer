package hu.kristof.nagy.hikebookserver.service.route.routeuniqueness;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.service.route.RouteServiceUtils;

import java.util.List;

/**
 * A Class the checks and handles a route's uniqueness.
 */
public abstract class RouteUniquenessHandler {
    protected String ownerName;
    protected String ownerPath;
    protected String routeName;
    protected List<Point> points;
    protected Firestore db;

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerPath() {
        return ownerPath;
    }

    public void handleRouteUniqueness() {
        handlePointsUniqueness();
        handleRouteNameUniqueness();
    }

    public void handlePointsUniqueness() {
        if (!arePointsUnique())
            throw new IllegalArgumentException(RouteServiceUtils.POINTS_NOT_UNIQE);
    }

    public void handleRouteNameUniqueness() {
        if (!isRouteNameUnique())
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(routeName)
            );
    }

    protected abstract boolean arePointsUnique();

    protected Query arePointsUniqueQuery() {
        return db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(getOwnerPath(), getOwnerName())
                .whereEqualTo(DbPathConstants.ROUTE_POINTS, points);
    }

    protected abstract boolean isRouteNameUnique();

    protected Query isRouteNameUniqueQuery() {
        return db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(getOwnerPath(), getOwnerName())
                .whereEqualTo(DbPathConstants.ROUTE_NAME, routeName);
    }

    public abstract static class Builder<T extends Builder<T>> {
        protected String ownerName;
        protected String ownerPath;
        protected String routeName;
        protected List<Point> points;
        protected Firestore db;

        public T setOwnerName(String ownerName) {
            this.ownerName = ownerName;
            return self();
        }

        public T setOwnerPath(String ownerPath) {
            this.ownerPath = ownerPath;
            return self();
        }

        public T setRouteName(String routeName) {
            this.routeName = routeName;
            return self();
        }

        public T setPoints(List<Point> points) {
            this.points = points;
            return self();
        }

        public abstract RouteUniquenessHandler build();
        protected abstract T self();
    }
}
