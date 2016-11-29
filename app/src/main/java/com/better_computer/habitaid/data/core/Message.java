package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import java.util.Calendar;
import java.util.Map;

import com.better_computer.habitaid.data.AbstractModel;

public class Message extends AbstractModel {
    private String scheduleId;
    private Calendar executed;
    private String receiver;
    private String receiverName;
    private String message;
    private String error;

    public Message() { }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();
        contentValues.put("schedule_id", scheduleId);
        if(executed != null) {
            contentValues.put("executed", dateTimeFormat.format(executed.getTime()));
        }

        contentValues.put("receiver", receiver);
        contentValues.put("receiverName", receiverName);
        contentValues.put("message", message);
        contentValues.put("error", error);
        return contentValues;
    }

    @Override
    public void populateWith(Map<String, Object> data) {
        super.populateWith(data);
        scheduleId = fetchData(data, "schedule_id");
        executed = fetchDataCalendar(data, "executed");
        receiver = fetchData(data, "receiver");
        receiverName = fetchData(data, "receiverName");
        message = fetchData(data, "message");
        error = fetchData(data, "error");
    }

    public String getReceiverString(int limit){
        String receiverStr  = getReceiverString();
        if(receiverStr.length() <= limit){
            return  receiverStr;
        }else {
            return receiverStr.substring(0, limit) + "...";
        }
    }

    public String getReceiverString(){
        return receiver;
    }

    public String getReceiverNameString(int limit){
        String receiverStr  = getReceiverNameString();
        if(receiverStr.length() <= limit){
            return  receiverStr;
        }else {
            return receiverStr.substring(0, limit) + "...";
        }
    }

    public String getReceiverNameString(){
        String sRet = receiverName;
        if (receiverName == null) {
            sRet = receiver;
        }
        return sRet;
    }

    public String getMessage(int limit){
        if(message.length() <= limit){
            return  message;
        }else {
            return message.substring(0, limit) + "...";
        }
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Calendar getExecuted() {
        return executed;
    }

    public void setExecuted(Calendar executed) {
        this.executed = executed;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
