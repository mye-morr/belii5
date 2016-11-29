package com.better_computer.habitaid.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.telephony.SmsManager;

import java.util.Calendar;
import java.util.List;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.Message;
import com.better_computer.habitaid.data.core.MessageHelper;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String SMS_SENT = "mm.belii3.SMS_SENT";
    public static final String SMS_DELIVERED = "mm.belii3.SMS_DELIVERED";

    private int notificationId = 001;
    private static final int NOTIFY_TYPE_OK = 0;
    private static final int NOTIFY_TYPE_FAILED = 1;

    public AlarmReceiver() { }

    @Override
    //m/ ALWAYS RUNNING!!!
    public void onReceive(final Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        DatabaseHelper.init(context);
        final MessageHelper messageHelper = DatabaseHelper.getInstance().getHelper(MessageHelper.class);

        // checks frame=_inactive to see which projects
        // are now within their preparation window
        // and/or are overdue and should be repeated
        messageHelper.checkStatus(Calendar.getInstance());

        //m/ puts messsages into message table as 'ready'
        messageHelper.initMessages(Calendar.getInstance());

        // don't waste processing on something that could be
        // done manually in history view by clicking a button
        //messageHelper.removeHistoryMessages();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sendBulkMessage(context, messageHelper, wl);
                return null;
            }
        }.execute();
    }

    private void sendBulkMessage(Context context, MessageHelper messageHelper, PowerManager.WakeLock wl) {
        //m/ returns only the 'ready' messages
        List<Message> messages = messageHelper.getMessagesFromQueue();
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            sendMessage(context, message);
            messageHelper.markAsSending(message);
        }

        wl.release();
    }

    //m/ this is the meat and potatoes
    public void sendMessage(Context context, final Message message){
        String sReceiver = message.getReceiverString();
        //Sent
        Intent sentPiIntent = new Intent(SMS_SENT);
        sentPiIntent.putExtra("message_id", message.get_id());
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentPiIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //delivered
        Intent deliverPiIntent = new Intent(SMS_DELIVERED);
        deliverPiIntent.putExtra("message_id", message.get_id());
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, deliverPiIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //send sms
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(sReceiver, null, message.getMessage(), sentPI, deliveredPI);

        //update the sms history
        Uri uri = Uri.parse("content://sms/sent/");
        ContentValues values = new ContentValues();
        values.put("address", message.getReceiverString());
        values.put("body", message.getMessage());
        context.getContentResolver().insert(uri, values);
    }

    public void setAlarm(SchedulerService schedulerService, long interval){
        AlarmManager alarmManager = (AlarmManager) schedulerService.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(schedulerService, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(schedulerService, 0, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), interval, alarmIntent);
    }

    public void cancelAlarm(SchedulerService schedulerService){
        Intent intent = new Intent(schedulerService, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(schedulerService, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) schedulerService.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
    }

    /* might want this later for mainstream users, ideally only show alert for undelivered messages
    private void showNotification(Context context, Message message, String title, String text, int type) {
        //We get a reference to the NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon((type == NOTIFY_TYPE_OK)? R.drawable.notification_check: R.drawable.notification_cross)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentText(text);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("message_id", message.get_id());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId++ , mBuilder.build());

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        if((settings.getBoolean("notifications_new_message_vibrate", true))) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }
    }
    */

}