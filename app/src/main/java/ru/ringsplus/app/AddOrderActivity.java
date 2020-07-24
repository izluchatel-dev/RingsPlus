package ru.ringsplus.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.firebase.FireBaseConnnection;
import ru.ringsplus.app.firebase.FireBaseOrderRings;
import ru.ringsplus.app.model.AppOptions;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.RingItem;
import ru.ringsplus.app.model.RingOrderItem;
import ru.ringsplus.app.model.StockCollection;
import ru.ringsplus.app.utils.DrawableUtils;

import static ru.ringsplus.app.OrderListActivity.PUT_EDIT_ORDER_POSITION;
import static ru.ringsplus.app.OrderListActivity.PUT_EDIT_ORDER_TITLE;
import static ru.ringsplus.app.utils.CalendarUtils.getDayItemFromIntent;

public class AddOrderActivity extends AppCompatActivity {

    public static final String ORDER_TITLE_PUT = "orderTitle";

    private RecyclerView recyclerRingsList;
    private EditText mOrderTitleName;
    private EditText mOrderDetails;
    private Button mSaveButton;

    private DayItem mDayItem;

    public String editOrderTitle = "";
    public Integer editOrderPosition = 0;

    private ProgressBar progressBar;

    FireBaseOrderRings fireBaseOrderRings;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mDayItem = getDayItemFromIntent(getIntent());

        DrawableUtils.updateDayTitle(mDayItem, findViewById(R.id.add_day_title));

        recyclerRingsList = findViewById(R.id.allRingsList);
        recyclerRingsList.setLayoutManager(new LinearLayoutManager(this));

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        FireBaseConnnection.setConnectedChecker(visible -> {
            if (visible) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerRingsList.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerRingsList.setVisibility(View.VISIBLE);
            }
        });

        fireBaseOrderRings = new FireBaseOrderRings(recyclerRingsList);

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

                        for (RingItem nextRingItem : fireBaseOrderRings.getRingItems()) {
                            if (nextRingItem.getCount() > 0) {
                                editOrderItem.getRingOrderItemList().add(new RingOrderItem(nextRingItem.getName(), nextRingItem.getCount()));
                            }
                        }

                        Intent intent = new Intent();
                        intent.putExtra(ORDER_TITLE_PUT, editOrderItem.getTitle());
                        intent.putExtra(PUT_EDIT_ORDER_POSITION, editOrderPosition);
                        setResult(RESULT_OK, intent);

                        finish();

                    }
                } else {
                    Toast.makeText(this, String.format(getString(R.string.add_order_item_exist_msg), orderTitle), Toast.LENGTH_SHORT).show();
                }
            } else { //Добавление
                if (!hasOrderItemInCurrentDay(orderTitle)) {
                    OrderItem addOrderItem = new OrderItem(orderTitle, orderDetails, orderAuthor);

                    for (RingItem nextRingItem : fireBaseOrderRings.getRingItems()) {
                        if (nextRingItem.getCount() > 0) {
                            addOrderItem.getRingOrderItemList().add(new RingOrderItem(nextRingItem.getName(), nextRingItem.getCount()));
                        }
                    }

                    if (mDayItem.getOrderItemList().isEmpty()) {
                        mDayItem.getOrderItemList().add(addOrderItem);

                        StockCollection.getInstance().getDayCollection().add(mDayItem);
                    } else {
                        mDayItem.getOrderItemList().add(addOrderItem);
                    }

                    Intent intent = new Intent();
                    intent.putExtra(ORDER_TITLE_PUT, addOrderItem.getTitle());
                    intent.putExtra(PUT_EDIT_ORDER_POSITION, editOrderPosition);
                    setResult(RESULT_OK, intent);

                    finish();
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

                    for (RingItem nextRingItem : fireBaseOrderRings.getRingItems()) {
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

}