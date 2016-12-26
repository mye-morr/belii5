package com.better_computer.habitaid.data.core;

import android.content.Context;

import com.better_computer.habitaid.data.AbstractHelper;

public class GamesHelper extends AbstractHelper<Games>{

    public GamesHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_games";
        this.columns.add("timestamp TEXT");
        this.columns.add("cat TEXT");
        this.columns.add("subcat TEXT");
        this.columns.add("name TEXT");
        this.columns.add("content TEXT");
        this.columns.add("pts INTEGER");
    }

    @Override
    protected Games getModelInstance() {
        return new Games();
    }

}