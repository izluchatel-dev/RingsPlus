package ru.ringsplus.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.RingItem;
import ru.ringsplus.app.model.StockCollection;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AddOrderActivity extends AppCompatActivity implements AddOrderRingsViewAdapter.MinusClickListener, AddOrderRingsViewAdapter.PlusClickListener {

    public static final String PUT_PARAM_DAY = "day";
    public static final String PUT_PARAM_MONTH = "month";
    public static final String PUT_PARAM_YEAR = "year";

    private Toolbar mToolbar;
    private TextView mDayTitle;
    private AddOrderRingsViewAdapter mAddOrderRingsViewAdapter;
    private RecyclerView recyclerRingsList;

    private DayItem mDayItem;

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
}