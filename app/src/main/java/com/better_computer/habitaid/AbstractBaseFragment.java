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

import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.form.NewWizardDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tedwei on 11/30/16.
 */

abstract public class AbstractBaseFragment extends Fragment {

    private static final int SETTING_RESULT = 1;

    protected Context context;

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
                ((MainActivity)context).getHistoryPopulator().setupClearHistory();
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

}
