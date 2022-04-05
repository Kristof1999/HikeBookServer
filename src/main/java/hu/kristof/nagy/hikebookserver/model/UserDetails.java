package hu.kristof.nagy.hikebookserver.model;

import java.util.List;
import java.util.Objects;

public class UserDetails {
    private User user;
    private List<String> groupNames;

    public UserDetails() {
    }

    public UserDetails(User user, List<String> groupNames) {
        this.user = user;
        this.groupNames = groupNames;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(List<String> groupNames) {
        this.groupNames = groupNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetails that = (UserDetails) o;
        return getUser().equals(that.getUser()) && getGroupNames().equals(that.getGroupNames());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getGroupNames());
    }
}
