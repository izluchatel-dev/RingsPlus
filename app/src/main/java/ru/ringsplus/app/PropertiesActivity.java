package ru.ringsplus.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
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

    private Toolbar mToolbar;
    private Switch mAutoRefresh;
    private Switch mReceiveNotify;
    private Button mClearButton;

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

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAutoRefresh = findViewById(R.id.auto_refresh);
        mAutoRefresh.setChecked(AppOptions.getInstance().getAutoRefresh(this));
        mReceiveNotify = findViewById(R.id.receive_notify);
        mReceiveNotify.setChecked(AppOptions.getInstance().getReceiveNotify(this));

        mAutoRefresh.setOnCheckedChangeListener((compoundButton, b) -> AppOptions.getInstance().setAutoRefresh(compoundButton.getContext(), b));
        mReceiveNotify.setOnCheckedChangeListener((compoundButton, b) -> AppOptions.getInstance().setReceiveNotify(compoundButton.getContext(), b));

        mClearButton = findViewById(R.id.clearDataButton);
        mClearButton.setOnClickListener(view -> {
            ClearDataTask clearDataTaskTask = new ClearDataTask(this);
            clearDataTaskTask.execute();
        });
    }

    class ClearDataTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog dialog;
        private Activity mParentActivity;

        public ClearDataTask(PropertiesActivity activity) {
            mParentActivity = activity;
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage(getString(R.string.clear_progress_wait));
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String mStatusMsg = getString(R.string.clear_successfull);

            try {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

                List<DayItem> removedDayItems = new ArrayList<>();
                for (DayItem dayItem: StockCollection.getInstance().getDayCollection()) {
                    if ((dayItem.getYear() != currentYear) || (dayItem.getMonth() != currentMonth)) {
                        removedDayItems.add(dayItem);
                    }
                }
                StockCollection.getInstance().getDayCollection().removeAll(removedDayItems);
            } catch (Exception e) {
                mStatusMsg = String.format(getString(R.string.clear_item_err), e.getMessage());
            }
            return mStatusMsg;
        }

        @Override
        protected void onPostExecute(String statusMsg) {
            super.onPostExecute(statusMsg);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Toast.makeText(mParentActivity, statusMsg, Toast.LENGTH_SHORT).show();
        }
    }
}