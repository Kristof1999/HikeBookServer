package hu.kristof.nagy.hikebookserver.model;

import java.util.Objects;

public class User {
    private String name, password;
    private float avgSpeed;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        avgSpeed = 0f;
    }

    public User(String name, String password, float avgSpeed) {
        this.name = name;
        this.password = password;
        this.avgSpeed = avgSpeed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
