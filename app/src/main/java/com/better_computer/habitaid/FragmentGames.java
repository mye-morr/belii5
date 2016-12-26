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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.Games;
import com.better_computer.habitaid.data.core.GamesHelper;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.form.schedule.GamesListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentGames extends AbstractBaseFragment {

    protected DatabaseHelper databaseHelper;
    protected NonSchedHelper nonSchedHelper;

    @Override
    public void refresh() {
        final ListView listViewSt = ((ListView) rootView.findViewById(R.id.schedule_list));
        final EditText etPtsLos = ((EditText) rootView.findViewById(R.id.pts_los));
        final EditText etPtsWa = ((EditText) rootView.findViewById(R.id.pts_wa));
        final EditText etPtsStru = ((EditText) rootView.findViewById(R.id.pts_stru));

        List<Games> games = (List<Games>) (List<?>) gamesHelper.findAll();
        listViewSt.setAdapter(new GamesListAdapter(context, games));

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
                            nonSchedHelper.delete(st.get_id());
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

        refresh();
    }
}