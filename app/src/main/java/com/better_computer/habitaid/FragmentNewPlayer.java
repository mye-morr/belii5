package com.better_computer.habitaid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.Content;
import com.better_computer.habitaid.data.core.ContentHelper;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.PlayerHelper;
import com.better_computer.habitaid.data.core.Schedule;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.form.NewWizardDialog;
import com.better_computer.habitaid.form.schedule.ContentListAdapter;
import com.better_computer.habitaid.form.schedule.NonSchedListAdapter;
import com.better_computer.habitaid.form.schedule.ScheduleListAdapter;
import com.better_computer.habitaid.util.DynaArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentNewPlayer extends Fragment {

    protected Context context;
    protected DatabaseHelper databaseHelper;
    protected PlayerHelper playerHelper;
    protected ContentHelper contentHelper;

    private DynaArray dynaArray = new DynaArray();

    public FragmentNewPlayer() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_schedule_new_player, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {

        this.context = getContext();
        this.databaseHelper = DatabaseHelper.getInstance();
        this.playerHelper = DatabaseHelper.getInstance().getHelper(PlayerHelper.class);
        this.contentHelper = DatabaseHelper.getInstance().getHelper(ContentHelper.class);

        final ListView listViewSubcat = ((ListView) rootView.findViewById(R.id.schedule_category_list));
        final ListView listViewItems = ((ListView) rootView.findViewById(R.id.schedule_subcategory_list));
        final ListView listViewContent = ((ListView) rootView.findViewById(R.id.schedule_new_player_list));

        SQLiteDatabase database = this.databaseHelper.getReadableDatabase();

        String sql = "SELECT DISTINCT subcat FROM core_tbl_player ORDER BY subcat";
        Cursor cursor = database.rawQuery(sql, new String[0]);

        List<String> listSubcat = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                listSubcat.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        //fix - android.database.CursorWindowAllocationException Start
        cursor.close();
        //fix - android.database.CursorWindowAllocationException End

        ArrayAdapter<String> adapterSubcat = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listSubcat);
        listViewSubcat.setAdapter(adapterSubcat);

        listViewSubcat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sSubcat = listViewSubcat.getItemAtPosition(i).toString();

                ((MainActivity) context).sSelectedPlayerSubcat = sSubcat;

                List<SearchEntry> keys = new ArrayList<SearchEntry>();
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcat", SearchEntry.Search.EQUAL, sSubcat));

                List<NonSched> listNonSched = (List<NonSched>) (List<?>) playerHelper.find(keys);
                listViewItems.setAdapter(new NonSchedListAdapter(context, listNonSched));
            }
        });

        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final NonSched nsPlayer = (NonSched)listViewItems.getItemAtPosition(i);

                String arrayid = nsPlayer.get_id();
                if (dynaArray.containsContributingArray(arrayid)) {
                    dynaArray.removeContributingArray(arrayid);
                } else {
                    List<SearchEntry> keys = new ArrayList<SearchEntry>();
                    keys.add(new SearchEntry(SearchEntry.Type.STRING, "playerid", SearchEntry.Search.EQUAL, nsPlayer.get_id()));
                    List<Content> listContent = contentHelper.find(keys);
                    dynaArray.addContributingArray(listContent, 1, arrayid, 0.6, 0.05);
                }
                listViewContent.setAdapter(new ContentListAdapter(context, dynaArray.currentInternalItemArray()));
            }
        });

        listViewContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final DynaArray.InternalItem item = (DynaArray.InternalItem) listViewContent.getItemAtPosition(i);
                dynaArray.removeContributingArray(item.getArrayId());
                listViewContent.setAdapter(new ContentListAdapter(context, dynaArray.currentInternalItemArray()));
            }
        });

   }

}