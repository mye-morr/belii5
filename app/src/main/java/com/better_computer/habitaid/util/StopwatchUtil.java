package com.better_computer.habitaid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StopwatchUtil {

    private static final String STOPWATCH_START_TIME = "StopwatchUtil_STOPWATCH_START_TIME";
    private static final String STOPWATCH_START_STATUS = "StopwatchUtil_STOPWATCH_START_STATUS";
    private static final String STOPWATCH_STOP_TIME = "StopwatchUtil_STOPWATCH_STOP_TIME";

    public static long resetStopwatchStartTime(Context context) {
        long time = System.currentTimeMillis();
        setStopwatchStartTime(context, time);
        setStopwatchStopTime(context, -1);
        return time;
    }

    public static long resetStopwatchStartTime(Context context, String desc) {
        long time = System.currentTimeMillis();
        setStopwatchStartTime(context, time, desc);
        setStopwatchStopTime(context, -1);
        return time;
    }

    private static void setStopwatchStartTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(STOPWATCH_START_TIME, period).apply();
    }

    private static void setStopwatchStartTime(Context context, long period, String desc) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit()
                .putLong(STOPWATCH_START_TIME, period)
                .putString(STOPWATCH_START_STATUS, desc)
                .apply();
    }

    private static long getStopwatchStartTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(STOPWATCH_START_TIME, -1);
    }

    public static void setStopwatchStopTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(STOPWATCH_STOP_TIME, period).apply();
    }

    public static long getStopwatchStopTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(STOPWATCH_STOP_TIME, -1);
    }

    public static String getStopwatchLastStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(STOPWATCH_START_STATUS, "");
    }

    public static long getStopwatchPassedTime(Context context) {
        long startTime = getStopwatchStartTime(context);
        long stopTime = getStopwatchStopTime(context);
        if (startTime < 0 && stopTime < 0) {
            // init condition
            long current = System.currentTimeMillis();
            setStopwatchStartTime(context, current);
            setStopwatchStopTime(context, current);
            return 0;
        } else if (stopTime < 0) {
            // still running
            long current = System.currentTimeMillis();
            return current - getStopwatchStartTime(context);
        } else {
            return stopTime - getStopwatchStartTime(context);
        }
    }

}
