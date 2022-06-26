package hu.kristof.nagy.hikebookserver.model;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbFields;

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
        var groupHikeName = documentSnapshot.getString(DbFields.GroupHike.NAME);
        var dateTime = DateTime.from(documentSnapshot);
        return new GroupHikeListHelper(groupHikeName, dateTime);
    }

    public static String[] getSelectPaths() {
        return new String[] {
                DbFields.GroupHike.NAME,
                DbFields.GroupHike.YEAR,
                DbFields.GroupHike.MONTH,
                DbFields.GroupHike.DAY_OF_MONTH,
                DbFields.GroupHike.HOUR_OF_DAY,
                DbFields.GroupHike.MINUTE
        };
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
