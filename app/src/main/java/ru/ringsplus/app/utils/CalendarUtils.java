package ru.ringsplus.app.utils;

import android.content.Intent;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.ringsplus.app.model.DayItem;
import ru.ringsplus.app.model.StockCollection;

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
        DayItem resultDayItem = null;

        int year = intent.getIntExtra(PUT_PARAM_YEAR, 0);
        int month = intent.getIntExtra(PUT_PARAM_MONTH, 0);
        int day = intent.getIntExtra(PUT_PARAM_DAY, 0);

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

}
