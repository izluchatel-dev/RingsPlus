package ru.ringsplus.app.model;

import java.util.ArrayList;
import java.util.List;

public class StockCollection {

    private static StockCollection sDayCollection;
    private List<DayItem> mDayItemList;

    private StockCollection() {
        mDayItemList = new ArrayList<>();
    };

    public static StockCollection getInstance() {
        if (sDayCollection == null) {
            sDayCollection = new StockCollection();
        }

      return sDayCollection;
    }

    public List<DayItem> getDayCollection() {
        return mDayItemList;
    }

}
