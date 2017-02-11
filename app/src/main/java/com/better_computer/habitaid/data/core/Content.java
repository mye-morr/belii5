package com.better_computer.habitaid.data.core;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

import com.better_computer.habitaid.data.AbstractModel;

public class Content extends AbstractModel {

    private String playerid = "";
    private String playercat = "";
    private String playersubcat = "";
    private String content = "";
    private int wt = 0;

    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();

        contentValues.put("playerid", playerid);
        contentValues.put("playercat", playercat);
        contentValues.put("playersubcat", playersubcat);
        contentValues.put("content", content);
        contentValues.put("wt", wt);

        return contentValues;
    }

    @Override
    public void populateWith(Map<String, Object> data) {
        super.populateWith(data);

        playerid = fetchData(data, "playerid");
        playercat = fetchData(data, "playercat");
        playersubcat = fetchData(data, "playersubcat");
        content = fetchData(data, "content");
        wt = fetchDataInteger(data, "wt");
    }

    public String getPlayerid() {
        return playerid;
    }

    public void setPlayerid(String playerid) {
        this.playerid = playerid;
    }

    public String getPlayerCat() {
        return playercat;
    }

    public void setPlayerCat(String playercat) {
        this.playercat = playercat;
    }

    public String getPlayerSubcat() {
        return playersubcat;
    }

    public void setPlayerSubcat(String subcat) {
        this.playersubcat = subcat;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWeight() { return wt; }

    public void setWeight(int weight) { this.wt = weight; }

}
