package ru.ringsplus.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.firebase.FireBaseConnnection;
import ru.ringsplus.app.firebase.FireBaseOrders;
import ru.ringsplus.app.firebase.FireBaseOrdersEditor;
import ru.ringsplus.app.model.AppOptions;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.DayStatus;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.OrderStatus;
import ru.ringsplus.app.utils.DrawableUtils;

import static java.lang.Boolean.TRUE;
import static ru.ringsplus.app.model.DayStatus.CloseDay;
import static ru.ringsplus.app.model.DayStatus.OpenDay;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_DAY;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_MONTH;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_YEAR;
import static ru.ringsplus.app.utils.CalendarUtils.getDayItemFromIntent;


public class OrderListActivity extends AppCompatActivity implements OrderListViewAdapter.OrderClickListener,
        OrderListViewAdapter.OrderCheckStatusClickListener,
        OrderListViewAdapter.OrderItemPopUpMenuListener {

    public static final String PUT_EDIT_ORDER_ID = "orderId";

    private DayItem mDayItem;

    private MenuItem dayStatusMenuItem;
    private FloatingActionButton mAddOrderButton;

    private RecyclerView recyclerOrderList;

    private FireBaseOrders mFireBaseOrders;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_list, menu);
        dayStatusMenuItem = menu.findItem(R.id.action_day_status);

        checkStatusDay(mFireBaseOrders.getDayStatus());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_day_status) {
            if (mFireBaseOrders.getDayStatus() != null) {
                if (mFireBaseOrders.getDayStatus() == OpenDay) {
                    mFireBaseOrders.setDayStatus(this, CloseDay);
                } else if (mFireBaseOrders.getDayStatus() == CloseDay) {
                    mFireBaseOrders.setDayStatus(this, OpenDay);
                }
            }
        } else if (item.getItemId() == R.id.action_calculate) {
            Intent summaIntent = new Intent(getBaseContext(), SummaActivity.class);
            summaIntent.putExtra(PUT_PARAM_DAY, mDayItem.getDay());
            summaIntent.putExtra(PUT_PARAM_MONTH, mDayItem.getMonth());
            summaIntent.putExtra(PUT_PARAM_YEAR, mDayItem.getYear());
            startActivity(summaIntent);
        } else if (item.getItemId() == R.id.action_help) {
            Intent helpIntent = new Intent(getBaseContext(), OrderHelpActivity.class);
            startActivity(helpIntent);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mDayItem = getDayItemFromIntent(getIntent());
        DrawableUtils.updateDayTitle(mDayItem, findViewById(R.id.day_title));

        mAddOrderButton = findViewById(R.id.add_order);

        recyclerOrderList = findViewById(R.id.orderList);
        recyclerOrderList.setLayoutManager(new LinearLayoutManager(this));

        mAddOrderButton.setOnClickListener(view -> {
            Intent addOrderIntent = new Intent(getBaseContext(), AddOrderActivity.class);
            addOrderIntent.putExtra(PUT_PARAM_DAY, mDayItem.getDay());
            addOrderIntent.putExtra(PUT_PARAM_MONTH, mDayItem.getMonth());
            addOrderIntent.putExtra(PUT_PARAM_YEAR, mDayItem.getYear());
            startActivity(addOrderIntent);
        });

        FireBaseConnnection.setConnectedChecker(this, true);
        mFireBaseOrders = new FireBaseOrders(recyclerOrderList, this, this,
                this, mDayItem, this::checkStatusDay);
    }

    private void checkStatusDay(DayStatus dayStatus) {
        if ((dayStatusMenuItem != null) && (mAddOrderButton != null)) {
            if (dayStatus != null) {
                if (dayStatus == OpenDay) {
                    dayStatusMenuItem.setIcon(R.drawable.lock_day);
                    dayStatusMenuItem.setTitle(R.string.lock_day_hint);

                    mAddOrderButton.setEnabled(true);
                    mAddOrderButton.setVisibility(View.VISIBLE);
                } else if (dayStatus == CloseDay) {
                    dayStatusMenuItem.setIcon(R.drawable.open_day);
                    dayStatusMenuItem.setTitle(R.string.open_day_hint);

                    mAddOrderButton.setEnabled(false);
                    mAddOrderButton.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        OrderItem mEditOrderItem = mFireBaseOrders.getOrderListViewAdapter().getItem(position);

        if ((mEditOrderItem.getOrderStatus() == OrderStatus.NewOrder) || (mEditOrderItem.getOrderStatus() == OrderStatus.ExecuteOrder)) {
            Intent editOrderIntent = new Intent(getBaseContext(), AddOrderActivity.class);
            editOrderIntent.putExtra(PUT_PARAM_DAY, mDayItem.getDay());
            editOrderIntent.putExtra(PUT_PARAM_MONTH, mDayItem.getMonth());
            editOrderIntent.putExtra(PUT_PARAM_YEAR, mDayItem.getYear());

            editOrderIntent.putExtra(PUT_EDIT_ORDER_ID, mEditOrderItem.getId());

            startActivity(editOrderIntent);
        }
    }

    @Override
    public void onCheckStatusButtonClick(View view, int position) {
        OrderItem mCheckOrderItem = mFireBaseOrders.getOrderListViewAdapter().getItem(position);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        String questionMsg = "";
        OrderStatus newOrderStatus = OrderStatus.NewOrder;
        switch (mCheckOrderItem.getOrderStatus()) {
            case NewOrder: {
                newOrderStatus = OrderStatus.ExecuteOrder;
                questionMsg = String.format(getString(R.string.change_order_status_new), mCheckOrderItem.getTitle());
                break;
            }
            case ExecuteOrder: {
                newOrderStatus = OrderStatus.ArchiveOrder;
                questionMsg = String.format(getString(R.string.change_order_status_execute), mCheckOrderItem.getTitle());
                break;
            }
        }

        dialogBuilder.setTitle(questionMsg);

        OrderStatus finalNewOrderStatus = newOrderStatus;
        dialogBuilder.setPositiveButton(R.string.item_dialog_yes, (dialog, which) -> {
            mFireBaseOrders.setLastOrderPosition(position - 1);
            mFireBaseOrders.updateOrderItemStatus(this, mCheckOrderItem, finalNewOrderStatus, mDayItem);
        });

        dialogBuilder.setNegativeButton(R.string.item_dialog_cancel, (dialog, which) -> dialog.cancel());

        dialogBuilder.show();
    }

    @Override
    public void onItemPopUpMenuClick(View view, int position) {
        OrderItem mCheckOrderItem = mFireBaseOrders.getOrderListViewAdapter().getItem(position);

        if (mCheckOrderItem.getOrderStatus() != OrderStatus.ArchiveOrder) {
            PopupMenu popup = new PopupMenu(this, view);

            popup.inflate(R.menu.menu_item_popup);

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_order_change_date_item:
                    {
                        final Calendar myCalendar = Calendar.getInstance();

                        DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, monthOfYear, dayOfMonth) -> {
                            DayItem newDayItem = new DayItem(year, monthOfYear + 1, dayOfMonth);

                            //Add new order
                            FireBaseOrders mFireBaseNewOrders = new FireBaseOrders(recyclerOrderList, this, this,
                                    this, newDayItem, this::checkStatusDay);

                            mFireBaseNewOrders.createCopyOrderItem(this, mCheckOrderItem, newDayItem);

//                            Update old order item
                            String newOrderDesc = getString(R.string.order_change_date_item_title_fmt);
                            String newOrderDate = String.format("%d.%d.%d", newDayItem.getDay(), newDayItem.getMonth(), newDayItem.getYear());
                            mCheckOrderItem.setDetails(String.format(newOrderDesc, mCheckOrderItem.getDetails(), newOrderDate)); ;

                            mFireBaseOrders.setLastOrderPosition(position - 1);
                            mFireBaseOrders.updateOrderItemStatus(this, mCheckOrderItem, OrderStatus.ArchiveOrder, mDayItem);
                        };

                        new DatePickerDialog(this, dateSetListener, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        return true;
                    }
                    default:
                        return false;
                }
            });

            popup.show();
        }
    }
}