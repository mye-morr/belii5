package com.better_computer.habitaid.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHelper <T extends AbstractModel> {

    protected String tableName;
    protected List<String> columns;
    protected Context context;
    protected DatabaseHelper databaseHelper;

    protected AbstractHelper(Context context) {
        this.context = context;
        this.databaseHelper = DatabaseHelper.getInstance();
        columns = new ArrayList<String>();
        columns.add("_id VARCHAR(100)");
        columns.add("_frame VARCHAR(20)");
        columns.add("_state VARCHAR(20)");
    }

    protected abstract T getModelInstance();

    public AbstractModel populateRelations(AbstractModel abstractModel){
        return abstractModel;
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder sqlBulder = new StringBuilder();
        sqlBulder.append("CREATE TABLE ").append(tableName).append("(");
        for (int colIndex = 0; colIndex < columns.size(); colIndex++){
            sqlBulder.append(columns.get(colIndex));
            if(colIndex < columns.size() - 1){
                sqlBulder.append(",");
            }
        }
        sqlBulder.append(")");
        Log.i("DB", sqlBulder.toString());
        sqLiteDatabase.execSQL(sqlBulder.toString());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        String sql = "DROP TABLE IF EXISTS " + tableName;
        Log.i("DB", sql);
        db.execSQL(sql);
        onCreate(db);
    }

    public boolean create(T model) {
        SQLiteDatabase database = this.databaseHelper.getWritableDatabase();
        ContentValues contentValues = model.getContentValues();
        if(contentValues.getAsString("_id") == null){
            contentValues.put("_id", java.util.UUID.randomUUID().toString());
        }
        if(contentValues.getAsString("_state") == null){
            contentValues.put("_state", "idle");
        }

        Log.i("DB", "Insert into " + tableName + ":" + contentValues.getAsString("_id"));
        Boolean output = database.insert(this.tableName, null, contentValues) > 0;
        return output;
    }

    public boolean update(List<SearchEntry> keys, T model){
        SQLiteDatabase database = this.databaseHelper.getWritableDatabase();
        String whereClause = "";
        List<String> whereArgs = new ArrayList<String>();
        for (int i = 0; i < keys.size(); i++) {
            SearchEntry searchEntry = keys.get(i);
            if(i > 0){
                whereClause += " AND ";
            }
            whereClause += searchEntry.toString();
            if(searchEntry.getValue() instanceof List){
                whereArgs.addAll((List)searchEntry.getValue());
            }else {
                whereArgs.add(searchEntry.getValue().toString());
            }
        }
        ContentValues contentValues = model.getContentValues();

        Log.i("DB", "Update " + tableName + ":" + whereClause);
        Boolean output = database.update(this.tableName, contentValues, whereClause, whereArgs.toArray(new String[whereArgs.size()])) > 0;
        return output;
    }

    public boolean delete(String id){
        List<SearchEntry> keys =  new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "_id", SearchEntry.Search.EQUAL, id));
        return delete(keys);
    }

    public boolean delete(List<SearchEntry> keys){
        SQLiteDatabase database = this.databaseHelper.getWritableDatabase();
        String whereClause = "";
        List<String> whereArgs = new ArrayList<String>();
        for (int i = 0; i < keys.size(); i++) {
            SearchEntry searchEntry = keys.get(i);
            if(i > 0){
                whereClause += " AND ";
            }
            whereClause += searchEntry.toString();
            if(searchEntry.getValue() instanceof List){
                whereArgs.addAll((List)searchEntry.getValue());
            }else {
                whereArgs.add(searchEntry.getValue().toString());
            }
        }
        Log.i("DB", "Delete " + tableName + ":" + whereClause);
        Boolean output = database.delete(this.tableName, whereClause, whereArgs.toArray(new String[whereArgs.size()])) > 0;
        return output;
    }

    public List<T> findAll(){
        List<SearchEntry> searchEntries = new ArrayList<SearchEntry>();
        return find(searchEntries);
    }

    public List<T> find(List<SearchEntry> keys){
        SQLiteDatabase database = this.databaseHelper.getReadableDatabase();
        String whereClause = "";
        List<String> whereArgs = new ArrayList<String>();
        for (int i = 0; i < keys.size(); i++) {
            SearchEntry searchEntry = keys.get(i);
            if(i > 0){
                whereClause += " AND ";
            }else if(i == 0 ){
                whereClause += " WHERE ";
            }
            whereClause += searchEntry.toString();
            if(searchEntry.getValue() instanceof List){
                whereArgs.addAll((List)searchEntry.getValue());
            }else {
                whereArgs.add(searchEntry.getValue().toString());
            }
        }
        String sql = "SELECT * FROM " + this.tableName + whereClause;
        Log.i("DB", sql);
        Cursor cursor = database.rawQuery(sql, whereArgs.toArray(new String[whereArgs.size()]));

        List<T> models = new ArrayList<T>();
        if(cursor.moveToFirst()){
            do {
                T model = getModelInstance();
                model.populateWith(cursor, this.columns);
                models.add(model);
            } while (cursor.moveToNext());
        }
        //fix - android.database.CursorWindowAllocationException Start

        //fix - android.database.CursorWindowAllocationException End
        return models;
    }

    public List<T> find(List<SearchEntry> keys, String sOrderBy){
        SQLiteDatabase database = this.databaseHelper.getReadableDatabase();
        String whereClause = "";
        List<String> whereArgs = new ArrayList<String>();
        for (int i = 0; i < keys.size(); i++) {
            SearchEntry searchEntry = keys.get(i);
            if(i > 0){
                whereClause += " AND ";
            }else if(i == 0 ){
                whereClause += " WHERE ";
            }
            whereClause += searchEntry.toString();
            if(searchEntry.getValue() instanceof List){
                whereArgs.addAll((List)searchEntry.getValue());
            }else {
                whereArgs.add(searchEntry.getValue().toString());
            }
        }
        String sql = "SELECT * FROM " + this.tableName + whereClause + " " + sOrderBy;
        Log.i("DB", sql);
        Cursor cursor = database.rawQuery(sql, whereArgs.toArray(new String[whereArgs.size()]));

        List<T> models = new ArrayList<T>();
        if(cursor.moveToFirst()){
            do {
                T model = getModelInstance();
                model.populateWith(cursor, this.columns);
                models.add(model);
            } while (cursor.moveToNext());
        }
        //fix - android.database.CursorWindowAllocationException Start

        //fix - android.database.CursorWindowAllocationException End
        return models;
    }

    // single record using SearchEntry key
    public T get(List<SearchEntry> keys){
        SQLiteDatabase database = this.databaseHelper.getReadableDatabase();
        String whereClause = "";
        List<String> whereArgs = new ArrayList<String>();
        for (int i = 0; i < keys.size(); i++) {
            SearchEntry searchEntry = keys.get(i);
            if(i > 0){
                whereClause += " AND ";
            }
            whereClause += searchEntry.toString();
            if(searchEntry.getValue() instanceof List){
                whereArgs.addAll((List)searchEntry.getValue());
            }else {
                whereArgs.add(String.valueOf(searchEntry.getValue()));
            }
        }
        String sql = "SELECT * FROM " + this.tableName + " WHERE " + whereClause;
        Log.i("DB", sql);
        Cursor cursor = database.rawQuery(sql, whereArgs.toArray(new String[whereArgs.size()]));

        T model = null;
        if(cursor.moveToFirst()){
            model = getModelInstance();
            model.populateWith(cursor, columns);
        }

        return model;
    }

    public List<T> findBy(String name, int value){
        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.NUMBER, name, SearchEntry.Search.EQUAL, String.valueOf(value)));
        return this.find(keys);
    }

    public List<T> findBy(String name, String value){
        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, name, SearchEntry.Search.EQUAL, String.valueOf(value)));
        return this.find(keys);
    }

    public T getBy(String name, int value){
        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.NUMBER, name, SearchEntry.Search.EQUAL, String.valueOf(value)));
        return this.get(keys);
    }

    public T getBy(String name, String value){
        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, name, SearchEntry.Search.EQUAL, String.valueOf(value)));
        return this.get(keys);
    }

    public boolean update(T model) {
        ArrayList<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "_id", SearchEntry.Search.EQUAL, model.get_id()));
        if(get(keys) != null){
            return update(keys, model);
        }else{
            return false;
        }
    }

    public boolean createOrUpdate(T model) {
        ArrayList<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "_id", SearchEntry.Search.EQUAL, model.get_id()));
        if(get(keys) == null){
            return create(model);
        }else{
            return update(keys, model);
        }
    }
}
