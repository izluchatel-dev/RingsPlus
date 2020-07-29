package ru.ringsplus.app.model;

import java.util.ArrayList;
import java.util.List;

public class OrderItem {

    private String id;
    private String title;
    private String details;
    private String author;
    private OrderStatus orderStatus;
    private List<RingOrderItem> mRingOrderItemList;

    public OrderItem() {
    }

    public OrderItem(String id, String title, String details, String author) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.author = author;
        this.orderStatus = OrderStatus.NewOrder;

        mRingOrderItemList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<RingOrderItem> getRingOrderItemList() {
        return mRingOrderItemList;
    }

    public void setRingOrderItemList(List<RingOrderItem> ringOrderItemList) {
        mRingOrderItemList = ringOrderItemList;
    }
}
