package com.better_computer.habitaid.data.core;

import android.content.Context;

import com.better_computer.habitaid.data.AbstractHelper;

public class ContentHelper extends AbstractHelper<Content> {

    public ContentHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_content";
        this.columns.add("playerid TEXT");
        this.columns.add("content TEXT");
        this.columns.add("weight REAL");
    }

    @Override
    protected Content getModelInstance() {
        return new Content();
    }

}