package hu.kristof.nagy.hikebookserver.model;

import java.util.Objects;

public class Point {
    private double latitude, longitude;
    private PointType type;

    public Point() {
        this(0.0, 0.0, PointType.NEW);
    }

    public Point(double latitude, double longitude, PointType type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.getLatitude(), getLatitude()) == 0 && Double.compare(point.getLongitude(), getLongitude()) == 0 && getType() == point.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLatitude(), getLongitude(), getType());
    }
}
