package hu.kristof.nagy.hikebookserver.model;

import com.google.cloud.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Objects;

public class GroupRoute extends Route {
    private String groupName;

    public GroupRoute() {
    }

    public GroupRoute(String groupName) {
        this(groupName, "", List.of(), "");
    }

    public GroupRoute(GroupRoute route) {
        this(route.getGroupName(), route.getRouteName(), route.getPoints(), route.getDescription());
    }

    public GroupRoute(String groupName, String routeName, List<Point> points, String description) {
        this.groupName = groupName;
        this.routeName = routeName;
        setPoints(points);
        this.description = description;
    }

    public static GroupRoute from(DocumentSnapshot documentSnapshot) {
        return new GroupRoute(
                Objects.requireNonNull(
                        documentSnapshot.toObject(GroupRoute.class)
                )
        );
    }

    @Override
    String getOwnerName() {
        return groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}
