package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import java.util.Map;

public class Player extends NonSched {

    private int wt = 0;
    private double extpct = 0.0;
    private double extthr = 0.0;

    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();

        contentValues.put("wt", wt);
        contentValues.put("extpct", extpct);
        contentValues.put("extthr", extthr);

        return contentValues;
    }

    @Override
    public void populateWith(Map<String, Object> data) {
        super.populateWith(data);

        wt = fetchDataInteger(data, "wt");
        extpct = fetchDataDouble(data, "extpct");
        extthr = fetchDataDouble(data, "extthr");
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

    public double getExtpct() { return extpct; }

    public void setExtpct(double extpct) { this.extpct = extpct; }

    public double getExtthr() { return extthr; }

    public void setExtthr(double extthr) { this.extthr = extthr; }

}