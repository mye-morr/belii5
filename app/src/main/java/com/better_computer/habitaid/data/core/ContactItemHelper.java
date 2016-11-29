package com.better_computer.habitaid.data.core;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.better_computer.habitaid.data.AbstractHelper;
import com.better_computer.habitaid.data.SearchEntry;

public class ContactItemHelper extends AbstractHelper<ContactItem>{

    public ContactItemHelper(Context context) {
        super(context);
        this.tableName = "core_tbl_contact_item";

        this.columns.add("name TEXT");
        this.columns.add("phone TEXT");
        this.columns.add("stfrq TEXT");
        this.columns.add("notes TEXT");
        this.columns.add("lastdat TEXT");
        this.columns.add("lastmsg TEXT");
    }

    @Override
    protected ContactItem getModelInstance() {
        return new ContactItem();
    }

    private boolean updated = false;

    public void fetchAndUpdate(){
        if(!updated) {
            ContactListUpdateAsync contactListUpdateAsync = new ContactListUpdateAsync();
            contactListUpdateAsync.execute();
            updated = true;
        }
    }

    @Override
    public List<ContactItem> find(List<SearchEntry> keys) {
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
        String sql = "SELECT * FROM " + this.tableName + whereClause + " ORDER BY name ASC";
        Log.i("DB", sql);
        Cursor cursor = database.rawQuery(sql, whereArgs.toArray(new String[whereArgs.size()]));

        List<ContactItem> models = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                ContactItem model = getModelInstance();
                model.populateWith(cursor, this.columns);
                models.add(model);
            } while (cursor.moveToNext());
        }
        //fix - android.database.CursorWindowAllocationException Start
        cursor.close();
        //fix - android.database.CursorWindowAllocationException End
        return models;
    }

    public class ContactListUpdateAsync extends AsyncTask<Integer, ContactItem, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    // get the phone number
                    Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        ContactItem contactItem = new ContactItem(name, phone); // , photoUri
                        contactItem.set_id(phone);
                        publishProgress(contactItem);
                    }
                    pCur.close();
                }
            }
            //fix - android.database.CursorWindowAllocationException Start
            cursor.close();
            //fix - android.database.CursorWindowAllocationException End
            return null;
        }

        @Override
        protected void onProgressUpdate(ContactItem... values) {
            ContactItem contactItem = values[0];
            if(getBy("_id", contactItem.get_id()) == null) {
                create(contactItem);
            }
        }
    }
}
