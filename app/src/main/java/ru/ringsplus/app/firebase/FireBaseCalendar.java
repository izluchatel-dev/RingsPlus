package ru.ringsplus.app.firebase;

import android.app.Activity;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.ringsplus.app.model.DayStatus;
import ru.ringsplus.app.utils.CalendarUtils;
import ru.ringsplus.app.utils.DrawableUtils;
import ru.ringsplus.app.utils.ProgressDialogWait;

public class FireBaseCalendar {

    public static final String FIREBASE_CALENDAR_PATH = "calendar";

    private DatabaseReference calendarReference;

    public FireBaseCalendar(CalendarView calendarView) {
        int month = calendarView.getCurrentPageDate().get(Calendar.MONTH) + 1;
        int year = calendarView.getCurrentPageDate().get(Calendar.YEAR);

        String pathMonthAndYear = String.valueOf(month) + String.valueOf(year);

        calendarReference = FirebaseDatabase.getInstance().getReference(FIREBASE_CALENDAR_PATH).child(pathMonthAndYear);

        calendarReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<EventDay> events = new ArrayList<>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String nextDayKey = postSnapshot.getKey();

                    if (nextDayKey != null) {
                        DataSnapshot statusDataSnapshot = postSnapshot.child("status");

                        DayStatus nextDayStatus = statusDataSnapshot.getValue(DayStatus.class);

                        int nextDay = Integer.parseInt(nextDayKey);

                        if (nextDayStatus != null) {
                            if (nextDayStatus == DayStatus.OpenDay) {
                                events.add(new EventDay(CalendarUtils.getCalendarByDate(year, month, nextDay),
                                        DrawableUtils.getHasIconWithText((calendarView.getContext()))));
                            } else if (nextDayStatus == DayStatus.CloseDay) {
                                events.add(new EventDay(CalendarUtils.getCalendarByDate(year, month, nextDay),
                                        DrawableUtils.getStopIconWithText((calendarView.getContext()))));
                            }
                        }
                    }
                }

                calendarView.setEvents(events);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(calendarView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public DatabaseReference getCalendarReference() {
        return calendarReference;
    }
}
