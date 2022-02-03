package com.ma.kissairaproject.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

public class Preferences {

    private static final int MODE_PRIVATE = 0;
    public static Location GLOBAL_LOCATION = new Location("");


    public static double getGlobalLocationLatitude() {
        if (GLOBAL_LOCATION == null || GLOBAL_LOCATION.getLatitude() == 0) {
            return 0;
        } else {
            return GLOBAL_LOCATION.getLatitude();
        }
    }

    public static double getGlobalLocationLongitude() {
        if (GLOBAL_LOCATION == null || GLOBAL_LOCATION.getLongitude() == 0) {
            return 0;
        } else {
            return GLOBAL_LOCATION.getLongitude();
        }
    }

    public static synchronized void setValue(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        prefs.edit().putString(key, value).commit();
    }

    public static synchronized String getValue(Context context, String key, String defaultValue) {

        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        return prefs.getString(key, defaultValue);
    }

    public static synchronized void setValue(Context context, String key, float value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        prefs.edit().putFloat(key, value).commit();
    }

    public static synchronized float getValue(Context context, String key, float defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        return prefs.getFloat(key, defaultValue);
    }

    public static synchronized void setValue(Context context, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        prefs.edit().putInt(key, value).commit();
    }

    public static synchronized int getValue(Context context, String key, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        return prefs.getInt(key, defaultValue);
    }

    public static synchronized void setValue(Context context, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        prefs.edit().putBoolean(key, value).commit();
    }

    public static synchronized boolean getValue(Context context, String key, boolean defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        return prefs.getBoolean(key, defaultValue);
    }

    public static synchronized void setValue(Context context, String key, long value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        prefs.edit().putLong(key, value).commit();
    }

    public static synchronized long getValue(Context context, String key, long defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        return prefs.getLong(key, defaultValue);
    }


    public static synchronized void setLong(Context context, String key, long value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        prefs.edit().putLong(key, value).commit();
    }

    public static synchronized long getLong(Context context, String key, long defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE);

        return prefs.getLong(key, defaultValue);
    }


}
