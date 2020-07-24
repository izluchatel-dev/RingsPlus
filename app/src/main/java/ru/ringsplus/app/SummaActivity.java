package ru.ringsplus.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ringsplus.app.firebase.FireBaseConnnection;
import ru.ringsplus.app.firebase.FireBaseRingsSumma;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.utils.DrawableUtils;

import static ru.ringsplus.app.utils.CalendarUtils.getDayItemFromIntent;

public class SummaActivity extends AppCompatActivity {

    private RecyclerView recyclerSumma;

    private DayItem mDayItem;

    private ProgressBar progressBar;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mDayItem = getDayItemFromIntent(getIntent());
        DrawableUtils.updateDayTitle(mDayItem, findViewById(R.id.summa_day_title));

        recyclerSumma = findViewById(R.id.summaRingsList);
        recyclerSumma.setLayoutManager(new LinearLayoutManager(this));

        FireBaseConnnection.setConnectedChecker(visible -> {
            if (visible) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerSumma.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerSumma.setVisibility(View.VISIBLE);
            }
        });

        FireBaseRingsSumma fireBaseRingsSumma = new FireBaseRingsSumma(recyclerSumma, mDayItem);
    }

}