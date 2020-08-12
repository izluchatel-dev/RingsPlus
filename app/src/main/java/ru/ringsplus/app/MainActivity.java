package ru.ringsplus.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ru.ringsplus.app.firebase.FireBaseCalendar;
import ru.ringsplus.app.firebase.FireBaseConnnection;
import ru.ringsplus.app.model.AppOptions;
import ru.ringsplus.app.model.DayItem;

import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_DAY;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_MONTH;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_YEAR;
import static ru.ringsplus.app.utils.CalendarUtils.getDayItemFromIntent;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;

    private FireBaseCalendar mFireBaseCalendar;

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

        calendarView = findViewById(R.id.calendarView);

        calendarView.setOnPreviousPageChangeListener(() -> {
            mFireBaseCalendar.getCalendarReference().onDisconnect();
            FireBaseConnnection.setConnectedChecker(this,true);
            mFireBaseCalendar = new FireBaseCalendar(calendarView);
        });

        calendarView.setOnForwardPageChangeListener(() -> {
            mFireBaseCalendar.getCalendarReference().onDisconnect();
            FireBaseConnnection.setConnectedChecker(this, true);
            mFireBaseCalendar = new FireBaseCalendar(calendarView);
        });

        Button detailButton = findViewById(R.id.setDateButton);
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

        FireBaseConnnection.setConnectedChecker(this, false);
        mFireBaseCalendar = new FireBaseCalendar(calendarView);

        checkNotifyClick();
    }

    private void checkNotifyClick() {
        DayItem mDayItem = getDayItemFromIntent(getIntent());

        if (mDayItem != null) {
            Intent orderListIntent = new Intent(getBaseContext(), OrderListActivity.class);
            orderListIntent.putExtra(PUT_PARAM_DAY, mDayItem.getDay());
            orderListIntent.putExtra(PUT_PARAM_MONTH, mDayItem.getMonth());
            orderListIntent.putExtra(PUT_PARAM_YEAR, mDayItem.getYear());
            startActivity(orderListIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppOptions.getInstance().checkReceiveNotify(this);
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