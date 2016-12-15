package com.better_computer.habitaid.data.core;

import android.content.Context;

import com.better_computer.habitaid.data.AbstractHelper;
import com.better_computer.habitaid.data.SearchEntry;

import java.util.ArrayList;
import java.util.List;

public class NonSchedHelper extends AbstractHelper<NonSched>{

    public NonSchedHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_nonsched";
        this.columns.add("cat TEXT");
        this.columns.add("subcat TEXT");
        this.columns.add("subsub TEXT");
        this.columns.add("iprio TEXT");
        this.columns.add("name TEXT");
        this.columns.add("abbrev TEXT");
        this.columns.add("content TEXT");
        this.columns.add("notes TEXT");
    }

    @Override
    protected NonSched getModelInstance() {
        return new NonSched();
    }

    public boolean createAndShift(NonSched model) {
        reorder(model.getCat(), 1);
        model.setIprio("0");
        return super.create(model);
    }

    private void reorder(String cat, int start) {
        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, cat));
        List<NonSched> list = find(keys, "ORDER BY iprio");
        for (int i = 0 ; i < list.size() ; i++) {
            NonSched nonSched = list.get(i);
            int newIprio = i + start;
            nonSched.setIprio("" + newIprio);
            update(nonSched);
        }
    }
}