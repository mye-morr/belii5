package com.better_computer.habitaid.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.Games;
import com.better_computer.habitaid.data.core.GamesHelper;

public class ButtonsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Games game = new Games();
        game.setCat(intent.getStringExtra("CATEGORY_PRESSED"));
        game.setSubcat("");
        game.setContent(intent.getStringExtra("STRING_PRESSED"));
        game.setPts(intent.getStringExtra("POINTS_PRESSED"));
        game.setTimestamp(Calendar.getInstance());

        DatabaseHelper.getInstance().getHelper(GamesHelper.class).createOrUpdate(game);
    }

}
