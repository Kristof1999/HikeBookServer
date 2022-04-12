package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.routes.EditedRoute;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RouteEditService {

    @Autowired
    private Firestore db;

    // TODO: update javadoc
    /**
     * Edits the route.
     * @param route the edited route
     * @return true if the edited route is unique for the given user
     */
    public boolean editRoute(
            EditedRoute route,
            String ownerName,
            String ownerPath
    ) {
        String oldRouteName = route.getOldRoute().getRouteName();
        String newRouteName = route.getNewRoute().getRouteName();
        String oldDescription = route.getOldRoute().getDescription();
        String newDescription = route.getNewRoute().getDescription();
        List<Point> oldPoints = route.getOldRoute().getPoints();
        List<Point> newPoints = route.getNewRoute().getPoints();

        if (oldRouteName.equals(newRouteName)) {
            if (oldDescription.equals(newDescription)) {
                if (oldPoints.equals(newPoints)) {
                    // nothing changed, no need to save
                    return true;
                } else {
                    return updateRouteWithPointsChange(
                            ownerName, ownerPath, route.getNewRoute()
                    );
                }
            } else {
                if (oldPoints.equals(newPoints)) {
                    // only the description changed
                    saveChanges(
                            ownerName, ownerPath, newRouteName, route.getNewRoute()
                    );
                    return true;
                } else {
                    return updateRouteWithPointsChange(
                            ownerName, ownerPath, route.getNewRoute()
                    );
                }
            }
        } else {
            if (oldDescription.equals(newDescription)) {
                if (oldPoints.equals(newPoints)) {
                    return updateRouteWithNameChange(
                            ownerName, ownerPath, oldRouteName, route.getNewRoute()
                    );
                } else {
                    return updateRouteWithNameAndPointsChange(
                            ownerName, ownerPath, oldRouteName, route.getNewRoute()
                    );
                }
            } else {
                if (oldPoints.equals(newPoints)) {
                    return updateRouteWithNameChange(
                            ownerName, ownerPath, oldRouteName, route.getNewRoute()
                    );
                } else {
                    return updateRouteWithNameAndPointsChange(
                            ownerName, ownerPath, oldRouteName, route.getNewRoute()
                    );
                }
            }
        }
    }

    private boolean updateRouteWithNameAndPointsChange(
            String ownerName,
            String ownerPath,
            String oldRouteName,
            Route route
    ) {
        if (RouteServiceUtils.routeNameExists(db, ownerName, route.getRouteName(), ownerPath)) {
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(route.getRouteName())
            );
        } else {
            if (RouteServiceUtils.arePointsUnique(db, ownerName, route.getPoints(), ownerPath)) {
                saveChanges(ownerName, ownerPath, oldRouteName, route);
                return true;
            } else {
                throw new IllegalArgumentException(RouteServiceUtils.POINTS_NOT_UNIQE);
            }
        }
    }

    private boolean updateRouteWithNameChange(
            String ownerName,
            String ownerPath,
            String oldRouteName,
            Route route
    ) {
        if (RouteServiceUtils.routeNameExists(db, ownerName, route.getRouteName(), ownerPath)) {
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(route.getRouteName())
            );
        } else {
            saveChanges(ownerName, ownerPath, oldRouteName, route);
            return true;
        }
    }

    private boolean updateRouteWithPointsChange(
            String ownerName,
            String ownerPath,
            Route route
    ) {
        if (RouteServiceUtils.arePointsUnique(db, ownerName, route.getPoints(), ownerPath)) {
            saveChanges(ownerName, ownerPath, route.getRouteName(), route);
            return true;
        } else {
            throw new IllegalArgumentException(RouteServiceUtils.POINTS_NOT_UNIQE);
        }
    }

    private void saveChanges(
            String ownerName,
            String ownerPath,
            String oldRouteName,
            Route route
    ) {
        if (route instanceof UserRoute) {
            var queryFuture = getRouteQuery(ownerName, ownerPath, oldRouteName).get();
            var querySnapshot = FutureUtil.handleFutureGet(queryFuture::get);

            var queryDocs = querySnapshot.getDocuments();
            if (queryDocs.size() > 1) {
                throw new QueryException("Got more than 1 query document snapshot, but was only expecting 1. " +
                        "Route name: " + oldRouteName + ", owner name:" + ownerName);
            } else if (queryDocs.size() == 0) {
                throw new QueryException("Got no query document snapshot, but was only expecting 1. " +
                        "Route name: " + oldRouteName + ", owner name:" + ownerName);
            } else {
                var docRef = getDocToUpdate(queryDocs);
                Map<String, Object> data = route.toMap();
                FutureUtil.handleFutureGet(() ->
                        docRef.set(data)
                                .get() // wait for write result
                );
            }
        } else {
            var transactionFuture = db.runTransaction(transaction -> {
                var query = getRouteQuery(ownerName, ownerPath, oldRouteName);
                var queryFuture = transaction.get(query);

                var queryDocs = queryFuture.get().getDocuments();
                if (queryDocs.size() > 1) {
                    throw new QueryException("Got more than 1 query document snapshot, but was only expecting 1. " +
                            "Route name: " + oldRouteName + ", owner name:" + ownerName);
                } else if (queryDocs.size() == 0) {
                    throw new QueryException("Got no query document snapshot, but was only expecting 1. " +
                            "Route name: " + oldRouteName + ", owner name:" + ownerName);
                } else {
                    var docRef = getDocToUpdate(queryDocs);
                    Map<String, Object> data = route.toMap();
                    transaction.set(docRef, data);
                }
                return null;
            });
            // wait for write to finish
            FutureUtil.handleFutureGet(transactionFuture::get);
        }
    }

    private DocumentReference getDocToUpdate(List<QueryDocumentSnapshot> queryDocs) {
        String id = queryDocs.get(0).getId();
        return db.collection(DbPathConstants.COLLECTION_ROUTE)
                .document(id);
    }

    private Query getRouteQuery(
            String ownerName,
            String ownerPath,
            String oldRouteName
    ) {
        return db.collection(DbPathConstants.COLLECTION_ROUTE)
                .whereEqualTo(ownerPath, ownerName)
                .whereEqualTo(DbPathConstants.ROUTE_NAME, oldRouteName);
    }
}
