package com.better_computer.habitaid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.data.core.Schedule;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.form.NewWizardDialog;
import com.better_computer.habitaid.form.schedule.ScheduleListAdapter;
import com.better_computer.habitaid.util.BaseItemTouchHelperCallback;
import com.better_computer.habitaid.util.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class FragmentOnTrack extends AbstractBaseFragment {

    protected View rootView;

    protected ScheduleHelper scheduleHelper;
    protected NonSchedHelper nonSchedHelper;

    ToggleButton btnOnTrack1;
    ToggleButton btnOnTrack2;
    RecyclerView listViewArck;
    OnTrackRecyclerViewAdapter listViewArckAdapter;

    public FragmentOnTrack() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ontrack, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_ontrack, container, false);
        this.rootView = view;
        return view;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.scheduleHelper = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class);
        this.nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);

        final View dialog = rootView;

        final ListView listView = ((ListView) dialog.findViewById(R.id.schedule_list));
        listViewArck = ((RecyclerView) dialog.findViewById(R.id.arck_list));
        listViewArckAdapter = new OnTrackRecyclerViewAdapter(context);
        ItemTouchHelper itemTouchHelper = listViewArckAdapter.getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(listViewArck);
        listViewArck.setAdapter(listViewArckAdapter);

        btnOnTrack1 = ((ToggleButton) dialog.findViewById(R.id.btnOnTrack1));
        btnOnTrack2 = ((ToggleButton) dialog.findViewById(R.id.btnOnTrack2));

        // default to right tab on open-view
        ((MainActivity) context).sSelectedEventsSubcat = btnOnTrack2.getTextOn().toString();
        
        btnOnTrack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnOnTrack1.isChecked()) {
                    btnOnTrack2.setChecked(false);
                }
                else {
                    btnOnTrack1.setChecked(true);
                }

                ((MainActivity) context).sSelectedEventsSubcat = btnOnTrack1.getTextOn().toString();
                refresh();
            }
        });

        btnOnTrack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnOnTrack2.isChecked()) {
                    btnOnTrack1.setChecked(false);
                }
                else {
                    btnOnTrack2.setChecked(true);
                }

                ((MainActivity) context).sSelectedEventsSubcat = btnOnTrack2.getTextOn().toString();
                refresh();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Schedule schedule = (Schedule) listView.getItemAtPosition(i);
                AlertDialog.Builder alertOptions = new AlertDialog.Builder(context);
                List<String> optsList = new ArrayList<String>();

                optsList.add("Move To");
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
                                    Calendar nextExecute = Calendar.getInstance();
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
                        } else if (options[i].equalsIgnoreCase("MOVE TO")) {
                            NonSched nsItem = new NonSched();
                            nsItem.setCat("arck");

                            nsItem.setSubcat(((MainActivity) context).sSelectedEventsSubcat);
                            nsItem.setContent(schedule.getMessage());

                            // returns boolean
                            if (DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).createAndShift(nsItem)) {
                                Toast.makeText(context, "Moved schedule.", Toast.LENGTH_SHORT).show();
                                scheduleHelper.delete(schedule.get_id());
                            }
                            else {
                                Toast.makeText(context, "Schedule moving failed.", Toast.LENGTH_SHORT).show();
                            }

                            ((MainActivity) context).resetup();

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
        final ToggleButton btnOnTrack1 = ((ToggleButton) dialog.findViewById(R.id.btnOnTrack1));
        final ToggleButton btnOnTrack2 = ((ToggleButton) dialog.findViewById(R.id.btnOnTrack2));
        final ListView listView = ((ListView) dialog.findViewById(R.id.schedule_list));

        List<SearchEntry> keys = new ArrayList<SearchEntry>();
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "arck"));

        String sActiveSubcategory = "";
        if(btnOnTrack1.isChecked()) {
            sActiveSubcategory = btnOnTrack1.getTextOn().toString();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcat", SearchEntry.Search.EQUAL, btnOnTrack1.getTextOn().toString()));
        }
        else {
            sActiveSubcategory = btnOnTrack2.getTextOn().toString();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcat", SearchEntry.Search.EQUAL, btnOnTrack2.getTextOn().toString()));
        }

        List<Schedule> schedules = (List<Schedule>) (List<?>) scheduleHelper.findBy("subcategory", sActiveSubcategory);
        listView.setAdapter(new ScheduleListAdapter(context, schedules));

        List<NonSched> listNsArck = (List<NonSched>)(List<?>)nonSchedHelper.find(keys, "ORDER BY iprio");
        listViewArckAdapter.setList(listNsArck);
    }

    class OnTrackRecyclerViewAdapter extends RecyclerView.Adapter<OnTrackRecyclerViewAdapter.ViewHolder>
            implements ItemTouchHelperAdapter {

        private Context context;
        private ItemTouchHelper itemTouchHelper;
        private List<NonSched> nonSchedList;
        private NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);

        public OnTrackRecyclerViewAdapter(Context context) {
            this.context = context;
            ItemTouchHelper.Callback callback = new BaseItemTouchHelperCallback(this);
            this.itemTouchHelper = new ItemTouchHelper(callback);
        }

        public void setList(List<NonSched> nonSchedList) {
            this.nonSchedList = nonSchedList;
            this.notifyDataSetChanged();
        }

        public ItemTouchHelper getItemTouchHelper() {
            return this.itemTouchHelper;
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(nonSchedList, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            new SaveOrderTask().execute();
            return true;
        }

        @Override
        public void onItemDismiss(int position) {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_non_schedule, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final NonSched nonSched = nonSchedList.get(position);
            holder.setData(nonSched);

            holder.handleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) ==
                            MotionEvent.ACTION_DOWN) {
                        itemTouchHelper.startDrag(holder);
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return nonSchedList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private NonSched nsItem;
            private TextView itemSummaryView;
            private ImageView itemIconView;
            private final ImageView handleView;

            private ViewHolder(View itemView) {
                super(itemView);
                itemView.setBackgroundColor(0x00000000);
                handleView = (ImageView) itemView.findViewById(R.id.handle);
                itemSummaryView = (TextView) itemView.findViewById(R.id.schedule_item_summary);
                itemIconView = (ImageView) itemView.findViewById(R.id.schedule_item_icon);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertOptions = new AlertDialog.Builder(context);
                        List<String> optsList = new ArrayList<String>();

                        optsList.add("Move To");
                        optsList.add("Delete");

                        final String[] options = optsList.toArray(new String[]{});
                        alertOptions.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, options), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (options[i].equalsIgnoreCase("MOVE TO")) {

                                    AlertDialog.Builder inputInterval = new AlertDialog.Builder(context);
                                    inputInterval.setTitle("Interval");
                                    inputInterval.setMessage("Minutes; varia");
                                    final EditText input = new EditText(context);
                                    inputInterval.setView(input);

                                    inputInterval.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Schedule schedule = new Schedule();
                                            schedule.setReceiver("");
                                            schedule.setReceiverName("");

                                            schedule.setCategory("ontrack");
                                            schedule.setSubcategory(((MainActivity) context).sSelectedEventsSubcat);

                                            Calendar instCal = Calendar.getInstance();
                                            instCal.add(Calendar.MINUTE, 1);

                                            schedule.getNextDue().set(Calendar.HOUR_OF_DAY, instCal.get(Calendar.HOUR_OF_DAY));
                                            schedule.getNextDue().set(Calendar.MINUTE, instCal.get(Calendar.MINUTE));

                                            schedule.setRemindInterval(input.getText().toString());

                                            schedule.set_frame("");
                                            schedule.set_state("active");
                                            schedule.setPrepCount("0");
                                            schedule.setRepeatEnable("false");

                                            schedule.setNextExecute(schedule.getNextDue());
                                            schedule.setMessage(nsItem.getContent());

                                            // returns boolean
                                            if (DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).createOrUpdate(schedule)) {
                                                Toast.makeText(context, "Added schedule.", Toast.LENGTH_SHORT).show();
                                                DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).delete(nsItem.get_id());
                                            } else {
                                                Toast.makeText(context, "Schedule saving failed.", Toast.LENGTH_SHORT).show();
                                            }

                                            ((MainActivity) context).resetup();

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
                                else if (options[i].equalsIgnoreCase("DELETE")) {
                                    Toast.makeText(context, "Schedule deleted.", Toast.LENGTH_SHORT).show();
                                    DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).delete(nsItem.get_id());

                                    ((MainActivity) context).resetup();
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

            private void setData(NonSched item) {
                this.nsItem = item;

                String sLabel = item.getContent();
                itemSummaryView.setText(sLabel);

                if(item.get_state().equalsIgnoreCase("active")) {
                    itemIconView.setImageResource(R.drawable.schedule_single);
                }else{
                    itemIconView.setImageResource(R.drawable.schedule_single_inactive);
                }
            }
        }

        class SaveOrderTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NonSched[] array = nonSchedList.toArray(new NonSched[0]);
                for (int i = 0 ; i < array.length ; i++) {
                    NonSched nonSched = array[i];
                    int iprio = i;
                    if (iprio != nonSched.getIprio()) {
                        nonSched.setIprio(iprio);
                        nonSchedHelper.update(nonSched);
                    }
                }
                return null;
            }
        }
    }
}