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
import ru.ringsplus.app.model.RingItem;
import ru.ringsplus.app.model.RingOrderItem;

public class FireBaseRingsSumma {

    public static final String FIREBASE_RINGS_PATH = "rings";

    private DatabaseReference mRingsReference;

    public FireBaseRingsSumma(RecyclerView recyclerView, DayItem dayItem) {
        mRingsReference = FirebaseDatabase.getInstance().getReference(FIREBASE_RINGS_PATH);
        mRingsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<RingItem> ringItems = new ArrayList<RingItem>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    RingItem nextRing = postSnapshot.getValue(RingItem.class);

                    nextRing.setCount(0);

                    for (OrderItem nextOrder: dayItem.getOrderItemList()) {
                        for (RingOrderItem nextRingOrder: nextOrder.getRingOrderItemList()) {
                            if (nextRing.getName().equals(nextRingOrder.getRingName())) {
                                nextRing.setCount(nextRing.getCount() + nextRingOrder.getCount());
                            }
                        }
                    }

                    if (nextRing.getCount() > 0) {
                        ringItems.add(nextRing);
                    }
                }

                Comparator<RingItem> compareRingItem = (RingItem o1, RingItem o2) -> o1.getName().compareTo( o2.getName() );
                Collections.sort(ringItems, compareRingItem);

                SummaViewAdapter mSummaViewAdapter = new SummaViewAdapter(recyclerView.getContext(), ringItems);
                recyclerView.setAdapter(mSummaViewAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(recyclerView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
