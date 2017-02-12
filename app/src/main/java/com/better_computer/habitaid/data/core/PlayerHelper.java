package com.better_computer.habitaid.data.core;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.better_computer.habitaid.data.SearchEntry;

import java.util.ArrayList;
import java.util.List;

public class PlayerHelper extends NonSchedHelper {

    public PlayerHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_player";
        this.columns.add("wt INTEGER");
        this.columns.add("numrepeats INTEGER");
    }

    @Override
    protected Player getModelInstance() {
        return new Player();
    }

    public boolean createOrUpdateBySubcatAndName(Player player) {
        boolean result;
        String subcat = player.getSubcat();
        String name = player.getName();
        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcat", SearchEntry.Search.EQUAL, subcat));
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "name", SearchEntry.Search.EQUAL, name));
        Player dbPlayer = (Player) get(keys);
        if (dbPlayer == null) {
            String sNewId = java.util.UUID.randomUUID().toString();
            player.set_id(sNewId);
            player.setContent(player.getWt() + "|" + player.getContent());
            player.setWt(0);
            // create
            result = create(player);
            Log.i("DB", "Insert into " + "core_tbl_player" + ":" + player.get_id());
        } else {
            // update
            String mergedContent = dbPlayer.getContent() + "\n" + player.getWt() + "|" + player.getContent();
            dbPlayer.setContent(mergedContent);
            result = update(dbPlayer);
            Log.i("DB", "Upadte " + "core_tbl_player" + ":" + player.get_id());
        }
        return result;
    }

}