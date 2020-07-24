package ru.ringsplus.app.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppOptions {

    private static AppOptions sAppOptions;

    private final String PREFERENCE_FILE_NAME = "RING_PREFERENCES";
    private final String USER_NAME = "USER_NAME";
    private final String RECEIVE_NOTIFY = "RECEIVE_NOTIFICATION";

    private AppOptions() {};

    public static AppOptions getInstance() {
        if (sAppOptions == null) {
            sAppOptions = new AppOptions();
        }

        return sAppOptions;
    }

    private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    }

    public String getUserName(Context context) {
        return getPreferences(context).getString(USER_NAME, "");
    }

    public void setUserName(Context context, String userName) {
        Editor editor = getPreferences(context).edit();
        try {
            editor.putString(USER_NAME, userName);
        } finally {
           editor.apply();
        }
    }

    public Boolean getReceiveNotify(Context context) {
        return getPreferences(context).getBoolean(RECEIVE_NOTIFY, true);
    }

    public void setReceiveNotify(Context context, Boolean receiveNotify) {
        Editor editor = getPreferences(context).edit();
        try {
            editor.putBoolean(RECEIVE_NOTIFY, receiveNotify);
        } finally {
            editor.apply();
        }
    }
}
