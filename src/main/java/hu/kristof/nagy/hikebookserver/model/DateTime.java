package hu.kristof.nagy.hikebookserver.model;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DateTime {
    private int year, month, dayOfMonth, hourOfDay, minute;

    public DateTime() {
    }

    public DateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    public Map<String, Object> toMap() {
        var data = new HashMap<String, Object>();
        data.put(DbPathConstants.GROUP_HIKE_YEAR, year);
        data.put(DbPathConstants.GROUP_HIKE_MONTH, month);
        data.put(DbPathConstants.GROUP_HIKE_DAY_OF_MONTH, dayOfMonth);
        data.put(DbPathConstants.GROUP_HIKE_HOUR_OF_DAY, hourOfDay);
        data.put(DbPathConstants.GROUP_HIKE_MINUTE, minute);
        return data;
    }

    public static DateTime from(DocumentSnapshot documentSnapshot) {
        var year = Objects.requireNonNull(
                documentSnapshot.getLong(DbPathConstants.GROUP_HIKE_YEAR)
        ).intValue();
        var month = Objects.requireNonNull(
                documentSnapshot.getLong(DbPathConstants.GROUP_HIKE_MONTH)
        ).intValue();
        var dayOfMonth = Objects.requireNonNull(
                documentSnapshot.getLong(DbPathConstants.GROUP_HIKE_DAY_OF_MONTH)
        ).intValue();
        var hourOfDay = Objects.requireNonNull(
                documentSnapshot.getLong(DbPathConstants.GROUP_HIKE_HOUR_OF_DAY)
        ).intValue();
        var minute = Objects.requireNonNull(
                documentSnapshot.getLong(DbPathConstants.GROUP_HIKE_MINUTE)
        ).intValue();
        return new DateTime(year, month, dayOfMonth, hourOfDay, minute);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return year + "-" + month + "-" + dayOfMonth + ", " + hourOfDay + ":" + minute;
    }
}
