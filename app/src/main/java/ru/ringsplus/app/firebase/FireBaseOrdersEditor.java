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
import ru.ringsplus.app.AddOrderRingsViewAdapter;
import ru.ringsplus.app.R;
import ru.ringsplus.app.firebase.service.MessageSenderService;
import ru.ringsplus.app.model.AppOptions;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.DayStatus;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.RingItem;
import ru.ringsplus.app.model.RingOrderItem;

import static ru.ringsplus.app.firebase.FireBaseOrders.FIREBASE_DAY_STATUS_PATH;
import static ru.ringsplus.app.firebase.FireBaseOrders.FIREBASE_ORDERS_PATH;
import static ru.ringsplus.app.firebase.FireBaseRings.FIREBASE_RINGS_PATH;
import static ru.ringsplus.app.model.DayStatus.CloseDay;
import static ru.ringsplus.app.model.DayStatus.OpenDay;

public class FireBaseOrdersEditor {

    private DatabaseReference mOrdersReference;
    private ArrayList<RingItem> ringItems;
    private OrderItem editOrderItem;
    private DatabaseReference dayStatusReference;
    private DayStatus mDayStatus = null;

    public FireBaseOrdersEditor(RecyclerView recyclerView, String orderId, DayItem dayItem,
                                SetEditOrderItemParamInterface setEditOrderItemParamInterface) {

        String pathMonthAndYear = String.valueOf(dayItem.getMonth()) + String.valueOf(dayItem.getYear());

        String orderFullPath = String.format(FIREBASE_ORDERS_PATH, pathMonthAndYear, String.valueOf(dayItem.getDay()));
        String dayStatusFullPath = String.format(FIREBASE_DAY_STATUS_PATH, pathMonthAndYear, String.valueOf(dayItem.getDay()));

        mOrdersReference = FirebaseDatabase.getInstance().getReference(orderFullPath);

        DatabaseReference ringsReference = FirebaseDatabase.getInstance().getReference(FIREBASE_RINGS_PATH);
        ringsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ringItems = new ArrayList<>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    RingItem nextRing = postSnapshot.getValue(RingItem.class);

                    nextRing.setCount(0);
                    ringItems.add(nextRing);
                }

                Comparator<RingItem> compareRingItem = (RingItem o1, RingItem o2) -> o1.getName().compareTo( o2.getName() );
                Collections.sort(ringItems, compareRingItem);

                if ((orderId != null) && (!orderId.isEmpty())) {
                    DatabaseReference mChildOrdersReference = mOrdersReference.child(orderId);
                    mChildOrdersReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            editOrderItem = dataSnapshot.getValue(OrderItem.class);

                            if (editOrderItem != null)  {
                                if (editOrderItem.getRingOrderItemList() != null) {
                                    for (RingItem nextRingItem : ringItems) {
                                        for (RingOrderItem ringOrderItem : editOrderItem.getRingOrderItemList()) {
                                            if (nextRingItem.getName().equals(ringOrderItem.getRingName())) {
                                                nextRingItem.setCount(ringOrderItem.getCount());

                                                break;
                                            }
                                        }
                                    }
                                }

                                setEditOrderItemParamInterface.setEditOrderItem(editOrderItem);

                                setRecyclerViewProps(recyclerView);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(recyclerView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    setRecyclerViewProps(recyclerView);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(recyclerView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        dayStatusReference = FirebaseDatabase.getInstance().getReference(dayStatusFullPath);
        dayStatusReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDayStatus = dataSnapshot.getValue(DayStatus.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(recyclerView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setRecyclerViewProps(RecyclerView recyclerView) {
        AddOrderRingsViewAdapter mAddOrderRingsViewAdapter = new AddOrderRingsViewAdapter(recyclerView.getContext(), ringItems);

        mAddOrderRingsViewAdapter.setPlusClickListener((view, position) -> {
            RingItem ringItem = mAddOrderRingsViewAdapter.getItem(position);

            ringItem.setCount(ringItem.getCount() + 1);
            mAddOrderRingsViewAdapter.notifyItemChanged(position);
        });

        mAddOrderRingsViewAdapter.setMinusClickListener((view, position) -> {
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

    public List<RingItem> getRingItems() {
        return ringItems;
    }

    public OrderItem getEditOrderItem() {
        return editOrderItem;
    }

    public void updateOrderItem(Context context, OrderItem orderItem, Boolean createFlag, DayItem dayItem) {
        mOrdersReference.child(orderItem.getId()).setValue(orderItem, (error, ref) -> {
            if (error == null) {
                String mStatusMsg = String.format(context.getString(R.string.order_item_add_success_fmt), orderItem.getTitle());

                String mNotifyTitle;
                String mDayStr = String.format("%d.%d.%d", dayItem.getDay(), dayItem.getMonth(), dayItem.getYear());

                if (createFlag) {
                    mNotifyTitle = String.format(context.getString(R.string.create_new_order_notify_fmt), orderItem.getTitle(), mDayStr);
                } else {
                    mNotifyTitle = String.format(context.getString(R.string.edit_order_notify_fmt), orderItem.getTitle(), mDayStr);
                }

                StringBuilder mNotifyBody = new StringBuilder();
                if (orderItem.getRingOrderItemList() != null) {
                    for (RingOrderItem ringOrderItem:  orderItem.getRingOrderItemList()) {
                        mNotifyBody.append("* ");
                        mNotifyBody.append(ringOrderItem.getRingName());
                        mNotifyBody.append(" - ");
                        mNotifyBody.append(ringOrderItem.getCount());
                        mNotifyBody.append("\n");
                    }
                }

                String mAuthorTitle = String.format(context.getString(R.string.author_notify_fmt), AppOptions.getInstance().getUserName(context));
                mNotifyBody.append(mAuthorTitle);

                new MessageSenderService().sendPost(mNotifyTitle, mNotifyBody.toString());

                Toast.makeText(context, mStatusMsg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        if (mDayStatus == null) {
            dayStatusReference.setValue(OpenDay, (error, ref) -> {
                if (error != null) {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
