package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import java.util.Map;

import com.better_computer.habitaid.data.AbstractModel;

// to store flashcards, games, comTas

public class NonSched extends AbstractModel{
    private String cat = "";
    private String subcat = "";
    private String subsub = "";
    private int iprio = 0;
    private String name = "";
    private String abbrev = "";
    private String content = "";
    private String notes = "";

    public ContentValues getContentValues() {

        ContentValues contentValues = super.getContentValues();

        contentValues.put("cat", cat);
        contentValues.put("subcat", subcat);
        contentValues.put("subsub", subsub);
        contentValues.put("iprio", iprio);
        contentValues.put("name", name);
        contentValues.put("abbrev", abbrev);
        contentValues.put("content", content);
        contentValues.put("notes", notes);

        return contentValues;
    }

    @Override
    public void populateWith(Map<String, Object> data) {
        super.populateWith(data);

        cat = fetchData(data, "cat");
        subcat = fetchData(data, "subcat");
        subsub = fetchData(data, "subsub");
        iprio = fetchDataInteger(data, "iprio");
        name = fetchData(data, "name");
        abbrev = fetchData(data, "abbrev");
        content = fetchData(data, "content");
        notes = fetchData(data, "notes");
    }

    public String getCat() { return cat; }

    public void setCat(String cat) { this.cat = cat; }

    public String getSubcat() { return subcat; }

    public void setSubcat(String subcat) { this.subcat = subcat; }

    public String getSubsub() { return subsub; }

    public void setSubsub(String subsub) { this.subsub = subsub; }

    public int getIprio() { return iprio; }

    public void setIprio(int iprio) { this.iprio = iprio; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getAbbrev() { return abbrev; }

    public void setAbbrev(String abbrev) { this.abbrev = abbrev; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }
}
