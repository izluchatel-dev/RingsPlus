package ru.ringsplus.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ru.ringsplus.app.model.AppOptions;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.StockCollection;

public class PropertiesActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_properties, menu);
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
        setContentView(R.layout.activity_properties);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Switch receiveNotify = findViewById(R.id.receive_notify);
        receiveNotify.setChecked(AppOptions.getInstance().getReceiveNotify(this));

        receiveNotify.setOnCheckedChangeListener((compoundButton, b) -> AppOptions.getInstance().setReceiveNotify(compoundButton.getContext(), b));

        Button clearButton = findViewById(R.id.clearDataButton);
        clearButton.setOnClickListener(view -> {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

            List<DayItem> removedDayItems = new ArrayList<>();
            for (DayItem dayItem: StockCollection.getInstance().getDayCollection()) {
                if ((dayItem.getYear() != currentYear) || (dayItem.getMonth() != currentMonth)) {
                    removedDayItems.add(dayItem);
                }
            }

            StockCollection.getInstance().getDayCollection().removeAll(removedDayItems);

            Toast.makeText(this, getString(R.string.clear_successfull), Toast.LENGTH_SHORT).show();
        });
    }
}