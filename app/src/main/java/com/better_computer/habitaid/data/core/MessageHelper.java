package com.better_computer.habitaid.data.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.better_computer.habitaid.data.AbstractHelper;
import com.better_computer.habitaid.data.DatabaseHelper;

public class MessageHelper extends AbstractHelper<Message>{

    public MessageHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_message";

        this.columns.add("schedule_id VARCHAR(100)");
        this.columns.add("executed TEXT");
        this.columns.add("receiverName TEXT");
        this.columns.add("receiver TEXT");
        this.columns.add("message TEXT");
        this.columns.add("error TEXT");
        this.columns.add("delivered TEXT");
    }

    @Override
    protected Message getModelInstance() {
        return new Message();
    }

    public void initMessages(Calendar calendar){
        List<Schedule> schedules = (List<Schedule>)(List<?>)DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).findBy("_state", "active");
        Calendar calWorking = Calendar.getInstance();

        // to limit # broadcasts / alarm cycle
        boolean bSentFirstSelfMsg = false;

        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            Calendar nextExecute = schedule.getNextExecute();

            if (nextExecute.getTimeInMillis() <= calendar.getTimeInMillis()) {
                if(schedule.getCategory().equals("contacts")) {
                    // ie now is ahead of when we planned to execute next
                        createMessageFromSchedule(schedule);
                        schedule.set_state("completed");
                }

                else {
                    int remindInterval = Integer.parseInt(schedule.getRemindInterval());

                    // send only one broadcast per alarm cycle
                    if (!bSentFirstSelfMsg) {
                        Intent i2 = new Intent();
                        i2.setAction("com.example.SendBroadcast");
                        i2.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        i2.putExtra("STRING_MSG", schedule.getMessage());
                        context.sendBroadcast(i2);

                        calWorking.add(Calendar.MINUTE, remindInterval);
                        schedule.setNextExecute(calWorking);

                        bSentFirstSelfMsg = true;
                    }
                }

                //fix - Multiple duplication of schedules Start
                DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).update(schedule);
                //fix - Multiple duplication of schedules End
            }
        }
    }

    public void checkStatus(Calendar calNow) {

        Calendar calBuf = Calendar.getInstance(); // for calculations

        // only process where frame=_inactive
        List<Schedule> schedules = (List<Schedule>) (List<?>) DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).findBy("_frame", "inactive");

        boolean bChanged = false;
        for (int i = 0; i < schedules.size(); i++) {
            Schedule sched = schedules.get(i);
            bChanged = false;

            Calendar calNextDue = sched.getNextDue();
            if (calNextDue.getTimeInMillis() <= calNow.getTimeInMillis()) { // past-due
                bChanged = true;

                String sRepeatType = sched.getRepeatType();
                int iRepeatValue = Integer.valueOf(sched.getRepeatValue());
                boolean isInflexible = Boolean.parseBoolean(sched.getRepeatInflexible());

                if (isInflexible) {
                    while (calNextDue.getTimeInMillis() <= calNow.getTimeInMillis()) {

                        if (sRepeatType.equalsIgnoreCase("Minutes")) {
                            calNextDue.add(Calendar.MINUTE, iRepeatValue);
                        } else if (sRepeatType.equalsIgnoreCase("Days")) {
                            calNextDue.add(Calendar.DATE, iRepeatValue);
                        } else if (sRepeatType.equalsIgnoreCase("Weeks")) {
                            calNextDue.add(Calendar.DATE, iRepeatValue * 7);
                        } else if (sRepeatType.equalsIgnoreCase("Months")) {
                            calNextDue.add(Calendar.MONTH, iRepeatValue);
                        }
                    }

                    sched.setNextDue(calNextDue);
                }

                else {
                    calBuf = (Calendar) calNow.clone();

                    if (sRepeatType.equalsIgnoreCase("Minutes")) {
                        calBuf.add(Calendar.MINUTE, iRepeatValue);
                    } else if (sRepeatType.equalsIgnoreCase("Days")) {
                        calBuf.add(Calendar.DATE, iRepeatValue);
                    } else if (sRepeatType.equalsIgnoreCase("Weeks")) {
                        calBuf.add(Calendar.DATE, iRepeatValue * 7);
                    } else if (sRepeatType.equalsIgnoreCase("Months")) {
                        calBuf.add(Calendar.MONTH, iRepeatValue);
                    }

                    sched.setNextDue(calBuf);
                }

                sched.setPrepCount("0");
            }

            long lWindowMillis = 0L;
            int iWindow = Integer.valueOf(sched.getPrepWindow());

            String sWindowType = sched.getPrepWindowType();

            if (sWindowType.equalsIgnoreCase("Minutes")) {
                lWindowMillis = 1L * iWindow * 1000 * 60;
            } else if (sWindowType.equalsIgnoreCase("Hours")) {
                lWindowMillis = 1L * iWindow * 1000 * 60 * 60;
            } else if (sWindowType.equalsIgnoreCase("Days")) {
                lWindowMillis = 1L * iWindow * 1000 * 60 * 60 * 24;
            } else if (sWindowType.equalsIgnoreCase("Weeks")) {
                lWindowMillis = 1L * iWindow * 1000 * 60 * 60 * 24 * 7;
            } else if (sWindowType.equalsIgnoreCase("Months")) {
                lWindowMillis = 1L * iWindow * 1000 * 60 * 60 * 24 * 30;
            }

            long lDiff = 0L;
            int iPrepRemain = 2 - Integer.valueOf(sched.getPrepCount());

            if (iPrepRemain == 0) {
                bChanged = true;

                // if 2 preps done, it's no longer
                // in scope of checkStatus function
                sched.set_frame("completed");
                sched.set_state("active");
            }

            else {
                lDiff = sched.getNextDue().getTimeInMillis()
                        - calNow.getTimeInMillis();

                double dRemainWindows = lDiff / lWindowMillis;

                Random rand = new Random();
                int iAdded = 0;

                // 10% time added for good measure
                if (dRemainWindows < 2.1) {
                    bChanged = true;

                    boolean bWakingHours = false;
                    int iNumTriesRandom = 0; // in-case we reach an impossible case :-\

                    int iHourOfDay = 0;
                    while(!bWakingHours && iNumTriesRandom < 50) {

                        calBuf = (Calendar) calNow.clone();

                        if (iPrepRemain == 2) {
                            // Random.nextInt sucks
                            if (lDiff >= 120000) {
                                iAdded = rand.nextInt((int) (lDiff / 120000)); // half-time
                            } else {
                                iAdded = 0;
                            }
                        } else { // iPrepRemain == 1
                            if (lDiff >= 60000) {
                                iAdded = rand.nextInt((int) (lDiff / 60000));
                            } else {
                                iAdded = 0;
                            }
                        }

                        calBuf.add(Calendar.MINUTE, iAdded);

                        iHourOfDay = calBuf.get(Calendar.HOUR_OF_DAY);
                        if(iHourOfDay > 9 && iHourOfDay < 13) {
                            bWakingHours = true;
                        }
                        else {
                            iNumTriesRandom++;
                        }
                    }

                    iNumTriesRandom = 0;
                    while(!bWakingHours && iNumTriesRandom < 50) {

                        calBuf = (Calendar) calNow.clone();

                        if (iPrepRemain == 2) {
                            // Random.nextInt sucks
                            if (lDiff >= 120000) {
                                iAdded = rand.nextInt((int) (lDiff / 120000)); // half-time
                            } else {
                                iAdded = 0;
                            }
                        } else { // iPrepRemain == 1
                            if (lDiff >= 60000) {
                                iAdded = rand.nextInt((int) (lDiff / 60000));
                            } else {
                                iAdded = 0;
                            }
                        }

                        calBuf.add(Calendar.MINUTE, iAdded);

                        iHourOfDay = calBuf.get(Calendar.HOUR_OF_DAY);
                        if(iHourOfDay > 13 && iHourOfDay < 20) {
                            bWakingHours = true;
                        }
                        else {
                            iNumTriesRandom++;
                        }
                    }

                    if(!bWakingHours) {
                        iHourOfDay = calBuf.get(Calendar.HOUR_OF_DAY);

                        if(iHourOfDay > 20) { // better next day (morning)
                            calBuf.add(Calendar.DAY_OF_MONTH, 1);
                        }

                        calBuf.set(Calendar.HOUR_OF_DAY, 9);
                        calBuf.add(Calendar.MINUTE, rand.nextInt(180));
                    }

                    sched.setNextExecute(calBuf);

                    sched.set_state("active");
                    sched.set_frame("active");
                } // end if dRemainWindows < 2.1
            } // end if iPrepRemain > 0

            if(bChanged) { // don't waste update operations
                DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).update(sched);
            }
        }
    }

    public void deleteFromHistory(Message message) {
        delete(message.get_id());
    }

    public void removeHistoryMessages(){
        List<Message> messages = (List<Message>)(List<?>)findBy("_state", "delivered");

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Calendar limitTime = Calendar.getInstance();
        String val = settings.getString("keep_history_till", "M-1");
        String[] valArray = val.split("-");
        int valInt = Integer.parseInt(valArray[1]);
        if(valArray[0].equalsIgnoreCase("D")){
            limitTime.add(Calendar.DATE, valInt);
        }else if(valArray[0].equalsIgnoreCase("M")){
            limitTime.add(Calendar.MONTH, valInt);
        }else if(valArray[0].equalsIgnoreCase("Y")){
            limitTime.add(Calendar.YEAR, valInt);
        }
        for (int i = 0; i < messages.size(); i++) {
            if(messages.get(i).getExecuted().getTimeInMillis() > limitTime.getTimeInMillis()){
                delete(messages.get(i).get_id());
            }
        }
    }

    // READY is the key-word
    // everything that qualifies is inserted into MESSAGE TABLE
    // and purged later by the removeHistoryMessage function
    private void createMessageFromSchedule(Schedule schedule){
        Message message = new Message();
        message.set_state("ready");
        message.setScheduleId(schedule.get_id());
        message.setMessage(schedule.getMessage());
        message.setReceiver(schedule.getReceiver());
        create(message);
    }

    public List<Message> getMessagesFromQueue(){
        List<Message> messages = (List<Message>)(List<?>)findBy("_state", "ready");
        return messages;
    }

    public void markAsSent(Message message){
        message.set_state("sent");
        //fix - Multiple duplication of schedules Start
        this.update(message);
        //fix - Multiple duplication of schedules End
    }

    public void markAsFailed(Message message, String error){
        message.set_state("failed");
        message.setError(error);
        //fix - Multiple duplication of schedules Start
        this.update(message);
        //fix - Multiple duplication of schedules End
    }

    public void markAsDelivered(Message message){
        message.set_state("delivered");
        //fix - Multiple duplication of schedules Start
        this.update(message);
        //fix - Multiple duplication of schedules End
    }

    public void markAsSending(Message message){
        message.set_state("sending");
        message.setExecuted(Calendar.getInstance());
        //fix - Multiple duplication of schedules Start
        this.update(message);
        //fix - Multiple duplication of schedules End
    }
}