package ru.ringsplus.app.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarUtils {

    public static Calendar getCalendarByDate(int year, int month, int day) {
        Calendar calendarDay = new GregorianCalendar();
        calendarDay.set(year, month - 1, day);
        return calendarDay;
    }

}
