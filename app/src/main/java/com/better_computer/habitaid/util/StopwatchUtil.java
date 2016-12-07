package com.better_computer.habitaid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by tedwei on 12/7/16.
 */

public class StopwatchUtil {

    private static final String STOPWATCH_START_TIME = "StopwatchUtil_STOPWATCH_START_TIME";
    private static final String STOPWATCH_STOP_TIME = "StopwatchUtil_STOPWATCH_STOP_TIME";

    public static long resetStopwatchStartTime(Context context) {
        long time = System.currentTimeMillis();
        setStopwatchStartTime(context, time);
        setStopwatchStopTime(context, -1);
        return time;
    }

    public static void setStopwatchStartTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(STOPWATCH_START_TIME, period).commit();
    }

    public static long getStopwatchStartTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long time = sp.getLong(STOPWATCH_START_TIME, -1);
        return time;
    }

    public static void setStopwatchStopTime(Context context, long period) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(STOPWATCH_STOP_TIME, period).commit();
    }

    public static long getStopwatchStopTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long time = sp.getLong(STOPWATCH_STOP_TIME, -1);
        return time;
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
