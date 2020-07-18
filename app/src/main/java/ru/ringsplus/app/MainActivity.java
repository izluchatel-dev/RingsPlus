package ru.ringsplus.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.DayStatus;
import ru.ringsplus.app.model.StockCollection;
import ru.ringsplus.app.utils.CalendarUtils;
import ru.ringsplus.app.utils.DrawableUtils;

import static ru.ringsplus.app.OrderListActivity.PUT_PARAM_DAY;
import static ru.ringsplus.app.OrderListActivity.PUT_PARAM_MONTH;
import static ru.ringsplus.app.OrderListActivity.PUT_PARAM_YEAR;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button detailButton;
    private Toolbar mToolbar;

    private static final int USER_PARAMS_REQUEST_ID = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_change_user) {
            Intent userIntent = new Intent(getBaseContext(), UserActivity.class);
            startActivityForResult(userIntent, USER_PARAMS_REQUEST_ID);
        } else if (item.getItemId() == R.id.action_refresh) {
            updateCalendarEvents();
        } else if (item.getItemId() == R.id.action_options) {
            Intent propertiesIntent = new Intent(getBaseContext(), PropertiesActivity.class);
            startActivity(propertiesIntent);
        } else if (item.getItemId() == R.id.action_stock) {
            Intent stockIntent = new Intent(getBaseContext(), StockActivity.class);
            startActivity(stockIntent);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);

        detailButton = (Button) findViewById(R.id.setDateButton);

        detailButton.setOnClickListener(v -> {
            int putDay = calendarView.getFirstSelectedDate().get(Calendar.DAY_OF_MONTH);
            int putMonth = calendarView.getFirstSelectedDate().get(Calendar.MONTH) + 1;
            int putYear = calendarView.getFirstSelectedDate().get(Calendar.YEAR);

            Intent orderListIntent = new Intent(getBaseContext(), OrderListActivity.class);
            orderListIntent.putExtra(PUT_PARAM_DAY, putDay);
            orderListIntent.putExtra(PUT_PARAM_MONTH, putMonth);
            orderListIntent.putExtra(PUT_PARAM_YEAR, putYear);
            startActivity(orderListIntent);
        });
    }

    public void updateCalendarEvents() {
        EventsTask eventsTask = new EventsTask(this);
        eventsTask.execute(this);
    }

    class EventsTask extends AsyncTask<Context, Void, String> {

        List<EventDay> events;
        private ProgressDialog dialog;
        private Activity mParentActivity;

        public EventsTask(MainActivity activity) {
            mParentActivity = activity;
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            events = new ArrayList<>();

            dialog.setMessage(getString(R.string.load_progress_wait));
            dialog.show();
        }

        @Override
        protected String doInBackground(Context... contexts) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String mStatusMsg = "";

            try {
                for (DayItem dayItem : StockCollection.getInstance().getDayCollection()) {

                    int year = dayItem.getYear();
                    int month = dayItem.getMonth();
                    int day = dayItem.getDay();

                    if (dayItem.getDayStatus().equals(DayStatus.OpenDay)) {
                        events.add(new EventDay(CalendarUtils.getCalendarByDate(year, month, day), DrawableUtils.getHasIconWithText((contexts[0]))));
                    } else if (dayItem.getDayStatus().equals(DayStatus.CloseDay)) {
                        events.add(new EventDay(CalendarUtils.getCalendarByDate(year, month, day), DrawableUtils.getStopIconWithText((contexts[0]))));
                    }
                }
            } catch (Exception e) {
                mStatusMsg = String.format(getString(R.string.get_data_err), e.getMessage());
            }

            return mStatusMsg;
        }

        @Override
        protected void onPostExecute(String statusMsg) {
            super.onPostExecute(statusMsg);

            calendarView.setEvents(events);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (!statusMsg.isEmpty()) {
                Toast.makeText(mParentActivity, statusMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        if ((requestCode == USER_PARAMS_REQUEST_ID) && (resultCode == RESULT_OK)) {
            String userName = data.getStringExtra(UserActivity.USER_NAME_PUT);

            if (!userName.isEmpty()) {
                String changeMessage = String.format(getString(R.string.change_user_name_fmt), userName);

                Toast.makeText(getApplicationContext(),changeMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateCalendarEvents();
    }
}