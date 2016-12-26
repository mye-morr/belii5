package com.better_computer.habitaid.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.Games;
import com.better_computer.habitaid.data.core.GamesHelper;
import com.better_computer.habitaid.util.StopwatchUtil;

import java.util.Calendar;

public class ButtonsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getStringExtra("CATEGORY_PRESSED").equalsIgnoreCase("status")) {
            String sPressed = intent.getStringExtra("STRING_PRESSED");
            String sGamesLastStatus = StopwatchUtil.getStopwatchLastStatus(context);
            if (!sPressed.equalsIgnoreCase(sGamesLastStatus)) {
                StopwatchUtil.setStopwatchStopTime(context, System.currentTimeMillis());

                long passedTime = StopwatchUtil.getStopwatchPassedTime(context);
                long passedSecs = passedTime / 1000;

                if(passedSecs < 60 * 60 * 3) { // 3 hours log limit
                    Games game = new Games();
                    game.setCat("status");
                    game.setSubcat("");
                    game.setContent(sGamesLastStatus);

                    int iPassedMin = -1 * ((int)passedSecs / 60);
                    if(sGamesLastStatus.equalsIgnoreCase("maint")) {
                        iPassedMin = -iPassedMin;
                    }

                    game.setPts(iPassedMin);
                    game.setTimestamp(Calendar.getInstance());

                    DatabaseHelper.getInstance().getHelper(GamesHelper.class).createOrUpdate(game);
                    StopwatchUtil.resetStopwatchStartTime(context, sPressed);
                }
                else {
                    StopwatchUtil.resetStopwatchStartTime(context, sPressed);
                }
            }
        }
        else {
            Games game = new Games();
            game.setCat(intent.getStringExtra("CATEGORY_PRESSED"));
            game.setSubcat("");
            game.setContent(intent.getStringExtra("STRING_PRESSED"));
            game.setPts(Integer.valueOf(intent.getStringExtra("POINTS_PRESSED")));
            game.setTimestamp(Calendar.getInstance());

            DatabaseHelper.getInstance().getHelper(GamesHelper.class).createOrUpdate(game);
        }
    }
}