package ru.ringsplus.app.model;

import java.util.ArrayList;
import java.util.List;

public class OrderItem {

    private String title;
    private String details;
    private List<RingOrderItem> mRingOrderItemList;
    private String author;

    public OrderItem(String title, String details, String author) {
        this.title = title;
        this.details = details;
        this.author = author;

        mRingOrderItemList = new ArrayList<>();
    }

    public String getTitle() {
        if (title.isEmpty()) {
            title = " - ";
        }

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        if (details.isEmpty()) {
            details = " - ";
        }

        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<RingOrderItem> getRingOrderItemList() {
        return mRingOrderItemList;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "title='" + title + '\'' +
                ", details='" + details + '\'' +
                ", mRingOrderItemList=" + mRingOrderItemList +
                ", author='" + author + '\'' +
                '}';
    }
}
