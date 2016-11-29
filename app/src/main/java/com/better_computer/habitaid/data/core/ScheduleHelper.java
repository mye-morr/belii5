package com.better_computer.habitaid.data.core;

import android.content.Context;

import com.better_computer.habitaid.data.AbstractHelper;

public class ScheduleHelper extends AbstractHelper<Schedule>{

    public ScheduleHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_schedule";
        this.columns.add("category TEXT");
        this.columns.add("subcategory TEXT");
        this.columns.add("remind_interval TEXT");
        this.columns.add("repeat_enable TEXT");
        this.columns.add("repeat_type TEXT");
        this.columns.add("repeat_value TEXT");
        this.columns.add("prep_count TEXT");
        this.columns.add("prep_window TEXT");
        this.columns.add("prep_window_type TEXT");
        this.columns.add("repeat_inflexible TEXT");
        this.columns.add("next_due TEXT");
        this.columns.add("next_execute TEXT");
        this.columns.add("receiver TEXT");
        this.columns.add("receiverName TEXT");
        this.columns.add("message TEXT");
        this.columns.add("comtas TEXT");
        this.columns.add("notes TEXT");
    }

    @Override
    protected Schedule getModelInstance() {
        return new Schedule();
    }
}