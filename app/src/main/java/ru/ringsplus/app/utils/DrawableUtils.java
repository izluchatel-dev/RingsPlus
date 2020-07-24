package ru.ringsplus.app.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarUtils;

import androidx.core.content.ContextCompat;
import ru.ringsplus.app.R;
import ru.ringsplus.app.model.DayItem;

public class DrawableUtils {

    public static final String STOP_SYMBOL = "!";
    public static final String HAS_SYMBOL = "+";

    public static Drawable getStopIconWithText(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.stop_icon);
        Drawable text = CalendarUtils.getDrawableText(context, STOP_SYMBOL, null, android.R.color.white, 12);

        Drawable[] layers = {background, text};
        return new LayerDrawable(layers);
    }

    public static Drawable getHasIconWithText(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.has_icon);
        Drawable text = CalendarUtils.getDrawableText(context, HAS_SYMBOL, null, android.R.color.white, 12);

        Drawable[] layers = {background, text};
        return new LayerDrawable(layers);
    }

    private DrawableUtils() {
    }

    public static void updateDayTitle(DayItem dayItem, TextView textView) {
        if (dayItem != null) {
            textView.setText(String.format("%d.%d.%d", dayItem.getDay(), dayItem.getMonth(), dayItem.getYear()));
        }
    }

}
