package com.better_computer.habitaid.form.schedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.better_computer.habitaid.MainActivity;
import com.better_computer.habitaid.MyApplication;
import com.better_computer.habitaid.R;
import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.Content;
import com.better_computer.habitaid.data.core.ContentHelper;
import com.better_computer.habitaid.data.core.Games;
import com.better_computer.habitaid.data.core.GamesHelper;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.data.core.PlayerHelper;
import com.better_computer.habitaid.data.core.Schedule;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.form.NewWizardDialog;
import com.better_computer.habitaid.util.DynaArray;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class SchedulePopulator {
    protected Context context;
    protected View rootView;
    protected String category;

    protected ScheduleHelper scheduleHelper;
    protected NonSchedHelper nonSchedHelper;
    protected GamesHelper gamesHelper;
    protected PlayerHelper playerHelper;
    protected ContentHelper contentHelper;
    protected DatabaseHelper databaseHelper;
    protected Calendar calSimulate;

    //protected volatile PlayerTask objCurPlayerTask;
    private DynaArray dynaArray = new DynaArray();

    private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void setupNew(String sCategory){
        new NewWizardDialog(context, sCategory).show();
    }

    public void resetup() {
        setup(rootView, category);
    }

    public void resetup(String category){
        setup(rootView, category);
    }

    public SchedulePopulator(Context context) {
        this.context = context;

        this.scheduleHelper = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class);
        this.nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
        this.gamesHelper = DatabaseHelper.getInstance().getHelper(GamesHelper.class);
        this.playerHelper = DatabaseHelper.getInstance().getHelper(PlayerHelper.class);
        this.contentHelper = DatabaseHelper.getInstance().getHelper(ContentHelper.class);
        this.databaseHelper = DatabaseHelper.getInstance();
        this.calSimulate = Calendar.getInstance();
    }

    public void setup(View rootView, String category) {
        this.rootView = rootView;
        this.category = category;

        if(category.equals("ontrack")) {
            setup_ontrack(rootView);
        }
        else if(category.equals("fb_integrate")) {
            setup_fb_integrate(rootView);
        }
        else if(category.equals("events")) {
            setup_events(rootView);
        }
        else if(category.equals("contacts")) {
            setup_contacts(rootView);
        }
        else if(category.equals("games")) {
            setup_games(rootView);
        }
        else if(category.equals("library")) {
            setup_library(rootView);
        }
        else if(category.equals("new_player")) {
            setup_new_player(rootView);
        }
        else if(category.equals("player")) {
            //setup_player(rootView);
        }
    }

    public void setup_fb_integrate(final View rootView) {
        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        Fragment fragment = ((MainActivity)context).getSupportFragmentManager().findFragmentById(R.id.container);
        loginButton.setFragment(fragment);

        final View postView = rootView.findViewById(R.id.post_button);
        postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl (Uri.parse("http://www.google.com"))
                        .setContentTitle("Hello Facebook")
                        .setContentDescription(
                                "The 'Hello Facebook' sample showcases simple Facebook integration")
                        .build();
                ShareDialog shareDialog = new ShareDialog((MainActivity)context);
                shareDialog.show(linkContent);
            }
        });

        final View postLayoutView = rootView.findViewById(R.id.post_layout);
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            // user has logged in
            postLayoutView.setVisibility(View.VISIBLE);
        }

        CallbackManager callbackManager = ((MyApplication)((MainActivity) context).getApplication()).getCallbackManager();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Test", "onSuccess");
                postLayoutView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                Log.d("Test", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Test", "onError");
            }
        });
    }

    public void setup_games(final View rootView) {

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

    public void setup_ontrack(final View rootView) {

        final View dialog = rootView;

        final ListView listView = ((ListView) rootView.findViewById(R.id.schedule_list));

        final ToggleButton btnOnTrack1 = ((ToggleButton) dialog.findViewById(R.id.btnOnTrack1));
        final ToggleButton btnOnTrack2 = ((ToggleButton) dialog.findViewById(R.id.btnOnTrack2));

        btnOnTrack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnOnTrack1.isChecked()) {
                    btnOnTrack2.setChecked(false);

                    List<Schedule> schedules =
                            (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", btnOnTrack1.getTextOn().toString());
                    listView.setAdapter(new ScheduleListAdapter(context, schedules));
                }
                else {
                    btnOnTrack1.setChecked(true);
                }
            }
        });

        btnOnTrack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnOnTrack2.isChecked()) {
                    btnOnTrack1.setChecked(false);

                    List<Schedule> schedules =
                            (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", btnOnTrack2.getTextOn().toString());
                    listView.setAdapter(new ScheduleListAdapter(context, schedules));
                }
                else {
                    btnOnTrack2.setChecked(true);
                }
            }
        });

        String sActiveSubcategory = "";
        if(btnOnTrack1.isChecked()) {
            sActiveSubcategory = btnOnTrack1.getTextOn().toString();
        }
        else {
            sActiveSubcategory = btnOnTrack2.getTextOn().toString();
        }

        List<Schedule> schedules = (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", sActiveSubcategory);
        listView.setAdapter(new ScheduleListAdapter(context, schedules));
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
                            ((MainActivity) context).getSchedulePopulator().resetup();
                            dialogInterface.dismiss();
                        } else if (options[i].equalsIgnoreCase("ACTIVATE")) {
                            schedule.set_state("active");
                            //fix - Multiple duplication of schedules Start
                            scheduleHelper.update(schedule);
                            //fix - Multiple duplication of schedules End
                            ((MainActivity) context).getSchedulePopulator().resetup();
                        } else if (options[i].equalsIgnoreCase("DEACTIVATE")) {
                            schedule.set_state("inactive");
                            //fix - Multiple duplication of schedules Start
                            scheduleHelper.update(schedule);
                            //fix - Multiple duplication of schedules End
                            ((MainActivity) context).getSchedulePopulator().resetup();
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
    }

    public void setup_events(final View rootView) {

        final View dialog = rootView;

        final ListView listView = ((ListView) rootView.findViewById(R.id.schedule_list));

        final ToggleButton btnEvents1 = ((ToggleButton) dialog.findViewById(R.id.btnEvents1));
        final ToggleButton btnEvents2 = ((ToggleButton) dialog.findViewById(R.id.btnEvents2));
        final ToggleButton btnEvents3 = ((ToggleButton) dialog.findViewById(R.id.btnEvents3));
        final ToggleButton btnEvents4 = ((ToggleButton) dialog.findViewById(R.id.btnEvents4));
        final ToggleButton btnEvents5 = ((ToggleButton) dialog.findViewById(R.id.btnEvents5));

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
            }
        });

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
                            ((MainActivity) context).getSchedulePopulator().resetup();
                            dialogInterface.dismiss();
                        } else if (options[i].equalsIgnoreCase("ACTIVATE")) {
                            schedule.set_state("active");
                            //fix - Multiple duplication of schedules Start
                            scheduleHelper.update(schedule);
                            //fix - Multiple duplication of schedules End
                            ((MainActivity) context).getSchedulePopulator().resetup();
                        } else if (options[i].equalsIgnoreCase("DEACTIVATE")) {
                            schedule.set_state("inactive");
                            //fix - Multiple duplication of schedules Start
                            scheduleHelper.update(schedule);
                            //fix - Multiple duplication of schedules End
                            ((MainActivity) context).getSchedulePopulator().resetup();
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
    }

    public void setup_contacts(final View rootView) {

        final View dialog = rootView;

        final ListView listViewContacts = ((ListView) rootView.findViewById(R.id.schedule_list));

        final ToggleButton btnContacts1 = ((ToggleButton) dialog.findViewById(R.id.btnContacts1));
        final ToggleButton btnContacts2 = ((ToggleButton) dialog.findViewById(R.id.btnContacts2));
        final ToggleButton btnContacts3 = ((ToggleButton) dialog.findViewById(R.id.btnContacts3));

        btnContacts1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnContacts1.isChecked()) {
                    btnContacts2.setChecked(false);
                    btnContacts3.setChecked(false);

                    List<Schedule> schedules =
                            (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", btnContacts1.getTextOn().toString());
                    listViewContacts.setAdapter(new ScheduleListAdapter(context, schedules));
                }
                else {
                    btnContacts1.setChecked(true);
                }
            }
        });

        btnContacts2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnContacts2.isChecked()) {
                    btnContacts1.setChecked(false);
                    btnContacts3.setChecked(false);

                    List<Schedule> schedules =
                            (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", btnContacts2.getTextOn().toString());
                    listViewContacts.setAdapter(new ScheduleListAdapter(context, schedules));
                }
                else {
                    btnContacts2.setChecked(true);
                }
            }
        });

        btnContacts3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnContacts3.isChecked()) {
                    btnContacts1.setChecked(false);
                    btnContacts2.setChecked(false);

                    List<Schedule> schedules =
                            (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", btnContacts3.getTextOn().toString());
                    listViewContacts.setAdapter(new ScheduleListAdapter(context, schedules));
                }
                else {
                    btnContacts3.setChecked(true);
                }
            }
        });

        String sActiveSubcategory = "";
        if(btnContacts2.isChecked()) {
            sActiveSubcategory = btnContacts2.getTextOn().toString();
        }
        else if (btnContacts3.isChecked()) {
            sActiveSubcategory = btnContacts3.getTextOn().toString();
        }
        else {
            sActiveSubcategory = btnContacts1.getTextOn().toString();
        }

        List<Schedule> schedules = (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", sActiveSubcategory);
        listViewContacts.setAdapter(new ScheduleListAdapter(context, schedules));
        listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Schedule schedule = (Schedule) listViewContacts.getItemAtPosition(i);
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
                            ((MainActivity) context).getSchedulePopulator().resetup();
                            dialogInterface.dismiss();
                        } else if (options[i].equalsIgnoreCase("ACTIVATE")) {
                            schedule.set_state("active");
                            //fix - Multiple duplication of schedules Start
                            scheduleHelper.update(schedule);
                            //fix - Multiple duplication of schedules End
                            ((MainActivity) context).getSchedulePopulator().resetup();
                        } else if (options[i].equalsIgnoreCase("DEACTIVATE")) {
                            schedule.set_state("inactive");
                            //fix - Multiple duplication of schedules Start
                            scheduleHelper.update(schedule);
                            //fix - Multiple duplication of schedules End
                            ((MainActivity) context).getSchedulePopulator().resetup();
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
    }

    public void setup_new_player(final View rootView) {

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

    public void setup_library(final View rootView) {

        final ListView listViewCategory = ((ListView) rootView.findViewById(R.id.schedule_category_list));
        final ListView listViewSubcategory = ((ListView) rootView.findViewById(R.id.schedule_subcategory_list));
        final RecyclerView listViewLibrary = ((RecyclerView) rootView.findViewById(R.id.schedule_library_list));
        final NonSchedRecyclerViewAdapter libViewAdapter = new NonSchedRecyclerViewAdapter(context);
        ItemTouchHelper itemTouchHelper = libViewAdapter.getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(listViewLibrary);
        listViewLibrary.setAdapter(libViewAdapter);

        String sCat = ((MainActivity) (context)).sSelectedLibraryCat;
        String sSubcat = ((MainActivity) (context)).sSelectedLibrarySubcat;

        SQLiteDatabase database = this.databaseHelper.getReadableDatabase();

        String sql = "SELECT DISTINCT cat FROM core_tbl_nonsched ORDER BY cat";
        Cursor cursor = database.rawQuery(sql, new String[0]);

        List<String> listCat = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                listCat.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        //fix - android.database.CursorWindowAllocationException Start
        cursor.close();
        //fix - android.database.CursorWindowAllocationException End

        ArrayAdapter<String> adapterCat = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listCat);
        listViewCategory.setAdapter(adapterCat);

        if (sCat.length() > 0) {
            String sql2 = "SELECT DISTINCT subcat FROM core_tbl_nonsched WHERE cat='" + sCat + "' ORDER BY subcat";

            SQLiteDatabase database2 = databaseHelper.getReadableDatabase();
            Cursor cursor2 = database2.rawQuery(sql2, new String[0]);

            List<String> listSubcat = new ArrayList<String>();
            if (cursor2.moveToFirst()) {
                do {
                    listSubcat.add(cursor2.getString(0));
                } while (cursor2.moveToNext());
            }

            //fix - android.database.CursorWindowAllocationException Start
            cursor2.close();
            //fix - android.database.CursorWindowAllocationException End

            ArrayAdapter<String> adapterSubcat = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listSubcat);
            listViewSubcategory.setAdapter(adapterSubcat);

            ///////////////////////////////////////////
            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, sCat));

            if (sSubcat.length() > 0) {
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcat", SearchEntry.Search.EQUAL, sSubcat));
            }

            List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
            libViewAdapter.setList(listNonSched);
        }
        else {
            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "library"));

            List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
            libViewAdapter.setList(listNonSched);
        }

        listViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String sCat = listViewCategory.getItemAtPosition(i).toString();

            ((MainActivity) context).sSelectedLibraryCat = sCat;
            ((MainActivity) context).sSelectedLibrarySubcat = "";

            String sql2 = "SELECT DISTINCT subcat FROM core_tbl_nonsched WHERE cat='" + sCat + "' ORDER BY subcat";

            SQLiteDatabase database2 = databaseHelper.getReadableDatabase();
            Cursor cursor2 = database2.rawQuery(sql2, new String[0]);

            List<String> listSubcat = new ArrayList<String>();
            if (cursor2.moveToFirst()) {
                do {
                    listSubcat.add(cursor2.getString(0));
                } while (cursor2.moveToNext());
            }

            //fix - android.database.CursorWindowAllocationException Start
            cursor2.close();
            //fix - android.database.CursorWindowAllocationException End

            ArrayAdapter<String> adapterSubcat = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listSubcat);
            listViewSubcategory.setAdapter(adapterSubcat);

            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, sCat));

            List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
            libViewAdapter.setList(listNonSched);
            }
        });

        listViewSubcategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sSubcat = listViewSubcategory.getItemAtPosition(i).toString();

                ((MainActivity) context).sSelectedLibrarySubcat = sSubcat;

                List<SearchEntry> keys = new ArrayList<SearchEntry>();
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, ((MainActivity)context).sSelectedLibraryCat));
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcat", SearchEntry.Search.EQUAL, sSubcat));

                List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
                libViewAdapter.setList(listNonSched);
            }
        });
    }

    /*
    public void setup_player(final View rootView) {
        final List<NonSched> listPlayer = (List<NonSched>) (List<?>) nonSchedHelper.findBy("cat","player");

        final EditText etPlayerContent = ((EditText) rootView.findViewById(R.id.etPlayerContent));
        final EditText etAddName = ((EditText) rootView.findViewById(R.id.player_add_name));

        final ListView listView = ((ListView) rootView.findViewById(R.id.player_list));
        listView.setAdapter(new NonSchedListAdapter(context, listPlayer));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final NonSched nsPlayer = (NonSched) listView.getItemAtPosition(i);
                etAddName.setText(nsPlayer.getName());
                etPlayerContent.setText(nsPlayer.getContent());
            }
        });

        final ImageButton addButton = ((ImageButton) rootView.findViewById(R.id.player_add_btn));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NonSched nsPlayer = new NonSched();
                nsPlayer.setCat("player");

                //!!! need to eventually add a subcategory etc.
                nsPlayer.setName(etAddName.getText().toString());
                nsPlayer.setContent(etPlayerContent.getText().toString());

                if(DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).createOrUpdate(nsPlayer)) {
                    Toast.makeText(context, "Saved.", Toast.LENGTH_SHORT).show();
                } else {
                Toast.makeText(context, "Saving failed.", Toast.LENGTH_SHORT).show();
                }

                setup_player(rootView);
            }
        });

        final ImageButton delButton = ((ImageButton) rootView.findViewById(R.id.player_del_btn));
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NonSched nsCurrent = (NonSched) nonSchedHelper.getBy("name",etAddName.getText().toString());
                Toast.makeText(context, "Deleted.", Toast.LENGTH_SHORT).show();
                nonSchedHelper.delete(nsCurrent.get_id());

                setup_player(rootView);
            }
        });

        final Button btnSuper = ((Button) rootView.findViewById(R.id.btnSuper));
        btnSuper.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                objCurPlayerTask = new PlayerTask(etPlayerContent.getText().toString().split("\\n"), "SUPER");
                objCurPlayerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        final Button btnHigh = ((Button) rootView.findViewById(R.id.btnHigh));
        btnHigh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                objCurPlayerTask = new PlayerTask(etPlayerContent.getText().toString().split("\\n"), "HIGH");
                objCurPlayerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        final Button btnMedium = ((Button) rootView.findViewById(R.id.btnMedium));
        btnMedium.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                objCurPlayerTask = new PlayerTask(etPlayerContent.getText().toString().split("\\n"), "MEDIUM");
                objCurPlayerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        final Button btnLow = ((Button) rootView.findViewById(R.id.btnLow));
        btnLow.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                objCurPlayerTask = new PlayerTask(etPlayerContent.getText().toString().split("\\n"), "LOW");
                objCurPlayerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        final Button btnStopPlayer = ((Button) rootView.findViewById(R.id.btnStopFlashcards));
        btnStopPlayer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (objCurPlayerTask != null) {
                    objCurPlayerTask.cancel(true);
                }

                Toast.makeText(context, "thanks for playing", Toast.LENGTH_SHORT).show();
            }
        });
    }
    */

    private Calendar stringToDate(String sInput) {

        //schedule.getScheduleDate().set(iYear, iMonth, iDay, calWorking.get(Calendar.HOUR_OF_DAY), calWorking.get(Calendar.MINUTE));

        return Calendar.getInstance();
    }

    private String dateToString(Calendar calInput) {
        String sBuf = "";

        Integer iMonth = calInput.get(Calendar.MONTH);
        Integer iDay = calInput.get(Calendar.DAY_OF_MONTH);
        Integer iHour = calInput.get(Calendar.HOUR_OF_DAY);
        Integer iMinute = calInput.get(Calendar.MINUTE);

        if(iMonth < 10)
            sBuf += "0";

        sBuf += String.valueOf(iMonth) + "/";

        if(iDay < 10)
            sBuf += "0";

        sBuf += String.valueOf(iDay)
                + "/" + calInput.get(Calendar.YEAR) + ", ";

        if (iHour < 10)
            sBuf += "0";
        sBuf += String.valueOf(iHour) + ":";

        if(iMinute < 10)
            sBuf += "0";
        sBuf += String.valueOf(iMinute);

        return sBuf;
    }

    public void setupClearGames(){
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
                ((MainActivity) context).getSchedulePopulator().resetup();
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

    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    /*
    private class PlayerTask extends AsyncTask<Void, Void, Integer> {
        private String[] sxItems;
        private Integer[] ixRandIdx;
        private int len;
        private int nxt;
        private int iMinBreak;

        public PlayerTask(String[] sxItems, String sRate) {

            switch(sRate) {
                case "SUPER":
                    this.iMinBreak = 1;
                    break;
                case "HIGH":
                    this.iMinBreak = 2;
                    break;
                case "MEDIUM":
                    this.iMinBreak = 3;
                    break;
                case "LOW":
                    this.iMinBreak = 4;
                    break;
                default:
                    this.iMinBreak = 2;
                    break;
            }

            this.sxItems = sxItems;
            this.len = sxItems.length;
            this.ixRandIdx = genRandIdx(this.len);
            this.nxt = -1;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Void... params) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();

            String sAction;
            String[] fc_output;

            while(!isCancelled()) {

                String sKeyVal = sxItems[ixRandIdx[++nxt]];

                if (nxt == (len-1)) {
                    nxt = -1;
                }

                int iBuf = sKeyVal.indexOf("-=");
                int iBufColon = sKeyVal.indexOf(":");

                if(iBuf > 0) {
                    if (iBufColon > 0) {
                        sAction = "com.example.SendBroadcast.fc_cat_4";
                        fc_output = new String[9];

                        for(int i=0; i<fc_output.length; i++)
                            fc_output[i] = "";

                        String sKey = sKeyVal.substring(0, iBuf).trim();
                        String sParamVal = sKeyVal.substring(iBuf + 2).trim();

                        fc_output[0] = sKey;

                        String[] sxParamVal = sParamVal.split(";");
                        switch (sxParamVal.length) {
                            case 4:
                                sxParamVal[3] = sxParamVal[3].trim();
                                iBuf = sxParamVal[3].indexOf(":");
                                fc_output[7] = sxParamVal[3].substring(0, Math.min(iBuf, 2));
                                fc_output[8] = sxParamVal[3].substring(iBuf + 1).trim();
                            case 3:
                                sxParamVal[2] = sxParamVal[2].trim();
                                iBuf = sxParamVal[2].indexOf(":");
                                fc_output[5] = sxParamVal[2].substring(0, Math.min(iBuf, 2));
                                fc_output[6] = sxParamVal[2].substring(iBuf + 1).trim();
                            case 2:
                                sxParamVal[1] = sxParamVal[1].trim();
                                iBuf = sxParamVal[1].indexOf(":");
                                fc_output[3] = sxParamVal[1].substring(0, Math.min(iBuf, 2));
                                fc_output[4] = sxParamVal[1].substring(iBuf + 1).trim();
                            case 1:
                                sxParamVal[0] = sxParamVal[0].trim();
                                iBuf = sxParamVal[0].indexOf(":");
                                fc_output[1] = sxParamVal[0].substring(0, Math.min(iBuf, 2));
                                fc_output[2] = sxParamVal[0].substring(iBuf + 1).trim();
                            default:
                                break;
                        }
                    }
                    else {
                        sAction = "com.example.SendBroadcast.fc2";
                        fc_output = new String[2];

                        String sKey = sKeyVal.substring(0, iBuf).trim();
                        String sVal = sKeyVal.substring(iBuf + 2).trim();

                        fc_output[0] = sKey;
                        fc_output[1] = sVal;

                    }

                    String sOutput = "";
                    for(int i=0; i < fc_output.length; i++)
                        sOutput += fc_output[i] + " |";

                    Intent i1 = new Intent();
                    i1.setAction(sAction);
                    i1.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    i1.putExtra("STRING_FC", sOutput);
                    i1.putExtra("BOOL_SHOW_ANSWERS", false);

                    context.sendBroadcast(i1);

                    try {
                        Thread.sleep(30 * 1000);

                        Intent i2 = new Intent();
                        i2.setAction(sAction);
                        i2.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        i2.putExtra("STRING_FC", sOutput);
                        i2.putExtra("BOOL_SHOW_ANSWERS", true);
                        context.sendBroadcast(i2);

                        Thread.sleep(iMinBreak * 60 * 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                }

                else { // no -= delimiter

                    Intent i3 = new Intent();
                    i3.setAction("com.example.SendBroadcast.fc1");
                    i3.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    i3.putExtra("STRING_FC", sKeyVal);
                    i3.putExtra("BOOL_SHOW_ANSWERS", false);

                    context.sendBroadcast(i3);

                    try {
                        Thread.sleep(iMinBreak * 60 * 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            wl.release();
            return 1;
        }

        @Override
        protected void onCancelled(Integer id) {
        }

        @Override
        protected void onPostExecute(Integer id) {
        }
    }

    protected Integer[] genRandIdx(int iSize) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < iSize; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        return list.toArray(new Integer[list.size()]);
    }
    */
}