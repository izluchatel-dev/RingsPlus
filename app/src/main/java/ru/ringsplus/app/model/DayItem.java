package ru.ringsplus.app.model;

import java.util.ArrayList;
import java.util.List;

public class DayItem {

    private int year;
    private int month;
    private int day;
    private List<OrderItem> mOrderItemList;
    private DayStatus mDayStatus = DayStatus.OpenDay;

    public DayItem(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;

        mOrderItemList = new ArrayList<>();
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<OrderItem> getOrderItemList() {
        return mOrderItemList;
    }

    public DayStatus getDayStatus() {
        return mDayStatus;
    }

    public void setDayStatus(DayStatus dayStatus) {
        mDayStatus = dayStatus;
    }

    @Override
    public String toString() {
        return "DayItem{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", mDayStatus=" + mDayStatus +
                '}';
    }
}
