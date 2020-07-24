package ru.ringsplus.app.firebase;

import android.content.Context;
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
import ru.ringsplus.app.R;
import ru.ringsplus.app.RingsViewAdapter;
import ru.ringsplus.app.model.RingItem;

public class FireBaseRings {

    public static final String FIREBASE_RINGS_PATH = "rings";

    private DatabaseReference mRingsReference;

    public FireBaseRings(RecyclerView recyclerView, RingsViewAdapter.DeleteClickListener deleteClickListener) {
        mRingsReference = FirebaseDatabase.getInstance().getReference(FIREBASE_RINGS_PATH);
        mRingsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<RingItem> ringItems = new ArrayList<RingItem>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    RingItem nextRing = postSnapshot.getValue(RingItem.class);

                    ringItems.add(nextRing);
                }

                Comparator<RingItem> compareRingItem = (RingItem o1, RingItem o2) -> o1.getName().compareTo( o2.getName() );
                Collections.sort(ringItems, compareRingItem);

                RingsViewAdapter ringsAdapter = new RingsViewAdapter(recyclerView.getContext(), ringItems);
                ringsAdapter.setDeleteClickListener(deleteClickListener);
                recyclerView.setAdapter(ringsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(recyclerView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addRingItem(Context context, RingItem addRingItem) {
        mRingsReference.child(addRingItem.getId()).setValue(addRingItem, (error, ref) -> {
            if (error == null) {
                String mStatusMsg = String.format(context.getString(R.string.add_item_ballon), addRingItem.getName());

                Toast.makeText(context, mStatusMsg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteRingItem(Context context, RingItem deleteRingItem) {
        mRingsReference.child(deleteRingItem.getId()).removeValue((error, ref) -> {
            if (error == null) {
                String mStatusMsg = String.format(context.getString(R.string.delete_item_ballon), deleteRingItem.getName());

                Toast.makeText(context, mStatusMsg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
