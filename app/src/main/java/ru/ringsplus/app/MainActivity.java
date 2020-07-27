package ru.ringsplus.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ru.ringsplus.app.firebase.FireBaseConnnection;
import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.DayStatus;
import ru.ringsplus.app.model.StockCollection;
import ru.ringsplus.app.utils.CalendarUtils;
import ru.ringsplus.app.utils.DrawableUtils;

import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_DAY;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_MONTH;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_YEAR;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button detailButton;

    private ProgressBar progressBar;

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
        } else if (item.getItemId() == R.id.action_options) {
            Intent propertiesIntent = new Intent(getBaseContext(), PropertiesActivity.class);
            startActivity(propertiesIntent);
        } else if (item.getItemId() == R.id.action_stock) {
            Intent stockIntent = new Intent(getBaseContext(), RingsActivity.class);
            startActivity(stockIntent);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar =  findViewById(R.id.progress_bar);

        calendarView = findViewById(R.id.calendarView);

        calendarView.setOnPreviousPageChangeListener(() -> {
            FireBaseConnnection.setConnectedChecker(this::onShowProgressBar, true);
        });

        calendarView.setOnForwardPageChangeListener(() -> {
            FireBaseConnnection.setConnectedChecker(this::onShowProgressBar, true);
        });

        detailButton = findViewById(R.id.setDateButton);
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

        FireBaseConnnection.setConnectedChecker(this::onShowProgressBar, false);
    }

    private void onShowProgressBar(Boolean visible) {
        if (visible) {
            progressBar.setVisibility(View.VISIBLE);
            calendarView.setVisibility(View.GONE);
            detailButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            calendarView.setVisibility(View.VISIBLE);
            detailButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateCalendarEvents();
    }

    public void updateCalendarEvents() {
        List<EventDay> events = new ArrayList<>();

        for (DayItem dayItem : StockCollection.getInstance().getDayCollection()) {

            int year = dayItem.getYear();
            int month = dayItem.getMonth();
            int day = dayItem.getDay();

            if (dayItem.getDayStatus().equals(DayStatus.OpenDay)) {
                events.add(new EventDay(CalendarUtils.getCalendarByDate(year, month, day), DrawableUtils.getHasIconWithText((this))));
            } else if (dayItem.getDayStatus().equals(DayStatus.CloseDay)) {
                events.add(new EventDay(CalendarUtils.getCalendarByDate(year, month, day), DrawableUtils.getStopIconWithText((this))));
            }
        }

        calendarView.setEvents(events);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        if ((requestCode == USER_PARAMS_REQUEST_ID) && (resultCode == RESULT_OK)) {
            String userName = data.getStringExtra(UserActivity.USER_NAME_PUT);

            if ((userName != null) && (!userName.isEmpty())) {
                String changeMessage = String.format(getString(R.string.change_user_name_fmt), userName);

                Toast.makeText(getApplicationContext(),changeMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}