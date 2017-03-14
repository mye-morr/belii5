package com.better_computer.habitaid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.Games;
import com.better_computer.habitaid.data.core.GamesHelper;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.form.schedule.GamesListAdapter;
import com.better_computer.habitaid.form.schedule.NonSchedListAdapter;
import com.better_computer.habitaid.util.StopwatchUtil;

import java.util.ArrayList;
import java.util.List;

public class FragmentGames extends AbstractBaseFragment {

    protected DatabaseHelper databaseHelper;
    protected NonSchedHelper nonSchedHelper;
    private TextView stopwatchView;
    private NonSched nonSched;

    @Override
    public void refresh() {
        final ListView listViewGames = ((ListView) rootView.findViewById(R.id.schedule_games));
        final ListView listViewLog = ((ListView) rootView.findViewById(R.id.schedule_list));
        final EditText etPtsLos = ((EditText) rootView.findViewById(R.id.pts_los));
        final EditText etPtsWa = ((EditText) rootView.findViewById(R.id.pts_wa));
        final EditText etPtsStru = ((EditText) rootView.findViewById(R.id.pts_stru));

        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "games"));
        List<NonSched> listNsComTas = (List<NonSched>)(List<?>)nonSchedHelper.find(keys);
        listViewGames.setAdapter(new NonSchedListAdapter(context, listNsComTas));

        List<Games> games = (List<Games>) (List<?>) gamesHelper.findAll();
        listViewLog.setAdapter(new GamesListAdapter(context, games));

        SQLiteDatabase database = this.databaseHelper.getReadableDatabase();

        SQLiteStatement s1 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat='status' AND content<>'maint'" );
        long lSumLos = s1.simpleQueryForLong();
        etPtsLos.setText(String.valueOf(lSumLos));

        SQLiteStatement s2 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat='status' AND content='maint'" );
        long lSumWa = s2.simpleQueryForLong();
        etPtsWa.setText(String.valueOf(lSumWa));

        SQLiteStatement s3 = database.compileStatement( "select sum(pts) from core_tbl_games WHERE cat<>'status'" );
        long lSumStru = s3.simpleQueryForLong();
        etPtsStru.setText(String.valueOf(lSumStru));
    }

    public FragmentGames() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_games, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_games, container, false);
        this.rootView = view;
        return view;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.databaseHelper = DatabaseHelper.getInstance();
        this.nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
        this.gamesHelper = DatabaseHelper.getInstance().getHelper(GamesHelper.class);

        final ListView listViewSt = ((ListView) rootView.findViewById(R.id.schedule_list));
        final ListView listViewGames = ((ListView) rootView.findViewById(R.id.schedule_games));
        stopwatchView = (TextView) rootView.findViewById(R.id.stopwatch);

        final EditText et1_1 = ((EditText) rootView.findViewById(R.id.et1_1));
        final EditText et1_2 = ((EditText) rootView.findViewById(R.id.et1_2));
        final EditText et1_3 = ((EditText) rootView.findViewById(R.id.et1_3));
        final EditText et1_4 = ((EditText) rootView.findViewById(R.id.et1_4));
        final EditText et2_1 = ((EditText) rootView.findViewById(R.id.et2_1));
        final EditText et2_2 = ((EditText) rootView.findViewById(R.id.et2_2));
        final EditText et2_3 = ((EditText) rootView.findViewById(R.id.et2_3));
        final EditText et2_4 = ((EditText) rootView.findViewById(R.id.et2_4));
        final EditText et3_1 = ((EditText) rootView.findViewById(R.id.et3_1));
        final EditText et3_2 = ((EditText) rootView.findViewById(R.id.et3_2));
        final EditText et3_3 = ((EditText) rootView.findViewById(R.id.et3_3));
        final EditText et3_4 = ((EditText) rootView.findViewById(R.id.et3_4));
        final EditText et4_1 = ((EditText) rootView.findViewById(R.id.et4_1));
        final EditText et4_2 = ((EditText) rootView.findViewById(R.id.et4_2));
        final EditText et4_3 = ((EditText) rootView.findViewById(R.id.et4_3));
        final EditText et4_4 = ((EditText) rootView.findViewById(R.id.et4_4));

        listViewGames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                nonSched = (NonSched)listViewGames.getItemAtPosition(i);
                et1_3.setText(nonSched.getSubcat());
                et1_4.setText(nonSched.getName());

                String[] sxLabels = new String[14];
                String[] sxLabelsSrc = nonSched.getContent().split(";");

                for(int j = 0; j<14; j++) {
                    sxLabels[j] = "";
                }

                for(int j = 0; j<sxLabelsSrc.length; j++) {
                    sxLabels[j] = sxLabelsSrc[j];
                }

                et2_1.setText(sxLabels[0]);
                et2_2.setText(sxLabels[1]);
                et2_3.setText(sxLabels[2]);
                et2_4.setText(sxLabels[3]);
                et3_1.setText(sxLabels[4]);
                et3_2.setText(sxLabels[5]);
                et3_3.setText(sxLabels[6]);
                et3_4.setText(sxLabels[7]);
                et4_1.setText(sxLabels[8]);
                et4_2.setText(sxLabels[9]);
                et4_3.setText(sxLabels[10]);
                et4_4.setText(sxLabels[11]);
                et1_1.setText(sxLabels[12]);
                et1_2.setText(sxLabels[13]);
            }
        });

        listViewSt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Games st = (Games) listViewSt.getItemAtPosition(i);
                AlertDialog.Builder alertOptions = new AlertDialog.Builder(context);
                List<String> optsList = new ArrayList<String>();

                optsList.add("Delete");

                final String[] options = optsList.toArray(new String[]{});
                alertOptions.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, options), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (options[i].equalsIgnoreCase("DELETE")) {
                            Toast.makeText(context, "Schedule deleted.", Toast.LENGTH_SHORT).show();
                            gamesHelper.delete(st.get_id());
                            refresh();
                            dialogInterface.dismiss();
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

        final Button bCon = ((Button) rootView.findViewById(R.id.con));
        final Button bStSt = ((Button) rootView.findViewById(R.id.stst));
        final Button bNew = ((Button) rootView.findViewById(R.id.ne));
        final Button bSav = ((Button) rootView.findViewById(R.id.sav));
        final Button bDel = ((Button) rootView.findViewById(R.id.del));

        bNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et1_1.setText("");
                et1_2.setText("");
                et1_3.setText("Category");
                et1_4.setText("Untitled");
                et2_1.setText("");
                et2_2.setText("");
                et2_3.setText("");
                et2_4.setText("");
                et3_1.setText("");
                et3_2.setText("");
                et3_3.setText("");
                et3_4.setText("");
                et4_1.setText("");
                et4_2.setText("");
                et4_3.setText("");
                et4_4.setText("");
                refresh();
            }
        });

        bSav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NonSched nsComTas = new NonSched();
                nsComTas.setCat("games");
                nsComTas.setSubcat(et1_3.getText().toString());
                nsComTas.setName(et1_4.getText().toString());
                nsComTas.setContent(
                    et2_1.getText().toString()
                    + ";" + et2_2.getText().toString()
                    + ";" + et2_3.getText().toString()
                    + ";" + et2_4.getText().toString()
                    + ";" + et3_1.getText().toString()
                    + ";" + et3_2.getText().toString()
                    + ";" + et3_3.getText().toString()
                    + ";" + et3_4.getText().toString()
                    + ";" + et4_1.getText().toString()
                    + ";" + et4_2.getText().toString()
                    + ";" + et4_3.getText().toString()
                    + ";" + et4_4.getText().toString()
                    + ";" + et1_1.getText().toString()
                    + ";" + et1_2.getText().toString() + ";"
                );

                // returns boolean
                if (DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).createOrUpdate(nsComTas)) {
                    Toast.makeText(context, "Game saved.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Game saving failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bStSt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if already started, set new stop time and display
                if (StopwatchUtil.getStopwatchStopTime(context) < 0) {
                    StopwatchUtil.setStopwatchStopTime(context, System.currentTimeMillis());

                    long passedTime = StopwatchUtil.getStopwatchPassedTime(context);
                    long passedSeconds = passedTime / 1000;
                    String strPassedTime = String.format("%d:%02d", passedSeconds / 60, passedSeconds % 60);
                    stopwatchView.setText(strPassedTime);
                }
                else {
                    // if not yet started, reset the start time
                    StopwatchUtil.resetStopwatchStartTime(context);
                    stopwatchView.setText("");
                }
            }
        });

        refresh();
    }
}