package com.better_computer.habitaid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.better_computer.habitaid.data.core.Schedule;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.form.NewWizardDialog;
import com.better_computer.habitaid.form.schedule.ScheduleListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentHistory extends Fragment {

    protected Context context;
    protected ScheduleHelper scheduleHelper;

    public FragmentHistory() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_schedule_events, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {

        this.context = getContext();
        this.scheduleHelper = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class);

        final View dialog = rootView;

   }

}