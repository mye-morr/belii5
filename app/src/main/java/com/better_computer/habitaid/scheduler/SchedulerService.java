package com.better_computer.habitaid.scheduler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.MessageHelper;

public class SchedulerService extends Service {

    private AlarmReceiver alarmReceiver;
    private long interval = 1000 * 45; //m/ every 45 seconds (in milliseconds)
    private MessageHelper messageHelper;

    public SchedulerService() {
        DatabaseHelper.init(this);
        this.messageHelper = DatabaseHelper.getInstance().getHelper(MessageHelper.class);
        this.alarmReceiver = new AlarmReceiver();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmReceiver.setAlarm(this, interval);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}