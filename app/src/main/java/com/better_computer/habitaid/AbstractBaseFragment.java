package com.better_computer.habitaid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.GamesHelper;
import com.better_computer.habitaid.data.core.MessageHelper;
import com.better_computer.habitaid.form.NewWizardDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tedwei on 11/30/16.
 */

abstract public class AbstractBaseFragment extends Fragment {

    private static final int SETTING_RESULT = 1;

    public abstract void refresh();

    protected Context context;
    protected View rootView;

    protected GamesHelper gamesHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_schedule_new_events:
                new NewWizardDialog(context, "events").show();
                return true;
            case R.id.action_schedule_new_contacts:
                new NewWizardDialog(context, "contacts").show();
                return true;
            case R.id.action_clear_history:
                clearHistory();
                return true;
            case R.id.action_clear_games:
                clearGames();
                return true;
            case R.id.action_refresh:
                ((MainActivity)context).resetup();
                return true;
            case R.id.action_library_new:
                new NewWizardDialog(context, "library").show();
                return true;
            case R.id.action_schedule_new_ontrack:
                new NewWizardDialog(context, "ontrack").show();
                return true;
            case R.id.action_settings:
                startActivityForResult(new Intent(context, SettingsActivity.class), SETTING_RESULT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SETTING_RESULT) {
                // do something here
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clearGames(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Clear Games");
        builder.setMessage("Are you sure that you want to delete the games history?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                List<SearchEntry> keys = new ArrayList<SearchEntry>();
                List<String> listCat = new ArrayList<String>();
                listCat.add("%");
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.LIKE, listCat));
                gamesHelper.delete(keys);
                ((MainActivity) context).resetup();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void clearHistory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Clear sent message history");
        builder.setMessage("Are you sure that you want to delete the sent message history?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MessageHelper messageHelper = DatabaseHelper.getInstance().getHelper(MessageHelper.class);
                List<SearchEntry> keys = new ArrayList<SearchEntry>();
                List<String> states = new ArrayList<String>();
                states.add("sending");
                states.add("delivered");
                states.add("failed");
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "_state", SearchEntry.Search.IN, states));
                messageHelper.delete(keys);
                ((MainActivity) context).resetup();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

}
