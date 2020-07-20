package ru.ringsplus.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.DayStatus;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.StockCollection;

import static ru.ringsplus.app.model.DayStatus.CloseDay;
import static ru.ringsplus.app.model.DayStatus.OpenDay;

public class OrderListActivity extends AppCompatActivity implements OrderListViewAdapter.OrderClickListener, OrderListViewAdapter.OrderDeleteClickListener {

    public static final String PUT_PARAM_DAY = "day";
    public static final String PUT_PARAM_MONTH = "month";
    public static final String PUT_PARAM_YEAR = "year";
    public static final String PUT_EDIT_ORDER_TITLE = "editOrderTitle";
    public static final String PUT_EDIT_ORDER_POSITION = "editOrderPosition";

    private static final int ADD_ORDER_REQUEST_ID = 1;
    private static final int EDIT_ORDER_REQUEST_ID = 2;

    private DayItem mDayItem;

    private Toolbar mToolbar;
    private TextView mDayTitle;
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
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        mDayItem = getDayItemFromIntent();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mDayTitle = findViewById(R.id.day_title);
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

    private DayItem getDayItemFromIntent() {
        DayItem resultDayItem = null;

        int year = getIntent().getIntExtra(PUT_PARAM_YEAR, 0);
        int month = getIntent().getIntExtra(PUT_PARAM_MONTH, 0);
        int day = getIntent().getIntExtra(PUT_PARAM_DAY, 0);

        for (DayItem nextDayItem: StockCollection.getInstance().getDayCollection()) {
            if ((nextDayItem.getDay() == day) &&
                    (nextDayItem.getMonth() == month) &&
                    (nextDayItem.getYear() == year)) {
                resultDayItem = nextDayItem;
                break;
            }
        }

        if (resultDayItem == null) {
            resultDayItem = new DayItem(year, month, day);
        }
        return resultDayItem;
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
        SetStatusDayTask setStatusDayTask = new SetStatusDayTask(this);
        setStatusDayTask.execute(dayStatus);
    }

    private void updateDayTitle() {
        if (mDayItem != null) {
            mDayTitle.setText(String.format("%d.%d.%d", mDayItem.getDay(), mDayItem.getMonth(), mDayItem.getYear()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mOrderListViewAdapter = new OrderListViewAdapter(this, mDayItem.getOrderItemList());
        mOrderListViewAdapter.setOrderDeleteClickListener(this);
        mOrderListViewAdapter.setOrderClickListener(this);
        recyclerOrderList.setAdapter(mOrderListViewAdapter);

        updateDayTitle();
    }

    @Override
    public void onDeleteButtonClick(View view, int position) {
        OrderItem mDeleteOrderItem = mOrderListViewAdapter.getItem(position);

        DeleteOrderItemTask deleteOrderItemTask = new DeleteOrderItemTask(this);
        deleteOrderItemTask.execute(mDeleteOrderItem);
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

    class SetStatusDayTask extends AsyncTask<DayStatus, Void, String> {

        private ProgressDialog dialog;
        private Activity mParentActivity;

        public SetStatusDayTask(OrderListActivity activity) {
            mParentActivity = activity;

            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage(getString(R.string.set_day_status));
            dialog.show();
        }

        @Override
        protected String doInBackground(DayStatus... dayStatuses) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String mStatusMsg = "";
            if (dayStatuses[0].equals(OpenDay)) {
                mStatusMsg =  getString(R.string.day_status_change_open);
            } else if (dayStatuses[0].equals(CloseDay)) {
                mStatusMsg =  getString(R.string.day_status_change_close);
            }

            try {
                mDayItem.setDayStatus(dayStatuses[0]);
            } catch (Exception e) {
                mStatusMsg = String.format(getString(R.string.day_status_err), e.getMessage());
            }

            return mStatusMsg;
        }

        @Override
        protected void onPostExecute(String statusMsg) {
            super.onPostExecute(statusMsg);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            checkStatusDay();

            Toast.makeText(mParentActivity, statusMsg, Toast.LENGTH_SHORT).show();
        }
    }

    class DeleteOrderItemTask extends AsyncTask<OrderItem, Void, String> {

        private ProgressDialog dialog;
        private Activity mParentActivity;

        public DeleteOrderItemTask(OrderListActivity activity) {
            mParentActivity = activity;

            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage(getString(R.string.delete_order_item_wait));
            dialog.show();
        }

        @Override
        protected String doInBackground(OrderItem... orderItems) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String mStatusMsg =  String.format(getString(R.string.delete_order_item_ballon), orderItems[0].getTitle());

            try {
                mDayItem.getOrderItemList().remove(orderItems[0]);

                if (mDayItem.getOrderItemList().isEmpty()) {
                    StockCollection.getInstance().getDayCollection().remove(mDayItem);
                }
            } catch (Exception e) {
                mStatusMsg = String.format(getString(R.string.delete_order_item_err), e.getMessage());
            }

            return mStatusMsg;
        }

        @Override
        protected void onPostExecute(String statusMsg) {
            super.onPostExecute(statusMsg);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            mOrderListViewAdapter.notifyDataSetChanged();

            Toast.makeText(mParentActivity, statusMsg, Toast.LENGTH_SHORT).show();
        }
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
                   mDayItem = getDayItemFromIntent();

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