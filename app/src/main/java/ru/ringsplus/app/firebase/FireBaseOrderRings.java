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
import ru.ringsplus.app.AddOrderRingsViewAdapter;
import ru.ringsplus.app.RingsViewAdapter;
import ru.ringsplus.app.model.RingItem;

public class FireBaseOrderRings {

    public static final String FIREBASE_RINGS_PATH = "rings";

    private List<RingItem> ringItems;

    public FireBaseOrderRings(RecyclerView recyclerView) {
        DatabaseReference ringsReference = FirebaseDatabase.getInstance().getReference(FIREBASE_RINGS_PATH);

        ringsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ringItems = new ArrayList<RingItem>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    RingItem nextRing = postSnapshot.getValue(RingItem.class);

                    nextRing.setCount(0);
                    ringItems.add(nextRing);
                }

                Comparator<RingItem> compareRingItem = (RingItem o1, RingItem o2) -> o1.getName().compareTo( o2.getName() );
                Collections.sort(ringItems, compareRingItem);

                AddOrderRingsViewAdapter mAddOrderRingsViewAdapter = new AddOrderRingsViewAdapter(recyclerView.getContext(), ringItems);

                mAddOrderRingsViewAdapter.setPlusClickListener((view, position) -> {
                    RingItem ringItem = mAddOrderRingsViewAdapter.getItem(position);

                    ringItem.setCount(ringItem.getCount() + 1);
                    mAddOrderRingsViewAdapter.notifyItemChanged(position);
                });

                mAddOrderRingsViewAdapter.setMinusClickListener((AddOrderRingsViewAdapter.MinusClickListener) (view, position) -> {
                    RingItem ringItem = mAddOrderRingsViewAdapter.getItem(position);

                    int ringCount = ringItem.getCount();

                    ringCount--;

                    if (ringCount < 0) {
                        ringCount = 0;
                    }

                    ringItem.setCount(ringCount);
                    mAddOrderRingsViewAdapter.notifyItemChanged(position);
                });

                recyclerView.setAdapter(mAddOrderRingsViewAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(recyclerView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public List<RingItem> getRingItems() {
        return ringItems;
    }
}
