package com.better_computer.habitaid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.Games;
import com.better_computer.habitaid.data.core.GamesHelper;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.form.schedule.GamesListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentGames extends AbstractBaseFragment {

    protected NonSchedHelper nonSchedHelper;
    protected GamesHelper gamesHelper;

    public FragmentGames() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_games, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
        this.gamesHelper = DatabaseHelper.getInstance().getHelper(GamesHelper.class);

        List<Games> games;
        games = (List<Games>) (List<?>) gamesHelper.findAll();
        final ListView listViewSt = ((ListView) rootView.findViewById(R.id.schedule_list));
        listViewSt.setAdapter(new GamesListAdapter(context, games));

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
                            ((MainActivity) context).getSchedulePopulator().resetup();
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
    }

}