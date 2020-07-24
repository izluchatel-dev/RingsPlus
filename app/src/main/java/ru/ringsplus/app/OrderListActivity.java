package ru.ringsplus.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.Format;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.DayStatus;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.StockCollection;
import ru.ringsplus.app.utils.CalendarUtils;
import ru.ringsplus.app.utils.DrawableUtils;

import static ru.ringsplus.app.model.DayStatus.CloseDay;
import static ru.ringsplus.app.model.DayStatus.OpenDay;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_DAY;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_MONTH;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_YEAR;
import static ru.ringsplus.app.utils.CalendarUtils.getDayItemFromIntent;

public class OrderListActivity extends AppCompatActivity implements OrderListViewAdapter.OrderClickListener, OrderListViewAdapter.OrderDeleteClickListener {

    public static final String PUT_EDIT_ORDER_TITLE = "editOrderTitle";
    public static final String PUT_EDIT_ORDER_POSITION = "editOrderPosition";

    private static final int ADD_ORDER_REQUEST_ID = 1;
    private static final int EDIT_ORDER_REQUEST_ID = 2;

    private DayItem mDayItem;

    private MenuItem dayStatusMenuItem;
    private FloatingActionButton mAddOrderButton;

    private OrderListViewAdapter mOrderListViewAdapter;
    private RecyclerView recyclerOrderList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_list, menu);
        dayStatusMenuItem = menu.findItem(R.id.action_day_status);

        checkStatusDay();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_day_status) {
            if (mDayItem != null) {
                if (mDayItem.getDayStatus().equals(OpenDay)) {
                    setStatusDay(CloseDay);
                } else if (mDayItem.getDayStatus().equals(CloseDay)) {
                    setStatusDay(OpenDay);
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
            startActivityForResult(addOrderIntent, ADD_ORDER_REQUEST_ID);
        });
    }

    private void checkStatusDay() {
        if ((mDayItem != null) && (dayStatusMenuItem != null)) {
            if (mDayItem.getDayStatus().equals(OpenDay)) {
                dayStatusMenuItem.setIcon(R.drawable.lock_day);
                dayStatusMenuItem.setTitle(R.string.lock_day_hint);

                mAddOrderButton.setEnabled(true);
                mAddOrderButton.setVisibility(View.VISIBLE);
            } else if (mDayItem.getDayStatus().equals(CloseDay)) {
                dayStatusMenuItem.setIcon(R.drawable.open_day);
                dayStatusMenuItem.setTitle(R.string.open_day_hint);

                mAddOrderButton.setEnabled(false);
                mAddOrderButton.setVisibility(View.GONE);
            }
        }
    }

    private void setStatusDay(DayStatus dayStatus) {
        mDayItem.setDayStatus(dayStatus);

        String mStatusMsg = "";
        if (dayStatus.equals(OpenDay)) {
            mStatusMsg =  getString(R.string.day_status_change_open);
        } else if (dayStatus.equals(CloseDay)) {
            mStatusMsg =  getString(R.string.day_status_change_close);
        }

        checkStatusDay();

        Toast.makeText(this, mStatusMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mOrderListViewAdapter = new OrderListViewAdapter(this, mDayItem.getOrderItemList());
        mOrderListViewAdapter.setOrderDeleteClickListener(this);
        mOrderListViewAdapter.setOrderClickListener(this);
        recyclerOrderList.setAdapter(mOrderListViewAdapter);
    }

    @Override
    public void onDeleteButtonClick(View view, int position) {
        OrderItem mDeleteOrderItem = mOrderListViewAdapter.getItem(position);

        String mStatusMsg = String.format(getString(R.string.delete_order_item_ballon), mDeleteOrderItem.getTitle());

        mDayItem.getOrderItemList().remove(mDeleteOrderItem);

        if (mDayItem.getOrderItemList().isEmpty()) {
            StockCollection.getInstance().getDayCollection().remove(mDayItem);
        }

        Toast.makeText(this, mStatusMsg, Toast.LENGTH_SHORT).show();

        mOrderListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        OrderItem mClickOrderItem = mOrderListViewAdapter.getItem(position);

        Intent editOrderIntent = new Intent(getBaseContext(), AddOrderActivity.class);
        editOrderIntent.putExtra(PUT_PARAM_DAY, mDayItem.getDay());
        editOrderIntent.putExtra(PUT_PARAM_MONTH, mDayItem.getMonth());
        editOrderIntent.putExtra(PUT_PARAM_YEAR, mDayItem.getYear());
        editOrderIntent.putExtra(PUT_EDIT_ORDER_TITLE, mClickOrderItem.getTitle());
        editOrderIntent.putExtra(PUT_EDIT_ORDER_POSITION, position);

        startActivityForResult(editOrderIntent, EDIT_ORDER_REQUEST_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        if (((requestCode == ADD_ORDER_REQUEST_ID) || (requestCode == EDIT_ORDER_REQUEST_ID)) && (resultCode == RESULT_OK)) {
            String orderTitle = data.getStringExtra(AddOrderActivity.ORDER_TITLE_PUT);
            Integer orderPosition = data.getIntExtra(PUT_EDIT_ORDER_POSITION, 0);

            if (!orderTitle.isEmpty()) {
                String changeMessage = String.format(getString(R.string.order_item_add_success_fmt), orderTitle);

               if (requestCode == ADD_ORDER_REQUEST_ID) {
                   mDayItem = CalendarUtils.getDayItemFromIntent(getIntent());

                   mOrderListViewAdapter.notifyItemInserted(mDayItem.getOrderItemList().size());

                   recyclerOrderList.scrollToPosition(mOrderListViewAdapter.getItemCount() - 1);
               } else if (requestCode == EDIT_ORDER_REQUEST_ID) {
                   if (orderPosition > 0) {
                       mOrderListViewAdapter.notifyItemChanged(orderPosition);

                       recyclerOrderList.scrollToPosition(orderPosition);
                   }
               }

                Toast.makeText(getApplicationContext(),changeMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

}