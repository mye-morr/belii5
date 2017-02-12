package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import java.util.Map;

public class Player extends NonSched {

    private int wt = 0;
    private int numrepeats = 0;

    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();

        contentValues.put("wt", wt);
        contentValues.put("numrepeats", numrepeats);

        return contentValues;
    }

    @Override
    public void populateWith(Map<String, Object> data) {
        super.populateWith(data);

        wt = fetchDataInteger(data, "wt");
        numrepeats = fetchDataInteger(data, "numrepeats");
    }

    public void copyFromNonSched(NonSched input) {
        setCat("player");
        setSubcat(input.getSubcat());
        setSubsub(input.getSubsub());
        setIprio(input.getIprio());
        setName(input.getName());
        setAbbrev(input.getAbbrev());
        setContent(input.getContent());
        setNotes(input.getNotes());
    }

    public int getWt() { return wt; }

    public void setWt(int wt) { this.wt = wt; }

    public int getNumRepeats() { return numrepeats; }

    public void setNumRepeats(int numrepeats) { this.numrepeats = numrepeats; }
}