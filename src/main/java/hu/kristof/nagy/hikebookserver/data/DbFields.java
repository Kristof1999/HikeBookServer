package hu.kristof.nagy.hikebookserver.data;

public class DbFields {
    public static final class User {
        public static final String NAME = "name";
        public static final String PASSWORD = "password";
        public static final String AVG_SPEED = "avgSpeed";
    }

    public static class Route {
        public static final String ROUTE_NAME = "routeName";
        public static final String POINTS = "points";
        public static final String DESCRIPTION = "description";
    }

    public static final class UserRoute extends Route {
        public static final String NAME = "userName";
    }

    public static final class GroupRoute extends Route {
        public static final String NAME = "groupName";
    }

    public static final class GroupHikeRoute extends Route {
        public static final String NAME = "groupHikeName";
    }

    public static final class Group {
        public static final String NAME = "name";
        public static final String MEMBER_NAME = "memberName";
    }

    public static final class GroupHike {
        public static final String NAME = "name";
        public static final String PARTICIPANT_NAME = "participantName";
        public static final String DATE_TIME = "dateTime";
        public static final String YEAR = "year";
        public static final String MONTH = "month";
        public static final String DAY_OF_MONTH = "dayOfMonth";
        public static final String HOUR_OF_DAY = "hourOfDay";
        public static final String MINUTE = "minute";
    }
}