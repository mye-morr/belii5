package com.better_computer.habitaid.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            Intent i1 = new Intent(context, SchedulerService.class);
            context.startService(i1);

            /* if we wanted to have a parallel service
            Intent i2 = new Intent(context, SchedulerServiceDaily.class);
            context.startService(i2);
            */
    }
}