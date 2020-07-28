package ru.ringsplus.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.firebase.FireBaseConnnection;
import ru.ringsplus.app.firebase.FireBaseOrders;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.DayStatus;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.StockCollection;
import ru.ringsplus.app.utils.DrawableUtils;

import static ru.ringsplus.app.model.DayStatus.CloseDay;
import static ru.ringsplus.app.model.DayStatus.OpenDay;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_DAY;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_MONTH;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_YEAR;
import static ru.ringsplus.app.utils.CalendarUtils.getDayItemFromIntent;

public class OrderListActivity extends AppCompatActivity implements OrderListViewAdapter.OrderClickListener, OrderListViewAdapter.OrderDeleteClickListener {

    public static final String PUT_EDIT_ORDER_ID = "orderId";

    private DayItem mDayItem;

    private ProgressBar progressBar;

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

        progressBar =  findViewById(R.id.progress_bar);

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

        FireBaseConnnection.setConnectedChecker(this::onShowProgressBar, true);

        mFireBaseOrders = new FireBaseOrders(recyclerOrderList, this, this,
                mDayItem.getDay(), mDayItem.getMonth(), mDayItem.getYear(), this::checkStatusDay);
    }

    private void onShowProgressBar(Boolean visible) {
        if (visible) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerOrderList.setVisibility(View.GONE);
            mAddOrderButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerOrderList.setVisibility(View.VISIBLE);

            if ((mFireBaseOrders.getDayStatus() == null) || (mFireBaseOrders.getDayStatus() == OpenDay))  {
                mAddOrderButton.setVisibility(View.VISIBLE);
            }
        }
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
    public void onDeleteButtonClick(View view, int position) {
        OrderItem mDeleteOrderItem = mFireBaseOrders.getOrderListViewAdapter().getItem(position);

        String mStatusMsg = String.format(getString(R.string.delete_order_item_ballon), mDeleteOrderItem.getTitle());

        mDayItem.getOrderItemList().remove(mDeleteOrderItem);

        if (mDayItem.getOrderItemList().isEmpty()) {
            StockCollection.getInstance().getDayCollection().remove(mDayItem);
        }

        Toast.makeText(this, mStatusMsg, Toast.LENGTH_SHORT).show();

        mFireBaseOrders.getOrderListViewAdapter().notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        OrderItem mEditOrderItem = mFireBaseOrders.getOrderListViewAdapter().getItem(position);

        Intent editOrderIntent = new Intent(getBaseContext(), AddOrderActivity.class);
        editOrderIntent.putExtra(PUT_PARAM_DAY, mDayItem.getDay());
        editOrderIntent.putExtra(PUT_PARAM_MONTH, mDayItem.getMonth());
        editOrderIntent.putExtra(PUT_PARAM_YEAR, mDayItem.getYear());

        editOrderIntent.putExtra(PUT_EDIT_ORDER_ID, mEditOrderItem.getId());

        startActivity(editOrderIntent);
    }

}