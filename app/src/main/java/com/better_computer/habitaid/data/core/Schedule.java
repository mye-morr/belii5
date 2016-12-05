package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import java.util.Calendar;
import java.util.Map;

import com.better_computer.habitaid.data.AbstractModel;

public class Schedule extends AbstractModel{
    private String category;
    private String subcategory;
    private String remindInterval = "5";
    private String repeatEnable = String.valueOf(false);
    private String repeatType;
    private String repeatValue = "";
    private String repeatInflexible = String.valueOf(true);
    private String prepCount = "0";
    private String prepWindow = "";
    private String prepWindowType;
    private Calendar nextDue = Calendar.getInstance();
    private Calendar nextExecute = Calendar.getInstance();
    private String receiver = "";
    private String receiverName = "";
    private String message = "";
    private String comTas = "";
    private String notes = "";

    // returns collection of contentValues from object
    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();
        contentValues.put("category", category);
        contentValues.put("subcategory", subcategory);
        contentValues.put("remind_interval", remindInterval);
        contentValues.put("repeat_enable", repeatEnable);
        contentValues.put("repeat_type", repeatType);
        contentValues.put("repeat_value", repeatValue);
        contentValues.put("repeat_inflexible", repeatInflexible);
        contentValues.put("prep_count", prepCount);
        contentValues.put("prep_window", prepWindow);
        contentValues.put("prep_window_type", prepWindowType);
        contentValues.put("next_due", dateTimeFormat.format(nextDue.getTime()));
        contentValues.put("next_execute", dateTimeFormat.format(nextExecute.getTime()));
        contentValues.put("receiver", receiver);
        contentValues.put("receiverName", receiverName);
        contentValues.put("message", message);
        contentValues.put("comtas", comTas);
        contentValues.put("notes", notes);
        return contentValues;
    }

    // populates object
    @Override
    public void populateWith(Map<String, Object> data) {
        super.populateWith(data);
        category = fetchData(data, "category");
        subcategory = fetchData(data, "subcategory");
        remindInterval = fetchData(data, "remind_interval", "3");
        repeatEnable = fetchData(data, "repeat_enable", String.valueOf(false));
        repeatType = fetchData(data, "repeat_type");
        repeatValue = fetchData(data, "repeat_value", "1");
        repeatInflexible = fetchData(data, "repeat_infexible", String.valueOf(true));
        prepCount = fetchData(data, "prep_count");
        prepWindow = fetchData(data, "prep_window");
        prepWindowType = fetchData(data, "prep_window_type");
        nextDue = fetchDataCalendar(data, "next_due");
        nextExecute = fetchDataCalendar(data, "next_execute");
        receiver = fetchData(data, "receiver");
        receiverName = fetchData(data, "receiverName");
        message = fetchData(data, "message");
        comTas = fetchData(data, "comtas");
        notes = fetchData(data, "notes");
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getRemindInterval() {
        return remindInterval;
    }

    public void setRemindInterval(String remindInterval) {
        this.remindInterval = remindInterval;
    }

    public String getPrepCount() {
        return prepCount;
    }

    public void setPrepCount(String prepCount) { this.prepCount = prepCount; }

    public String getPrepWindow() {
        return prepWindow;
    }

    public void setPrepWindow(String prepWindow) {
        this.prepWindow = prepWindow;
    }

    public String getPrepWindowType() {
        return prepWindowType;
    }

    public void setPrepWindowType(String prepWindowType) { this.prepWindowType = prepWindowType; }

    public String getRepeatEnable() {
        return repeatEnable;
    }

    public void setRepeatEnable(String repeatEnable) {
        this.repeatEnable = repeatEnable;
    }

    public Calendar getNextDue() {
        return nextDue;
    }

    public void setNextDue(Calendar nextDue) {
        this.nextDue = nextDue;
    }

    public int getRepeatTypeSelected(String[] types){
        for (int i = 0; i < types.length; i++) {
            if(types[i].equalsIgnoreCase(repeatType)){
                return i;
            }
        }
        return 0;
    }

    public int getPrepWindowTypeSelected(String[] types){
        for (int i = 0; i < types.length; i++) {
            if(types[i].equalsIgnoreCase(prepWindowType)){
                return i;
            }
        }
        return 0;
    }
    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public String getRepeatValue() {
        return repeatValue;
    }

    public void setRepeatValue(String repeatValue) {
        this.repeatValue = repeatValue;
    }

    public String getRepeatInflexible() {
        return repeatInflexible;
    }

    public void setRepeatInflexible(String repeatInflexible) { this.repeatInflexible = repeatInflexible; }

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

    public String getMessage(int limit){
        if(message.length() <= limit){
            return  message;
        }else {
            return message.substring(0, limit) + "...";
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComTas() {
        return comTas;
    }

    public void setComTas(String comTas) {
        this.comTas = comTas;
    }

    public void removeComTas(String comTas){
        this.comTas = this.comTas.replaceAll(comTas + ";", "");
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Calendar getNextExecute() {
        return nextExecute;
    }

    public void setNextExecute(Calendar nextExecute) {
        this.nextExecute = nextExecute;
    }
}