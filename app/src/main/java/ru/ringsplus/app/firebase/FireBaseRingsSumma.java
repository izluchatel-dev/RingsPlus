package ru.ringsplus.app.firebase;

import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.SummaViewAdapter;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.OrderStatus;
import ru.ringsplus.app.model.RingItem;
import ru.ringsplus.app.model.RingOrderItem;

import static ru.ringsplus.app.firebase.FireBaseOrders.FIREBASE_ORDERS_PATH;
import static ru.ringsplus.app.firebase.FireBaseRings.FIREBASE_RINGS_PATH;

public class FireBaseRingsSumma {

    public FireBaseRingsSumma(RecyclerView recyclerView, DayItem dayItem) {

        String pathMonthAndYear = String.valueOf(dayItem.getMonth()) + String.valueOf(dayItem.getYear());

        String orderFullPath = String.format(FIREBASE_ORDERS_PATH, pathMonthAndYear, String.valueOf(dayItem.getDay()));

        DatabaseReference ringsReference = FirebaseDatabase.getInstance().getReference(FIREBASE_RINGS_PATH);
        ringsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<RingItem> ringItems = new ArrayList<>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    RingItem nextRing = postSnapshot.getValue(RingItem.class);

                    if (nextRing != null) {
                        nextRing.setCount(0);
                        ringItems.add(nextRing);
                    }
                }

                DatabaseReference mOrdersReference = FirebaseDatabase.getInstance().getReference(orderFullPath);
                mOrdersReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            OrderItem nextOrder = postSnapshot.getValue(OrderItem.class);

                            if ((nextOrder != null) && (nextOrder.getRingOrderItemList() != null)) {
                                if (nextOrder.getOrderStatus() == OrderStatus.NewOrder) {
                                    for (RingOrderItem nextRingOrderItem: nextOrder.getRingOrderItemList()) {
                                        for (RingItem nextRingItem: ringItems) {
                                            if (nextRingItem.getName().equals(nextRingOrderItem.getRingName())) {
                                                nextRingItem.setCount(nextRingItem.getCount() + nextRingOrderItem.getCount());
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        List<RingItem> finalRingItems = new ArrayList<>();
                        for (RingItem nextRingItem: ringItems) {
                            if (nextRingItem.getCount() > 0) {
                                finalRingItems.add(nextRingItem);
                            }
                        }

                        Comparator<RingItem> compareRingItem = (RingItem o1, RingItem o2) -> o1.getName().compareTo( o2.getName() );
                        Collections.sort(finalRingItems, compareRingItem);

                        SummaViewAdapter mSummaViewAdapter = new SummaViewAdapter(recyclerView.getContext(), finalRingItems);
                        recyclerView.setAdapter(mSummaViewAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(recyclerView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(recyclerView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
