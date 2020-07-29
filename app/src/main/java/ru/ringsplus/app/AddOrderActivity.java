package ru.ringsplus.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.firebase.FireBaseConnnection;
import ru.ringsplus.app.firebase.FireBaseOrdersEditor;
import ru.ringsplus.app.model.AppOptions;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.RingItem;
import ru.ringsplus.app.model.RingOrderItem;
import ru.ringsplus.app.utils.DrawableUtils;

import static ru.ringsplus.app.OrderListActivity.PUT_EDIT_ORDER_ID;
import static ru.ringsplus.app.utils.CalendarUtils.getDayItemFromIntent;

public class AddOrderActivity extends AppCompatActivity {

    public static final String ORDER_TITLE_PUT = "orderTitle";

    private RecyclerView recyclerRingsList;
    private EditText mOrderTitleName;
    private EditText mOrderDetails;
    private Button mSaveButton;

    private DayItem mDayItem;

    FireBaseOrdersEditor mFireBaseOrdersEditor;

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

            OrderItem editOrderItem = mFireBaseOrdersEditor.getEditOrderItem();

            if (editOrderItem != null) {
                editOrderItem.setTitle(orderTitle);
                editOrderItem.setDetails(orderDetails);
                editOrderItem.setAuthor(orderAuthor);

                if (editOrderItem.getRingOrderItemList() == null) {
                    editOrderItem.setRingOrderItemList(new ArrayList<>());
                }
                editOrderItem.getRingOrderItemList().clear();
            } else {
                editOrderItem = new OrderItem(UUID.randomUUID().toString(), orderTitle, orderDetails, orderAuthor);
            }

            if (editOrderItem != null) {
                if (mFireBaseOrdersEditor.getRingItems() != null) {
                    for (RingItem nextRingItem : mFireBaseOrdersEditor.getRingItems()) {
                        if (nextRingItem.getCount() > 0) {
                            editOrderItem.getRingOrderItemList().add(new RingOrderItem(nextRingItem.getName(), nextRingItem.getCount()));
                        }
                    }
                }

                mFireBaseOrdersEditor.updateOrderItem(this, editOrderItem);

                finish();
            }
        });

        FireBaseConnnection.setConnectedChecker(this, true);

        String editOrderId = getIntent().getStringExtra(PUT_EDIT_ORDER_ID);
        mFireBaseOrdersEditor = new FireBaseOrdersEditor(recyclerRingsList, editOrderId, mDayItem,
                this::setEditOrderItem);
    }

    private void setEditOrderItem(OrderItem orderItem) {
        mOrderTitleName.setText(orderItem.getTitle());
        mOrderDetails.setText(orderItem.getDetails());
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
}