package com.better_computer.habitaid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.better_computer.habitaid.data.core.ContactItemHelper;
import com.better_computer.habitaid.data.core.ContentHelper;
import com.better_computer.habitaid.data.core.GamesHelper;
import com.better_computer.habitaid.data.core.MessageHelper;
import com.better_computer.habitaid.data.core.PlayerHelper;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.data.core.ScheduleHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String dbName = "schedule_sms_db";
    static DatabaseHelper databaseHelper;
    private List<AbstractHelper> modelHelpers;

    public static void init(Context context){
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(context);

            // this creates new tables through onCreate (AbstractHelper)
            databaseHelper.modelHelpers.add(new MessageHelper(context));
            databaseHelper.modelHelpers.add(new ScheduleHelper(context));
            databaseHelper.modelHelpers.add(new NonSchedHelper(context));
            databaseHelper.modelHelpers.add(new ContactItemHelper(context));
            databaseHelper.modelHelpers.add(new GamesHelper(context));
            databaseHelper.modelHelpers.add(new PlayerHelper(context));
            databaseHelper.modelHelpers.add(new ContentHelper(context));
        }
    }

    public static DatabaseHelper getInstance(){
        return databaseHelper;
    }

    public <T> T getHelper(Class<T> type){
        for (int i = 0; i < modelHelpers.size(); i++) {
            if(modelHelpers.get(i).getClass().equals(type)){
                return (T)modelHelpers.get(i);
            }
        }
        return null;
    }

    private DatabaseHelper(Context context) {
        super(context, dbName, null, 2);
        modelHelpers = new ArrayList<AbstractHelper>();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (int i = 0; i < modelHelpers.size(); i++) {
            modelHelpers.get(i).onCreate(sqLiteDatabase);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        for (int i = 0; i < modelHelpers.size(); i++) {
            modelHelpers.get(i).onUpgrade(sqLiteDatabase, oldVer, newVer);
        }
    }
}
