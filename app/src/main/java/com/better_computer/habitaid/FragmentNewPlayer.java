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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.Content;
import com.better_computer.habitaid.data.core.ContentHelper;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.data.core.Player;
import com.better_computer.habitaid.data.core.PlayerHelper;
import com.better_computer.habitaid.data.core.Schedule;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.form.schedule.ContentListAdapter;
import com.better_computer.habitaid.form.schedule.NonSchedListAdapter;
import com.better_computer.habitaid.player.ContentExtPickerFragment;
import com.better_computer.habitaid.player.PlayerNamePickerFragment;
import com.better_computer.habitaid.service.PlayerService;
import com.better_computer.habitaid.service.PlayerServiceStatic;
import com.better_computer.habitaid.util.DynaArray;
import com.better_computer.habitaid.util.PlayerTask;

import java.util.ArrayList;
import java.util.Calendar;
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
                final Player nsPlayer = (Player)listViewItems.getItemAtPosition(i);

                AlertDialog.Builder alertOptions = new AlertDialog.Builder(context);
                List<String> optsList = new ArrayList<String>();

                if(nsPlayer.get_state().equalsIgnoreCase("active")) {
                    optsList.add("Remove");
                }
                else {
                    optsList.add("Add");
                }

                optsList.add("Delete");

                final String[] options = optsList.toArray(new String[]{});
                alertOptions.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, options), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (options[i].equalsIgnoreCase("DELETE")) {
                            contentHelper.deleteByPlayerId(nsPlayer.get_id());
                            playerHelper.delete(nsPlayer.get_id());
                            refreshItemList();
                            refreshContentList();
                        } else if (options[i].equalsIgnoreCase("ADD")) {

                            ContentExtPickerFragment fragment = ContentExtPickerFragment.newInstance();
                            fragment.setListener(new ContentExtPickerFragment.Listener() {
                                @Override
                                public void onValueSet(int wt, double extpct, double extthr) {
                                    nsPlayer.setWt(wt);
                                    nsPlayer.setExtpct(extpct);
                                    nsPlayer.setExtthr(extthr);

                                    String playerContent = nsPlayer.getContent();
                                    String[] contentArray = playerContent.split("\n");
                                    for (String strContent : contentArray) {
                                        Content content = new Content();
                                        String sNewId = java.util.UUID.randomUUID().toString();
                                        content.set_id(sNewId);
                                        content.set_state("active");
                                        content.setPlayerid(nsPlayer.get_id());
                                        content.setPlayerCat(nsPlayer.getSubcat());
                                        content.setPlayerSubcat(nsPlayer.getName());

                                        int iBufPipe = 0;
                                        iBufPipe = strContent.indexOf("|");
                                        content.setWeight(Integer.parseInt(strContent.substring(0,iBufPipe).trim()));
                                        content.setContent(strContent.substring(iBufPipe+1).trim());

                                        contentHelper.create(content);
                                    }
                                    nsPlayer.set_state("active");
                                    playerHelper.update(nsPlayer);

                                    ((MainActivity) context).dynaArray.addContributingArray(
                                            contentHelper.findBy("playerid",nsPlayer.get_id()),
                                            nsPlayer.getName(), wt, extpct, extthr);

                                    refreshItemList();
                                    refreshContentList();
                                }
                            });
                            fragment.show(((MainActivity)context).getSupportFragmentManager(), null);

                        } else if (options[i].equalsIgnoreCase("REMOVE")) {
                            nsPlayer.set_state("inactive");
                            playerHelper.update(nsPlayer);
                            contentHelper.deleteByPlayerId(nsPlayer.get_id());

                            ((MainActivity) context).dynaArray.removeContributingArray(
                                    nsPlayer.get_id());

                            refreshItemList();
                            refreshContentList();
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
            }
        });

        final Button btnStart = ((Button) rootView.findViewById(R.id.btnStart));
        btnStart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                AlertDialog.Builder inputInterval = new AlertDialog.Builder(context);
                inputInterval.setTitle("Interval");
                inputInterval.setMessage("Minutes; varia");
                final EditText input = new EditText(context);
                input.setText("3");
                inputInterval.setView(input);

                inputInterval.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        PlayerService.startService(context, input.getText().toString());
                    }
                });
                inputInterval.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                inputInterval.show();
            }
        });

        final Button btnStop = ((Button) rootView.findViewById(R.id.btnStop));
        btnStop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                PlayerService.stopService(context);
                ((MainActivity) context).dynaArray.init();

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