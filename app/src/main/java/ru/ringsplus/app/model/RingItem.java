package ru.ringsplus.app.model;

public class RingItem {

    private String name;
    private Integer count;

    public RingItem(String name) {
        this.count = 0;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "RingItem{" +
                "name='" + name + '\'' +
                ", count=" + count +
                '}';
    }
}
