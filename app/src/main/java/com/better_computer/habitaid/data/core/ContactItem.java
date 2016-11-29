package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import java.util.Map;

import com.better_computer.habitaid.data.AbstractModel;

public class ContactItem extends AbstractModel{
    private String name;
    private String phone;
    private String stfrq;
    private String notes;
    private String lastdat;
    private String lastmsg;

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("stfrq", stfrq);
        contentValues.put("notes", notes);
        contentValues.put("lastdat", lastdat);
        contentValues.put("lastmsg", lastmsg);

        return contentValues;
    }

    @Override
    public void populateWith(Map<String, Object> data) {
        super.populateWith(data);
        name = fetchData(data, "name");
        phone = fetchData(data, "phone");
        stfrq = fetchData(data, "stfrq");
        notes = fetchData(data, "notes");
        lastdat = fetchData(data, "lastdat");
        lastmsg = fetchData(data, "lastmsg");
    }

    public ContactItem() {
    }

    public ContactItem(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public ContactItem(String phone) {
        this.phone = phone;
    }

    public String getName(String defaultName) {

        if(name == null){
            return defaultName;
        }else {
            return name;
        }
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getStFrq() {
        return stfrq;
    }

    public void setStFrq(String stfrq) {
        this.stfrq = stfrq;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLastdat() {
        return lastdat;
    }

    public void setLastdat(String lastdat) {
        this.lastdat = lastdat;
    }

    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ContactItem) {
            ContactItem contactItem = (ContactItem)o;
            return this.phone.equals(contactItem.getPhone());
        }else{
            return false;
        }
    }
}
