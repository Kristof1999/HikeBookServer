package hu.kristof.nagy.hikebookserver.model;

import java.util.Objects;

public class Point {
    private double latitude, longitude;
    private PointType type;
    private String title;

    public Point(double latitude, double longitude, PointType type, String title) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public PointType getType() {
        return type;
    }

    public void setType(PointType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.getLatitude(), getLatitude()) == 0 &&
                Double.compare(point.getLongitude(), getLongitude()) == 0 &&
                getType() == point.getType() && getTitle().equals(point.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLatitude(), getLongitude(), getType(), getTitle());
    }

    @Override
    public String toString() {
        return "Point{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", type=" + type +
                ", title='" + title + '\'' +
                '}';
    }
}
