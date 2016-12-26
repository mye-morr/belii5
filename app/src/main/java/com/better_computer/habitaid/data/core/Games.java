package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import com.better_computer.habitaid.data.AbstractModel;

import java.util.Calendar;
import java.util.Map;

public class Games extends AbstractModel{

    private Calendar timestamp = Calendar.getInstance();
    private String cat = "";
    private String subcat = "";
    private String name = "";
    private String content = "";
    private int pts = 0;

    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();

        contentValues.put("timestamp", dateTimeFormat.format(timestamp.getTime()));
        contentValues.put("cat", cat);
        contentValues.put("subcat", subcat);
        contentValues.put("name", name);
        contentValues.put("content", content);
        contentValues.put("pts", pts);

        return contentValues;
    }

    @Override
    public void populateWith(Map<String, Object> data) {
        super.populateWith(data);

        timestamp = fetchDataCalendar(data, "timestamp");
        cat = fetchData(data, "cat");
        subcat = fetchData(data, "subcat");
        name = fetchData(data, "name");
        content = fetchData(data, "content");
        pts = fetchDataInteger(data, "pts");
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public String getCat() { return cat; }

    public void setCat(String cat) { this.cat = cat; }

    public String getSubcat() {
        return subcat;
    }

    public void setSubcat(String subcat) {
        this.subcat = subcat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPts() {
        return pts;
    }

    public void setPts(int pts) { this.pts = pts; }

}
