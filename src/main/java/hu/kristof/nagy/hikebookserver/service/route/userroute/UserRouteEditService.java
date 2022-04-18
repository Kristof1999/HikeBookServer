package hu.kristof.nagy.hikebookserver.service.route.userroute;

import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.routes.EditedRoute;
import hu.kristof.nagy.hikebookserver.model.Point;
import hu.kristof.nagy.hikebookserver.model.routes.EditedUserRoute;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import hu.kristof.nagy.hikebookserver.service.Util;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;
import hu.kristof.nagy.hikebookserver.service.route.RouteServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserRouteEditService {

    @Autowired
    private Firestore db;

    /**
     * Edits the route. If something changes, then it must be unique for the user.
     * @return true if the edited route is unique for the given user
     */
    public boolean editRoute(
            EditedUserRoute route,
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
                            ownerName, ownerPath, route.getNewUserRoute()
                    );
                }
            } else {
                if (oldPoints.equals(newPoints)) {
                    // only the description changed
                    saveChanges(
                            ownerName, ownerPath, newRouteName, route.getNewUserRoute()
                    );
                    return true;
                } else {
                    return updateRouteWithPointsChange(
                            ownerName, ownerPath, route.getNewUserRoute()
                    );
                }
            }
        } else {
            if (oldDescription.equals(newDescription)) {
                if (oldPoints.equals(newPoints)) {
                    return updateRouteWithNameChange(
                            ownerName, ownerPath, oldRouteName, route.getNewUserRoute()
                    );
                } else {
                    return updateRouteWithNameAndPointsChange(
                            ownerName, ownerPath, oldRouteName, route.getNewUserRoute()
                    );
                }
            } else {
                if (oldPoints.equals(newPoints)) {
                    return updateRouteWithNameChange(
                            ownerName, ownerPath, oldRouteName, route.getNewUserRoute()
                    );
                } else {
                    return updateRouteWithNameAndPointsChange(
                            ownerName, ownerPath, oldRouteName, route.getNewUserRoute()
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
        if (RouteServiceUtils.routeNameExistsForOwner(db, ownerName, route.getRouteName(), ownerPath)) {
            throw new IllegalArgumentException(
                    RouteServiceUtils.getRouteNameNotUniqueString(route.getRouteName())
            );
        } else {
            if (RouteServiceUtils.arePointsUniqueForOwner(db, ownerName, route.getPoints(), ownerPath)) {
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
        if (RouteServiceUtils.routeNameExistsForOwner(db, ownerName, route.getRouteName(), ownerPath)) {
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
        if (RouteServiceUtils.arePointsUniqueForOwner(db, ownerName, route.getPoints(), ownerPath)) {
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
            Util.handleListSize(queryDocs, documentSnapshots -> {
                var docRef = getDocToUpdate(documentSnapshots);
                Map<String, Object> data = route.toMap();
                return FutureUtil.handleFutureGet(() ->
                        docRef.set(data)
                                .get() // wait for write result
                );
            });
        } else {
            var transactionFuture = db.runTransaction(transaction -> {
                var query = getRouteQuery(ownerName, ownerPath, oldRouteName);
                var queryFuture = transaction.get(query);

                var queryDocs = queryFuture.get().getDocuments();
                return Util.handleListSize(queryDocs, documentSnapshots -> {
                    var docRef = getDocToUpdate(documentSnapshots);
                    Map<String, Object> data = route.toMap();
                    return transaction.set(docRef, data);
                });
            });
            // wait for write to finish
            FutureUtil.handleFutureGet(transactionFuture::get);
        }
    }

    private DocumentReference getDocToUpdate(List<? extends DocumentSnapshot> queryDocs) {
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
