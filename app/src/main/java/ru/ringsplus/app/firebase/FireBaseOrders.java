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
import ru.ringsplus.app.model.DayStatus;
import ru.ringsplus.app.model.OrderItem;

import static ru.ringsplus.app.firebase.FireBaseCalendar.FIREBASE_CALENDAR_PATH;
import static ru.ringsplus.app.model.DayStatus.CloseDay;
import static ru.ringsplus.app.model.DayStatus.OpenDay;

public class FireBaseOrders {

    public static final String FIREBASE_ORDERS_PATH = FIREBASE_CALENDAR_PATH + "/%s/%s/orders";
    public static final String FIREBASE_DAY_STATUS_PATH = FIREBASE_CALENDAR_PATH + "/%s/%s/status";

    private OrderListViewAdapter mOrderListViewAdapter;
    private DayStatus mDayStatus = null;

    private DatabaseReference dayStatusReference;

    public FireBaseOrders(RecyclerView recyclerView,
                          OrderListViewAdapter.OrderClickListener orderClickListener,
                          OrderListViewAdapter.OrderDeleteClickListener orderDeleteClickListener,
                          int day, int month, int year, CheckDayStatusInterface checkDayStatusInterface) {

        String pathMonthAndYear = String.valueOf(month) + String.valueOf(year);

        String orderFullPath = String.format(FIREBASE_ORDERS_PATH, pathMonthAndYear, String.valueOf(day));
        String dayStatusFullPath = String.format(FIREBASE_DAY_STATUS_PATH, pathMonthAndYear, String.valueOf(day));

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
                mOrderListViewAdapter.setOrderDeleteClickListener(orderDeleteClickListener);
                mOrderListViewAdapter.setOrderClickListener(orderClickListener);
                recyclerView.setAdapter(mOrderListViewAdapter);
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

    public OrderListViewAdapter getOrderListViewAdapter() {
        return mOrderListViewAdapter;
    }

    public DayStatus getDayStatus() {
        return mDayStatus;
    }
}
