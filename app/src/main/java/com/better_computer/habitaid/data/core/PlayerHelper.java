package com.better_computer.habitaid.data.core;

import android.content.Context;

public class PlayerHelper extends NonSchedHelper {

    public PlayerHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_player";
        this.columns.add("wt TEXT");
        this.columns.add("extpct TEXT");
        this.columns.add("extthr TEXT");
    }

    @Override
    protected Player getModelInstance() {
        return new Player();
    }

}