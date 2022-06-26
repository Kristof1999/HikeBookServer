package hu.kristof.nagy.hikebookserver.model;

import hu.kristof.nagy.hikebookserver.data.DbFields;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class User {
    private String name;
    private String password;
    private double avgSpeed;

    public User() {}

    public User(String name, String password) {
        this(name, password, 3.0);
    }

    public User(String name, String password, double avgSpeed) {
        this.name = name;
        this.password = password;
        this.avgSpeed = avgSpeed;
    }

    public Map<String, Object> toMap() {
        var data = new HashMap<String, Object>();
        data.put(DbFields.User.NAME, getName());
        data.put(DbFields.User.PASSWORD, getPassword());
        data.put(DbFields.User.AVG_SPEED, getAvgSpeed());
        return data;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User userAuth = (User) o;
        return getName().equals(userAuth.getName()) && getPassword().equals(userAuth.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPassword());
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", avgSpeed=" + avgSpeed +
                '}';
    }
}
