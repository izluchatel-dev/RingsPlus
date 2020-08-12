package ru.ringsplus.app.utils;

import android.content.Intent;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.ringsplus.app.model.DayItem;

public class CalendarUtils {

    public static final String PUT_PARAM_DAY = "day";
    public static final String PUT_PARAM_MONTH = "month";
    public static final String PUT_PARAM_YEAR = "year";

    public static Calendar getCalendarByDate(int year, int month, int day) {
        Calendar calendarDay = new GregorianCalendar();
        calendarDay.set(year, month - 1, day);
        return calendarDay;
    }

    public static DayItem getDayItemFromIntent(Intent intent) {
        if (intent != null) {
            int year = intent.getIntExtra(PUT_PARAM_YEAR, 0);
            int month = intent.getIntExtra(PUT_PARAM_MONTH, 0);
            int day = intent.getIntExtra(PUT_PARAM_DAY, 0);

            if ((day != 0) && (month != 0) && (year != 0)) {
                return new DayItem(year, month, day);
            } else
                 return null;
        } else
            return null;
    }

}
