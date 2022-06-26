package hu.kristof.nagy.hikebookserver.model;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.data.DbFields;

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
        data.put(DbFields.GroupHike.YEAR, year);
        data.put(DbFields.GroupHike.MONTH, month);
        data.put(DbFields.GroupHike.DAY_OF_MONTH, dayOfMonth);
        data.put(DbFields.GroupHike.HOUR_OF_DAY, hourOfDay);
        data.put(DbFields.GroupHike.MINUTE, minute);
        return data;
    }

    public static DateTime from(DocumentSnapshot documentSnapshot) {
        var year = Objects.requireNonNull(
                documentSnapshot.getLong(DbFields.GroupHike.YEAR)
        ).intValue();
        var month = Objects.requireNonNull(
                documentSnapshot.getLong(DbFields.GroupHike.MONTH)
        ).intValue();
        var dayOfMonth = Objects.requireNonNull(
                documentSnapshot.getLong(DbFields.GroupHike.DAY_OF_MONTH)
        ).intValue();
        var hourOfDay = Objects.requireNonNull(
                documentSnapshot.getLong(DbFields.GroupHike.HOUR_OF_DAY)
        ).intValue();
        var minute = Objects.requireNonNull(
                documentSnapshot.getLong(DbFields.GroupHike.MINUTE)
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
