package com.better_computer.habitaid.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.better_computer.habitaid.util.PlayerTaskStatic;

public class PlayerServiceStatic extends Service {

    private static final String LOG_TAG = PlayerServiceStatic.class.getSimpleName();

    private static final String KEY_ITEMS = "KEY_ITEMS";
    private static final String KEY_RATE = "KEY_RATE";

    protected volatile PlayerTaskStatic objCurPlayerTask;

    public static void startService(Context context, String[] sxItems, String sRate) {
        Intent intent = new Intent(context, PlayerServiceStatic.class);
        intent.putExtra(KEY_ITEMS, sxItems);
        intent.putExtra(KEY_RATE, sRate);
        context.startService(intent);
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, PlayerServiceStatic.class);
        context.stopService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand()");

        // task is running, stop it and create a new one
        stopTaskIfRunning();

        String[] sxItems = intent.getStringArrayExtra(KEY_ITEMS);
        String sRate = intent.getStringExtra(KEY_RATE);
        objCurPlayerTask = new PlayerTaskStatic(this, sxItems, sRate);
        objCurPlayerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy()");
        stopTaskIfRunning();
    }

    private void stopTaskIfRunning() {
        if (objCurPlayerTask != null) {
            Log.d(LOG_TAG, "Stop running task : " + objCurPlayerTask);
            objCurPlayerTask.cancel(true);
            objCurPlayerTask = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
