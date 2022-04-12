package hu.kristof.nagy.hikebookserver.model;

import hu.kristof.nagy.hikebookserver.data.DbPathConstants;

import java.util.HashMap;
import java.util.Map;

public final class Group {
    private final String groupName;
    private final String memberName;

    public Group(String groupName, String memberName) {
        this.groupName = groupName;
        this.memberName = memberName;
    }

    public Map<String, Object> toMap() {
        var data = new HashMap<String, Object>();
        data.put(DbPathConstants.GROUP_NAME, groupName);
        data.put(DbPathConstants.GROUP_MEMBER_NAME, memberName);
        return data;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getMemberName() {
        return memberName;
    }
}
