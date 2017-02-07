package com.better_computer.habitaid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.Content;
import com.better_computer.habitaid.data.core.ContentHelper;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.PlayerHelper;
import com.better_computer.habitaid.form.schedule.ContentListAdapter;
import com.better_computer.habitaid.form.schedule.NonSchedListAdapter;
import com.better_computer.habitaid.service.PlayerService;
import com.better_computer.habitaid.service.PlayerServiceStatic;
import com.better_computer.habitaid.util.PlayerTask;

import java.util.ArrayList;
import java.util.List;

public class FragmentNewPlayer extends AbstractBaseFragment {

    protected DatabaseHelper databaseHelper;
    protected PlayerHelper playerHelper;
    protected ContentHelper contentHelper;

    private ListView listViewContent;

    public FragmentNewPlayer() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_new_player, container, false);
        this.rootView = view;
        return view;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.databaseHelper = DatabaseHelper.getInstance();
        this.playerHelper = DatabaseHelper.getInstance().getHelper(PlayerHelper.class);
        this.contentHelper = DatabaseHelper.getInstance().getHelper(ContentHelper.class);

        final ListView listViewSubcat = ((ListView) rootView.findViewById(R.id.schedule_category_list));
        final ListView listViewItems = ((ListView) rootView.findViewById(R.id.schedule_subcategory_list));
        listViewContent = ((ListView) rootView.findViewById(R.id.schedule_new_player_list));

        listViewSubcat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sSubcat = listViewSubcat.getItemAtPosition(i).toString();
                ((MainActivity) context).sSelectedPlayerSubcat = sSubcat;

                refreshItemList();
            }
        });

        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final NonSched nsPlayer = (NonSched)listViewItems.getItemAtPosition(i);

                AlertDialog.Builder alertOptions = new AlertDialog.Builder(context);
                List<String> optsList = new ArrayList<String>();

                if(nsPlayer.get_state().equalsIgnoreCase("active")) {
                    optsList.add("Remove");
                }
                else {
                    optsList.add("Add");
                }

                final String[] options = optsList.toArray(new String[]{});
                alertOptions.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, options), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (options[i].equalsIgnoreCase("Add")) {
                            if (!"active".equals(nsPlayer.get_state())) {
                                nsPlayer.set_state("active");
                                playerHelper.update(nsPlayer);
                                String playerContent = nsPlayer.getContent();
                                String[] contentArray = playerContent.split("\n");
                                for (String strContent : contentArray) {
                                    Content content = new Content();
                                    String sNewId = java.util.UUID.randomUUID().toString();
                                    content.set_id(sNewId);
                                    content.set_state("active");
                                    content.setPlayerid(nsPlayer.get_id());
                                    content.setContent(strContent);
                                    content.setWeight(0.1);
                                    contentHelper.create(content);
                                }
                                refreshItemList();
                                refreshContentList();
                            }
                        } else if (options[i].equalsIgnoreCase("Remove")) {
                            if ("active".equals(nsPlayer.get_state())) {
                                nsPlayer.set_state("inactive");
                                playerHelper.update(nsPlayer);
                                contentHelper.deleteByPlayerId(nsPlayer.get_id());
                                refreshItemList();
                                refreshContentList();
                            }
                        }
                    }
                });

                alertOptions.setCancelable(true);
                alertOptions.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertOptions.show();

//                String arrayid = nsPlayer.get_id();
//                if (dynaArray.containsContributingArray(arrayid)) {
//                    dynaArray.removeContributingArray(arrayid);
//                } else {
//                    List<SearchEntry> keys = new ArrayList<SearchEntry>();
//                    keys.add(new SearchEntry(SearchEntry.Type.STRING, "playerid", SearchEntry.Search.EQUAL, nsPlayer.get_id()));
//                    List<Content> listContent = contentHelper.find(keys);
//                    dynaArray.addContributingArray(listContent, 1, arrayid, 0.6, 0.05);
//                }
//                listViewContent.setAdapter(new ContentListAdapter(context, dynaArray.currentInternalItemArray()));
            }
        });

//        listViewContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                final DynaArray.InternalItem item = (DynaArray.InternalItem) listViewContent.getItemAtPosition(i);
//                dynaArray.removeContributingArray(item.getArrayId());
//                listViewContent.setAdapter(new ContentListAdapter(context, dynaArray.currentInternalItemArray()));
//            }
//        });

        final Button btnStart = ((Button) rootView.findViewById(R.id.btnStart));
        btnStart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                List<Content> listContent = contentHelper.findAll();
                ((MainActivity) context).dynaArray.addContributingArray(listContent, 1, "a", 0.2, 0.2);

                PlayerService.startService(context, ((MainActivity) context).dynaArray, "SUPER");

//                objCurPlayerTask = new PlayerTask(context, dynaArray.currentStringArray(), "SUPER");
//                objCurPlayerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        final Button btnStop = ((Button) rootView.findViewById(R.id.btnStop));
        btnStop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                PlayerService.stopService(context);

                Toast.makeText(context, "thanks for playing", Toast.LENGTH_SHORT).show();
            }
        });

        refresh();
   }

    @Override
    public void refresh() {
        final ListView listViewSubcat = ((ListView) rootView.findViewById(R.id.schedule_category_list));

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
        refreshItemList();
        refreshContentList();
    }

    private void refreshItemList() {
        String sSubcat = ((MainActivity) context).sSelectedPlayerSubcat;
        if(!sSubcat.equalsIgnoreCase("")) {
            final ListView listViewItems = ((ListView) rootView.findViewById(R.id.schedule_subcategory_list));


            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcat", SearchEntry.Search.EQUAL, sSubcat));

            List<NonSched> listNonSched = (List<NonSched>) (List<?>) playerHelper.find(keys);
            listViewItems.setAdapter(new NonSchedListAdapter(context, listNonSched));
        }
    }

    private void refreshContentList() {
        List<Content> contents = contentHelper.findAll();
        listViewContent.setAdapter(new ContentListAdapter(context, contents));
    }
}