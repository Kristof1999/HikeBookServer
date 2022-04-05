package hu.kristof.nagy.hikebookserver.model;

import java.util.List;
import java.util.Objects;

public class GroupDetails {
    private Group group;
    private List<String> memberNames;

    public GroupDetails() {
    }

    public GroupDetails(Group group, List<String> memberNames) {
        this.group = group;
        this.memberNames = memberNames;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<String> getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(List<String> memberNames) {
        this.memberNames = memberNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupDetails that = (GroupDetails) o;
        return getGroup().equals(that.getGroup()) && getMemberNames().equals(that.getMemberNames());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroup(), getMemberNames());
    }
}
