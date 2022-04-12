package hu.kristof.nagy.hikebookserver.model;

import java.util.Objects;

public final class BrowseListItem {
    private String userName, routeName;

    public BrowseListItem() {
    }

    public BrowseListItem(String userName, String routeName) {
        this.userName = userName;
        this.routeName = routeName;
    }

    public String getUserName() {
        return userName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrowseListItem that = (BrowseListItem) o;
        return getUserName().equals(that.getUserName()) && getRouteName().equals(that.getRouteName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getRouteName());
    }

    @Override
    public String toString() {
        return "BrowseListItem{" +
                "userName='" + userName + '\'' +
                ", routeName='" + routeName + '\'' +
                '}';
    }
}
