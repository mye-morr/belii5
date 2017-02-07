package com.better_computer.habitaid.data.core;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

import com.better_computer.habitaid.data.AbstractModel;

public class Content extends AbstractModel implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.playerid);
        dest.writeString(this.content);
        dest.writeDouble(this.weight);
    }

    public Content() {
    }

    protected Content(Parcel in) {
        this.playerid = in.readString();
        this.content = in.readString();
        this.weight = in.readDouble();
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel source) {
            return new Content(source);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };
}
