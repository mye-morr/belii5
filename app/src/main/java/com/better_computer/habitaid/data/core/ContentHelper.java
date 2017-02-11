package com.better_computer.habitaid.data.core;

import android.content.Context;

import com.better_computer.habitaid.data.AbstractHelper;
import com.better_computer.habitaid.data.SearchEntry;

import java.util.ArrayList;
import java.util.List;

public class ContentHelper extends AbstractHelper<Content> {

    public ContentHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_content";
        this.columns.add("playerid TEXT");
        this.columns.add("playercat TEXT");
        this.columns.add("playersubcat TEXT");
        this.columns.add("content TEXT");
        this.columns.add("wt INTEGER");
    }

    @Override
    protected Content getModelInstance() {
        return new Content();
    }

    public boolean deleteByPlayerId(String playerid) {
        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "playerid", SearchEntry.Search.EQUAL, playerid));
        return delete(keys);
    }

}