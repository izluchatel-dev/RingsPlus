package ru.ringsplus.app.model;

public class RingOrderItem {

    private String mRingName;
    private int count;

    public RingOrderItem(String ringName, int count) {
        mRingName = ringName;
        this.count = count;
    }

    public String getRingName() {
        return mRingName;
    }

    public void setRingName(String ringName) {
        mRingName = ringName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "RingOrderItem{" +
                "mRingName='" + mRingName + '\'' +
                ", count=" + count +
                '}';
    }
}
