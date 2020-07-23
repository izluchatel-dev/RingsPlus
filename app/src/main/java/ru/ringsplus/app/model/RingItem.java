package ru.ringsplus.app.model;

import com.google.firebase.database.Exclude;

public class RingItem {

    private String id;
    private String name;
    private Integer count;

    public RingItem() {
    }

    public RingItem(String id, String name) {
        this.id = id;
        this.name = name;
        this.count = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
