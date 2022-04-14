package hu.kristof.nagy.hikebookserver.model;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;

public class GroupHikeListHelper {
    private String groupHikeName;
    private DateTime dateTime;

    public GroupHikeListHelper() {
    }

    public GroupHikeListHelper(String groupHikeName, DateTime dateTime) {
        this.groupHikeName = groupHikeName;
        this.dateTime = dateTime;
    }

    public static GroupHikeListHelper from(DocumentSnapshot documentSnapshot) {
        var groupHikeName = documentSnapshot.getString(DbPathConstants.GROUP_HIKE_NAME);
        var dateTime = DateTime.from(documentSnapshot);
        return new GroupHikeListHelper(groupHikeName, dateTime);
    }

    public String getGroupHikeName() {
        return groupHikeName;
    }

    public void setGroupHikeName(String groupHikeName) {
        this.groupHikeName = groupHikeName;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
