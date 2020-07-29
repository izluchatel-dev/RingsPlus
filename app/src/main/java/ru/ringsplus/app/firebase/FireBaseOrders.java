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
import ru.ringsplus.app.OrderListViewAdapter;
import ru.ringsplus.app.R;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.DayStatus;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.OrderStatus;

import static ru.ringsplus.app.firebase.FireBaseCalendar.FIREBASE_CALENDAR_PATH;
import static ru.ringsplus.app.model.DayStatus.CloseDay;
import static ru.ringsplus.app.model.DayStatus.OpenDay;

public class FireBaseOrders {

    public static final String FIREBASE_ORDERS_PATH = FIREBASE_CALENDAR_PATH + "/%s/%s/orders";
    public static final String FIREBASE_DAY_STATUS_PATH = FIREBASE_CALENDAR_PATH + "/%s/%s/status";

    private OrderListViewAdapter mOrderListViewAdapter;
    private DayStatus mDayStatus = null;

    private DatabaseReference dayStatusReference;
    private DatabaseReference mOrdersStatusReference;

    private Integer lastOrderPosition = 0;

    public FireBaseOrders(RecyclerView recyclerView,
                          OrderListViewAdapter.OrderClickListener orderClickListener,
                          OrderListViewAdapter.OrderCheckStatusClickListener checkStatusClickListener,
                          DayItem dayItem, CheckDayStatusInterface checkDayStatusInterface) {

        String pathMonthAndYear = String.valueOf(dayItem.getMonth()) + String.valueOf(dayItem.getYear());

        String orderFullPath = String.format(FIREBASE_ORDERS_PATH, pathMonthAndYear, String.valueOf(dayItem.getDay()));
        String dayStatusFullPath = String.format(FIREBASE_DAY_STATUS_PATH, pathMonthAndYear, String.valueOf(dayItem.getDay()));

        mOrdersStatusReference = FirebaseDatabase.getInstance().getReference(orderFullPath);

        DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference(orderFullPath);
        ordersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<OrderItem> orderItems = new ArrayList<>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    OrderItem nextOrder = postSnapshot.getValue(OrderItem.class);

                    orderItems.add(nextOrder);
                }

                Comparator<OrderItem> compareOrdersItem = (OrderItem o1, OrderItem o2) -> o1.getTitle().compareTo( o2.getTitle() );
                Collections.sort(orderItems, compareOrdersItem);

                mOrderListViewAdapter = new OrderListViewAdapter(recyclerView.getContext(), orderItems);
                mOrderListViewAdapter.setOrderCheckStatusClickListener(checkStatusClickListener);
                mOrderListViewAdapter.setOrderClickListener(orderClickListener);
                recyclerView.setAdapter(mOrderListViewAdapter);

                if (lastOrderPosition > 0) {
                    recyclerView.scrollToPosition(lastOrderPosition);
                    lastOrderPosition = 0;
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

                checkDayStatusInterface.checkDayStatus(mDayStatus);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(recyclerView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setDayStatus(Context context, DayStatus dayStatus) {
        dayStatusReference.setValue(dayStatus, (error, ref) -> {
            if (error == null) {
                String mStatusMsg = "";
                if (dayStatus.equals(OpenDay)) {
                    mStatusMsg =  context.getString(R.string.day_status_change_open);
                } else if (dayStatus.equals(CloseDay)) {
                    mStatusMsg =  context.getString(R.string.day_status_change_close);
                }

                Toast.makeText(context, mStatusMsg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateOrderItemStatus(Context context, OrderItem orderItem, OrderStatus orderStatus) {
        orderItem.setOrderStatus(orderStatus);

        String changeStatusResult = "";
        switch (orderStatus) {
            case ExecuteOrder: {
                changeStatusResult = String.format(context.getString(R.string.change_order_status_execute_successful), orderItem.getTitle());
                break;
            }
            case ArchiveOrder: {
                changeStatusResult = String.format(context.getString(R.string.change_order_status_archive_successful), orderItem.getTitle());
                break;
            }
        }

        String finalChangeStatusResult = changeStatusResult;
        mOrdersStatusReference.child(orderItem.getId()).setValue(orderItem, (error, ref) -> {
            if (error == null) {
                Toast.makeText(context, finalChangeStatusResult, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public OrderListViewAdapter getOrderListViewAdapter() {
        return mOrderListViewAdapter;
    }

    public DayStatus getDayStatus() {
        return mDayStatus;
    }

    public Integer getLastOrderPosition() {
        return lastOrderPosition;
    }

    public void setLastOrderPosition(Integer lastOrderPosition) {
        this.lastOrderPosition = lastOrderPosition;
    }
}