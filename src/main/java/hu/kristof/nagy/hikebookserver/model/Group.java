package hu.kristof.nagy.hikebookserver.model;

import hu.kristof.nagy.hikebookserver.data.DbFields;

import java.util.HashMap;
import java.util.Map;

public final class Group {
    private String groupName;
    private String memberName;

    public Group() {
    }

    public Group(String groupName, String memberName) {
        this.groupName = groupName;
        this.memberName = memberName;
    }

    public Map<String, Object> toMap() {
        var data = new HashMap<String, Object>();
        data.put(DbFields.Group.NAME, groupName);
        data.put(DbFields.Group.MEMBER_NAME, memberName);
        return data;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getMemberName() {
        return memberName;
    }
}
