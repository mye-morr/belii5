package com.better_computer.habitaid.data.core;

import android.content.ContentValues;

import java.util.Map;

public class Player extends NonSched {

    private String wt = "";
    private String extpct = "";
    private String extthr = "";

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

        wt = fetchData(data, "wt");
        extpct = fetchData(data, "extpct");
        extthr = fetchData(data, "extthr");
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

    public String getWt() { return wt; }

    public void setWt(String wt) { this.wt = wt; }

    public int getIntWt() {
        return Integer.parseInt(wt);
    }

    public String getExtpct() { return extpct; }

    public void setExtpct(String extpct) { this.extpct = extpct; }

    public double getDoubleExtpct() {
        return Double.parseDouble(extpct);
    }

    public String getExtthr() { return extthr; }

    public void setExtthr(String extthr) { this.extthr = extthr; }

    public double getDoubleExtthr() {
        return Double.parseDouble(extthr);
    }

}
