package ru.ringsplus.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.model.AppOptions;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.RingItem;
import ru.ringsplus.app.model.RingOrderItem;
import ru.ringsplus.app.model.StockCollection;

import static ru.ringsplus.app.OrderListActivity.PUT_EDIT_ORDER_POSITION;
import static ru.ringsplus.app.OrderListActivity.PUT_EDIT_ORDER_TITLE;

public class AddOrderActivity extends AppCompatActivity implements AddOrderRingsViewAdapter.MinusClickListener, AddOrderRingsViewAdapter.PlusClickListener {

    public static final String PUT_PARAM_DAY = "day";
    public static final String PUT_PARAM_MONTH = "month";
    public static final String PUT_PARAM_YEAR = "year";

    public static final String ORDER_TITLE_PUT = "orderTitle";

    private Toolbar mToolbar;
    private TextView mDayTitle;
    private AddOrderRingsViewAdapter mAddOrderRingsViewAdapter;
    private RecyclerView recyclerRingsList;
    private EditText mOrderTitleName;
    private EditText mOrderDetails;
    private Button mSaveButton;

    private DayItem mDayItem;

    public String editOrderTitle = "";
    public Integer editOrderPosition = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_order, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        clearRingCount();

        mDayItem = getDayItemFromIntent();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mDayTitle = findViewById(R.id.add_day_title);

        recyclerRingsList = findViewById(R.id.allRingsList);
        recyclerRingsList.setLayoutManager(new LinearLayoutManager(this));

