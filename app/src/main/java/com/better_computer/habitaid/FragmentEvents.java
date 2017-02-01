package com.better_computer.habitaid;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ToggleButton;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.Schedule;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.form.NewWizardDialog;
import com.better_computer.habitaid.form.schedule.ScheduleListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentEvents extends AbstractBaseFragment {

    protected ScheduleHelper scheduleHelper;

    public FragmentEvents() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_events, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_events, container, false);
        this.rootView = view;
        return view;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.scheduleHelper = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class);

        final View dialog = rootView;

        final ListView listView = ((ListView) rootView.findViewById(R.id.schedule_list));

        final ToggleButton btnEvents1 = ((ToggleButton) dialog.findViewById(R.id.btnEvents1));
        final ToggleButton btnEvents2 = ((ToggleButton) dialog.findViewById(R.id.btnEvents2));
        final ToggleButton btnEvents3 = ((ToggleButton) dialog.findViewById(R.id.btnEvents3));
        final ToggleButton btnEvents4 = ((ToggleButton) dialog.findViewById(R.id.btnEvents4));
        final ToggleButton btnEvents5 = ((ToggleButton) dialog.findViewById(R.id.btnEvents5));

        // default to left tab on open-view
        ((MainActivity) context).sSelectedEventsSubcat = btnEvents1.getTextOn().toString();

        btnEvents1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnEvents1.isChecked()) {
                    btnEvents2.setChecked(false);
                    btnEvents3.setChecked(false);
                    btnEvents4.setChecked(false);
                    btnEvents5.setChecked(false);

                    List<Schedule> schedules =
                            (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", btnEvents1.getTextOn().toString());
                    listView.setAdapter(new ScheduleListAdapter(context, schedules));
                }
                else {
                    btnEvents1.setChecked(true);
                }

                ((MainActivity) context).sSelectedEventsSubcat = btnEvents1.getTextOn().toString();
            }
        });

        btnEvents2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnEvents2.isChecked()) {
                    btnEvents1.setChecked(false);
                    btnEvents3.setChecked(false);
                    btnEvents4.setChecked(false);
                    btnEvents5.setChecked(false);

                    List<Schedule> schedules =
                            (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", btnEvents2.getTextOn().toString());
                    listView.setAdapter(new ScheduleListAdapter(context, schedules));
                }
                else {
                    btnEvents2.setChecked(true);
                }

                ((MainActivity) context).sSelectedEventsSubcat = btnEvents2.getTextOn().toString();
            }
        });

        btnEvents3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnEvents3.isChecked()) {
                    btnEvents1.setChecked(false);
                    btnEvents2.setChecked(false);
                    btnEvents4.setChecked(false);
                    btnEvents5.setChecked(false);

                    List<Schedule> schedules =
                            (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", btnEvents3.getTextOn().toString());
                    listView.setAdapter(new ScheduleListAdapter(context, schedules));
                }
                else {
                    btnEvents3.setChecked(true);
                }

                ((MainActivity) context).sSelectedEventsSubcat = btnEvents3.getTextOn().toString();
            }
        });

        btnEvents4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnEvents4.isChecked()) {
                    btnEvents1.setChecked(false);
                    btnEvents2.setChecked(false);
                    btnEvents3.setChecked(false);
                    btnEvents5.setChecked(false);

                    List<Schedule> schedules =
                            (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", btnEvents4.getTextOn().toString());
                    listView.setAdapter(new ScheduleListAdapter(context, schedules));
                }
                else {
                    btnEvents4.setChecked(true);
                }

                ((MainActivity) context).sSelectedEventsSubcat = btnEvents4.getTextOn().toString();
            }
        });

        btnEvents5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnEvents5.isChecked()) {
                    btnEvents1.setChecked(false);
                    btnEvents2.setChecked(false);
                    btnEvents3.setChecked(false);
                    btnEvents4.setChecked(false);

                    List<Schedule> schedules =
                            (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", btnEvents5.getTextOn().toString());
                    listView.setAdapter(new ScheduleListAdapter(context, schedules));
                }
                else {
                    btnEvents5.setChecked(true);
                }

                ((MainActivity) context).sSelectedEventsSubcat = btnEvents5.getTextOn().toString();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Schedule schedule = (Schedule) listView.getItemAtPosition(i);
                AlertDialog.Builder alertOptions = new AlertDialog.Builder(context);
                List<String> optsList = new ArrayList<String>();

                optsList.add("Edit");
                optsList.add("Postpone");

                if (schedule.get_state().equalsIgnoreCase("active")) {
                    optsList.add("Deactivate");
                } else if (schedule.get_state().equalsIgnoreCase("inactive")) {
                    optsList.add("Activate");
                }

                optsList.add("Delete");

                optsList.add("Show Details");

                final String[] options = optsList.toArray(new String[]{});
                alertOptions.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, options), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (options[i].equalsIgnoreCase("POSTPONE")) {
                            AlertDialog.Builder postponeMinutes = new AlertDialog.Builder(context);
                            postponeMinutes.setTitle("Postpone");
                            postponeMinutes.setMessage("Minutes; varia");
                            final EditText input = new EditText(context);
                            postponeMinutes.setView(input);

                            postponeMinutes.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Calendar nextExecute = schedule.getNextExecute();
                                    nextExecute.add(Calendar.MINUTE, Integer.parseInt(input.getText().toString()));
                                    schedule.setNextExecute(nextExecute);
                                    scheduleHelper.update(schedule);
                                }
                            });
                            postponeMinutes.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            postponeMinutes.show();

                        } else if (options[i].equalsIgnoreCase("EDIT")) {
                            new NewWizardDialog(context, schedule).show();
                        } else if (options[i].equalsIgnoreCase("DELETE")) {
                            Toast.makeText(context, "Schedule deleted.", Toast.LENGTH_SHORT).show();
                            scheduleHelper.delete(schedule.get_id());
                            ((MainActivity) context).resetup();
                            dialogInterface.dismiss();
                        } else if (options[i].equalsIgnoreCase("ACTIVATE")) {
                            schedule.set_state("active");
                            //fix - Multiple duplication of schedules Start
                            scheduleHelper.update(schedule);
                            //fix - Multiple duplication of schedules End
                            ((MainActivity) context).resetup();
                        } else if (options[i].equalsIgnoreCase("DEACTIVATE")) {
                            schedule.set_state("inactive");
                            //fix - Multiple duplication of schedules Start
                            scheduleHelper.update(schedule);
                            //fix - Multiple duplication of schedules End
                            ((MainActivity) context).resetup();
                        } else if (options[i].equalsIgnoreCase("SHOW DETAILS")) {
                            AlertDialog.Builder showDetails = new AlertDialog.Builder(context);
                            showDetails.setTitle("Show Details");

                            int iMinutesNextDue = schedule.getNextDue().get(Calendar.MINUTE);
                            String sMinutesNextDue = iMinutesNextDue < 10 ? "0" + String.valueOf(iMinutesNextDue) : String.valueOf(iMinutesNextDue);

                            int iMinutesNextExecute = schedule.getNextExecute().get(Calendar.MINUTE);
                            String sMinutesNextExecute = iMinutesNextExecute < 10 ? "0" + String.valueOf(iMinutesNextExecute) : String.valueOf(iMinutesNextExecute);

                            showDetails.setMessage("frame: " + schedule.get_frame()
                                    + "\n" + "state: " + schedule.get_state()
                                    + "\n" + "repeatEnabled: " + schedule.getRepeatEnable()
                                    + "\n" + "repeatEvery: " + schedule.getRepeatValue() + " " + schedule.getRepeatType()
                                    + "\n" + "prepWindow: " + schedule.getPrepWindow()
                                    + "\n" + "prepWindowType: " + schedule.getPrepWindowType()
                                    + "\n" + "prepCount: " + schedule.getPrepCount()
                                    + "\n" + "nD: " + String.valueOf(schedule.getNextDue().get(Calendar.MONTH) + 1) + "/" + String.valueOf(schedule.getNextDue().get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(schedule.getNextDue().get(Calendar.YEAR)) + " " + String.valueOf(schedule.getNextDue().get(Calendar.HOUR_OF_DAY)) + ":" + sMinutesNextDue
                                    + "\n" + "nE: " + String.valueOf(schedule.getNextExecute().get(Calendar.MONTH) + 1) + "/" + String.valueOf(schedule.getNextExecute().get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(schedule.getNextExecute().get(Calendar.YEAR)) + " " + String.valueOf(schedule.getNextExecute().get(Calendar.HOUR_OF_DAY)) + ":" + sMinutesNextExecute
                            );

                            showDetails.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            showDetails.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            showDetails.show();
                        }
                    }
                });
                alertOptions.setCancelable(true);
                alertOptions.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertOptions.show();
            }
        });

        refresh();
    }

    @Override
    public void refresh() {
        final View dialog = rootView;

        final ListView listView = ((ListView) rootView.findViewById(R.id.schedule_list));

        final ToggleButton btnEvents1 = ((ToggleButton) dialog.findViewById(R.id.btnEvents1));
        final ToggleButton btnEvents2 = ((ToggleButton) dialog.findViewById(R.id.btnEvents2));
        final ToggleButton btnEvents3 = ((ToggleButton) dialog.findViewById(R.id.btnEvents3));
        final ToggleButton btnEvents4 = ((ToggleButton) dialog.findViewById(R.id.btnEvents4));
        final ToggleButton btnEvents5 = ((ToggleButton) dialog.findViewById(R.id.btnEvents5));

        String sActiveSubcategory = "";
        if(btnEvents2.isChecked()) {
            sActiveSubcategory = btnEvents2.getTextOn().toString();
        }
        else if(btnEvents3.isChecked()) {
            sActiveSubcategory = btnEvents3.getTextOn().toString();
        }
        else if(btnEvents4.isChecked()) {
            sActiveSubcategory = btnEvents4.getTextOn().toString();
        }
        else if(btnEvents5.isChecked()) {
            sActiveSubcategory = btnEvents5.getTextOn().toString();
        }
        else {
            sActiveSubcategory = btnEvents1.getTextOn().toString();
        }

        List<Schedule> schedules = (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", sActiveSubcategory);
        listView.setAdapter(new ScheduleListAdapter(context, schedules));
    }
}