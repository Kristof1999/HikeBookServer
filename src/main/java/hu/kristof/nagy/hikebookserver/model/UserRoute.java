package hu.kristof.nagy.hikebookserver.model;

import com.google.cloud.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Objects;

public class UserRoute extends Route {
    private String userName;

    public UserRoute() {

    }

    public UserRoute(String userName) {
        this(userName, "", List.of(), "");
    }

    public UserRoute(UserRoute route) {
        this(route.getUserName(), route.getRouteName(), route.getPoints(), route.getDescription());
    }

    public UserRoute(String userName, String routeName, List<Point> points, String description) {
        this.userName = userName;
        this.routeName = routeName;
        setPoints(points);
        this.description = description;
    }

    // TODO: try to eliminate this dependency, make it less error-prone
    // Note: if you change this, then don't forget to change RouteLoad's
    // loadRoutesForUser select call: if you want to get a field which is
    // not present in the select call, then you'll get a NullPointerException
    public static UserRoute from(DocumentSnapshot documentSnapshot) {
        return new UserRoute(
                Objects.requireNonNull(
                        documentSnapshot.toObject(UserRoute.class)
                )
        );
    }

    @Override
    String getOwnerName() {
        return userName;
    }

    public String getUserName() {
        return userName;
    }
}
