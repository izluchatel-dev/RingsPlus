package ru.ringsplus.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ru.ringsplus.app.firebase.FireBaseClearOrders;
import ru.ringsplus.app.model.AppOptions;

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

        Switch showArchiveItems = findViewById(R.id.show_archive_items);
        showArchiveItems.setChecked(AppOptions.getInstance().getShowArchiveItems(this));
        showArchiveItems.setOnCheckedChangeListener((compoundButton, b) -> AppOptions.getInstance().setShowArchiveItems(compoundButton.getContext(), b));

        Button clearButton = findViewById(R.id.clearDataButton);
        clearButton.setOnClickListener(view -> {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(getString(R.string.clear_question));

            dialogBuilder.setPositiveButton(R.string.item_dialog_yes, (dialog, which) -> {
                FireBaseClearOrders fireBaseClearOrders = new FireBaseClearOrders(this);
            });

            dialogBuilder.setNegativeButton(R.string.item_dialog_cancel, (dialog, which) -> dialog.cancel());

            dialogBuilder.show();
        });
    }
}