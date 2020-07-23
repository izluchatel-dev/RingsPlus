package ru.ringsplus.app.model;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StockCollection {

    public static final String FIREBASE_RINGS_PATH = "rings";
    public static final String FIREBASE_CONNECTION_INFO = ".info/connected";

    private static StockCollection sDayCollection;
    private List<DayItem> mDayItemList;
    private List<RingItem> mRingItems;

    private StockCollection() {
        mDayItemList = new ArrayList<>();
        mRingItems = new ArrayList<>();
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

    public List<RingItem> getRingItems() {
        return mRingItems;
    }

    public void onRingsCollectionDataChange(DataSnapshot dataSnapshot) {
        mRingItems.clear();

        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            RingItem nextRing = postSnapshot.getValue(RingItem.class);

            mRingItems.add(nextRing);
        }

        Comparator<RingItem> compareRingItem = (RingItem o1, RingItem o2) -> o1.getName().compareTo( o2.getName() );
        Collections.sort(mRingItems, compareRingItem);
    }

}
