package com.better_computer.habitaid.data.core;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

import com.better_computer.habitaid.data.AbstractModel;

public class Content extends AbstractModel {

    private String playerid = "";
    private String content = "";
    private double weight = 0.0;

    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();

        contentValues.put("playerid", playerid);
        contentValues.put("content", content);
        contentValues.put("weight", weight);

        return contentValues;
    }

    @Override
    public void populateWith(Map<String, Object> data) {
        super.populateWith(data);

        playerid = fetchData(data, "playerid");
        content = fetchData(data, "content");
        weight = fetchDataDouble(data, "weight");
    }

    public String getPlayerid() {
        return playerid;
    }

    public void setPlayerid(String playerid) {
        this.playerid = playerid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getWeight() { return weight; }

    public void setWeight(double weight) { this.weight = weight; }

}
