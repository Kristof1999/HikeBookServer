package hu.kristof.nagy.hikebookserver.model;

import com.google.common.primitives.Bytes;

import java.util.*;

public class User {
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String AVG_SPEED = "avgSpeed";

    private String name;
    private String password;
    private float avgSpeed;

    public User() {
    }

    public User(String name, String password) {
        this(name, password, 0f);
    }

    public User(String name, String password, float avgSpeed) {
        this.name = name;
        this.password = password;
        this.avgSpeed = avgSpeed;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
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
}