        mOrderTitleName = findViewById(R.id.titleName);
        mOrderTitleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkSaveButtonEnabled();
            }
        });

        mOrderDetails = findViewById(R.id.detailsText);
        mSaveButton = findViewById(R.id.orderItemSave);
        mSaveButton.setOnClickListener( view -> {
            String orderTitle = String.valueOf(mOrderTitleName.getText()).trim();
            String orderDetails = String.valueOf(mOrderDetails.getText()).trim();
            String orderAuthor = AppOptions.getInstance().getUserName(this);

            //Было редактирование
            if ((editOrderTitle != null) && (!editOrderTitle.isEmpty())) {
                if ((editOrderTitle.equals(orderTitle)) || (!hasOrderItemInCurrentDay(orderTitle))) {
                    OrderItem editOrderItem = getCurrentEditOrderItemByTitle(editOrderTitle);

                    if (editOrderItem != null) {
                        editOrderItem.setTitle(orderTitle);
                        editOrderItem.setDetails(orderDetails);
                        editOrderItem.setAuthor(orderAuthor);
                        editOrderItem.getRingOrderItemList().clear();

                        for (RingItem nextRingItem : StockCollection.getInstance().getRingItems()) {
                            if (nextRingItem.getCount() > 0) {
                                editOrderItem.getRingOrderItemList().add(new RingOrderItem(nextRingItem.getName(), nextRingItem.getCount()));
                            }
                        }

                        SaveOrderItemTask saveOrderItemTask = new SaveOrderItemTask(this, true);
                        saveOrderItemTask.execute(editOrderItem);
                    }
                } else {
                    Toast.makeText(this, String.format(getString(R.string.add_order_item_exist_msg), orderTitle), Toast.LENGTH_SHORT).show();
                }
            } else { //Добавление
                if (!hasOrderItemInCurrentDay(orderTitle)) {
                    OrderItem addOrderItem = new OrderItem(orderTitle, orderDetails, orderAuthor);

                    for (RingItem nextRingItem : StockCollection.getInstance().getRingItems()) {
                        if (nextRingItem.getCount() > 0) {
                            addOrderItem.getRingOrderItemList().add(new RingOrderItem(nextRingItem.getName(), nextRingItem.getCount()));
                        }
                    }

                    SaveOrderItemTask saveOrderItemTask = new SaveOrderItemTask(this, false);
                    saveOrderItemTask.execute(addOrderItem);
                } else {
                    Toast.makeText(this, String.format(getString(R.string.add_order_item_exist_msg), orderTitle), Toast.LENGTH_SHORT).show();
                }
            }
        });

        fillOrderItemByOrderTitle();
    }

    private void fillOrderItemByOrderTitle() {
        editOrderTitle = getIntent().getStringExtra(PUT_EDIT_ORDER_TITLE);
        editOrderPosition = getIntent().getIntExtra(PUT_EDIT_ORDER_POSITION, 0);

        if ((editOrderTitle != null) && (!editOrderTitle.isEmpty()))
            for (OrderItem nextOrderItem: mDayItem.getOrderItemList()) {
                if (nextOrderItem.getTitle().equals(editOrderTitle)) {
                    mOrderTitleName.setText(nextOrderItem.getTitle());
                    mOrderDetails.setText(nextOrderItem.getDetails());

                    for (RingItem nextRingItem : StockCollection.getInstance().getRingItems()) {
                        for (RingOrderItem ringOrderItem: nextOrderItem.getRingOrderItemList()) {
                            if (nextRingItem.getName().equals(ringOrderItem.getRingName())) {
                                nextRingItem.setCount(ringOrderItem.getCount());

                                break;
                            }
                        }
                    }

                    break;
                }
            }
    }

    private void checkSaveButtonEnabled() {
        if (mOrderTitleName.length() != 0) {
            mSaveButton.setEnabled(true);
            mSaveButton.setBackgroundResource(R.color.colorPrimary);
        } else {
            mSaveButton.setEnabled(false);
            mSaveButton.setBackgroundResource(R.color.disabledColor);
        }
    }

    private OrderItem getCurrentEditOrderItemByTitle(String orderTitle) {
        OrderItem resultOrderItem = null;

        for (OrderItem nextOrderItem: mDayItem.getOrderItemList()) {
            if (nextOrderItem.getTitle().equals(orderTitle)) {
                resultOrderItem = nextOrderItem;
                break;
            }
        }

        return resultOrderItem;
    }

    private void clearRingCount() {
       for (RingItem nextRingItem : StockCollection.getInstance().getRingItems()) {
           nextRingItem.setCount(0);
       }
    }

    private Boolean hasOrderItemInCurrentDay(String orderItemTitle) {
        Boolean hasOrderItem = false;

        for (OrderItem nextOrderItem: mDayItem.getOrderItemList()) {
            if (nextOrderItem.getTitle().equals(orderItemTitle)) {
                hasOrderItem = true;
                break;
            }
        }

        return hasOrderItem;
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

    private void updateDayTitle() {
        if (mDayItem != null) {
            mDayTitle.setText(String.format("%d.%d.%d", mDayItem.getDay(), mDayItem.getMonth(), mDayItem.getYear()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAddOrderRingsViewAdapter = new AddOrderRingsViewAdapter(this, StockCollection.getInstance().getRingItems());
        mAddOrderRingsViewAdapter.setPlusClickListener(this);
        mAddOrderRingsViewAdapter.setMinusClickListener(this);
        recyclerRingsList.setAdapter(mAddOrderRingsViewAdapter);

        updateDayTitle();
    }

    @Override
    public void onMinusClick(View view, int position) {
        RingItem ringItem = mAddOrderRingsViewAdapter.getItem(position);

        int ringCount = ringItem.getCount();

        ringCount--;

        if (ringCount < 0) {
            ringCount = 0;
        }

        ringItem.setCount(ringCount);
        mAddOrderRingsViewAdapter.notifyItemChanged(position);
    }

    @Override
    public void onPlusClick(View view, int position) {
        RingItem ringItem = mAddOrderRingsViewAdapter.getItem(position);

        ringItem.setCount(ringItem.getCount() + 1);
        mAddOrderRingsViewAdapter.notifyItemChanged(position);
    }

    class SaveOrderStatus {
        public String orderTitleName;
        public String errMsg;
    }

    class SaveOrderItemTask extends AsyncTask<OrderItem, Void, SaveOrderStatus> {

        private ProgressDialog dialog;
        private Activity mParentActivity;
        private Boolean mIsEditOrderItem;

        public SaveOrderItemTask(AddOrderActivity activity, Boolean isEditOrderItem) {
            mParentActivity = activity;
            mIsEditOrderItem = isEditOrderItem;

            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage(getString(R.string.save_order_item_wait));
            dialog.show();
        }

        @Override
        protected SaveOrderStatus doInBackground(OrderItem... orderItems) {
            SaveOrderStatus orderStatus = new SaveOrderStatus();
            orderStatus.errMsg = "";
            orderStatus.orderTitleName = orderItems[0].getTitle();

            try {
                if (mDayItem.getOrderItemList().isEmpty()) {
                    mDayItem.getOrderItemList().add(orderItems[0]);
                    StockCollection.getInstance().getDayCollection().add(mDayItem);
                } else {
                    if (!mIsEditOrderItem) {
                        mDayItem.getOrderItemList().add(orderItems[0]);
                    }
                }

            } catch (Exception e) {
                orderStatus.errMsg = String.format(getString(R.string.save_order_item_err), e.getMessage());
            }

            return orderStatus;
        }

        @Override
        protected void onPostExecute(SaveOrderStatus orderStatus) {
            super.onPostExecute(orderStatus);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (!orderStatus.errMsg.isEmpty()) {
                Toast.makeText(mParentActivity, orderStatus.errMsg, Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra(ORDER_TITLE_PUT, orderStatus.orderTitleName);
                intent.putExtra(PUT_EDIT_ORDER_POSITION, editOrderPosition);
                setResult(RESULT_OK, intent);

                finish();
            }
        }
    }
}