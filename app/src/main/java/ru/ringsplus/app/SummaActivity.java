package ru.ringsplus.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.OrderItem;
import ru.ringsplus.app.model.RingItem;
import ru.ringsplus.app.model.RingOrderItem;
import ru.ringsplus.app.model.StockCollection;

import static ru.ringsplus.app.OrderListActivity.PUT_PARAM_DAY;
import static ru.ringsplus.app.OrderListActivity.PUT_PARAM_MONTH;
import static ru.ringsplus.app.OrderListActivity.PUT_PARAM_YEAR;

public class SummaActivity extends AppCompatActivity {

    private SummaViewAdapter mSummaViewAdapter;
    private Toolbar mToolbar;
    private RecyclerView recyclerSumma;
    private TextView mDayTitle;

    private DayItem mDayItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_summa, menu);
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
        setContentView(R.layout.activity_summa);

        mDayItem = getDayItemFromIntent();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mDayTitle = findViewById(R.id.summa_day_title);

        recyclerSumma = findViewById(R.id.summaRingsList);
        recyclerSumma.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<RingItem> getAllOrderRings () {
        List<RingItem> resultOrderRings = new ArrayList<>(StockCollection.getInstance().getRingItems());

        for (RingItem nextRingItem : resultOrderRings) {
            nextRingItem.setCount(0);

            for (OrderItem nextOrder: mDayItem.getOrderItemList()) {
                for (RingOrderItem nextRingOrder: nextOrder.getRingOrderItemList()) {
                    if (nextRingItem.getName().equals(nextRingOrder.getRingName())) {
                        nextRingItem.setCount(nextRingItem.getCount() + nextRingOrder.getCount());
                    }
                }
            }
        }

        Iterator<RingItem> nextRingItem = resultOrderRings.iterator();
        while (nextRingItem.hasNext()) {
            RingItem checkRingItem = nextRingItem.next();

            if (checkRingItem.getCount() <= 0) {
                nextRingItem.remove();
            }
        }

        return resultOrderRings;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSummaViewAdapter = new SummaViewAdapter(this, getAllOrderRings());
        recyclerSumma.setAdapter(mSummaViewAdapter);

        updateDayTitle();
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
}