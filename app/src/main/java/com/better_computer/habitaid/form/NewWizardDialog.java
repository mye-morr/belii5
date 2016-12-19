package com.better_computer.habitaid.form;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.better_computer.habitaid.MainActivity;
import com.better_computer.habitaid.R;
import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.ContactItem;
import com.better_computer.habitaid.data.core.ContactItemHelper;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.data.core.Schedule;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.form.schedule.ContactListAdapter;
import com.better_computer.habitaid.form.schedule.NonSchedListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class NewWizardDialog extends WizardDialog {

    private String[] repeatTypes;
    private String[] contactCategories;
    private String[] eventCategories;
    private String category;
    private Schedule schedule;
    private NonSched nonSched;
    private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    public void initialize() {
        super.initialize();
        setTitle("Add New");
        setIcon(R.drawable.wizard_icon);
    }

    public NewWizardDialog(Context context, String sCategory) {
        super(context);

        this.schedule = new Schedule();
        this.nonSched = new NonSched();
        this.category = sCategory;

        this.repeatTypes = context.getResources().getStringArray(R.array.repeat_list);
        this.contactCategories = context.getResources().getStringArray(R.array.contact_categories);
        this.eventCategories = context.getResources().getStringArray(R.array.event_categories);

        if (sCategory.equals("contacts")) {
            this.steps.add(new DialogStep_1Step_Contacts());
        }
        else if (sCategory.equals("library")) {
            this.steps.add(new DialogStep_1Step_Library());
        }
        else {
            this.steps.add(new DialogStep_1Step_Events());
        }
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public NewWizardDialog(Context context, NonSched nonSched) {
        this(context, "library");
        this.nonSched = nonSched;
    }

    public NewWizardDialog(Context context, Schedule schedule){
        this(context, schedule.getCategory());
        this.schedule = schedule;
    }

    public void updateContactTags(){
        LinearLayout parent = ((LinearLayout)dialog.findViewById(R.id.new_wizard_1step_receiver_list));

        String receiver = schedule.getReceiver();
        String receiverName = schedule.getReceiverName();

        parent.removeAllViews();
        View tagView = ((MainActivity)context).getLayoutInflater().inflate(R.layout.receiver_tag, null);
        ((TextView)tagView.findViewById(R.id.tag_display)).setText(receiverName);
        ((TextView)tagView.findViewById(R.id.tag_id)).setText(receiver);
        ((ImageButton)tagView.findViewById(R.id.tag_remove)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Schedule schedTemp = NewWizardDialog.this.getSchedule();
                schedTemp.setReceiver("");
                schedTemp.setReceiverName("");
                NewWizardDialog.this.updateContactTags();
            }
        });
        parent.addView(tagView);
    }

    public void updateContactList(String name){
        ContactItemHelper contactItemHelper = DatabaseHelper.getInstance().getHelper(ContactItemHelper.class);
        List<SearchEntry> keys = new ArrayList<SearchEntry>();

        if(name != null){
            name = "%" + name + "%";
        }
        else{
            name = "%";
        }
        keys.add(new SearchEntry(SearchEntry.Type.STRING, "name", SearchEntry.Search.LIKE, name));

        List<ContactItem> contactItems = (List<ContactItem>)(List<?>)contactItemHelper.find(keys);
        ((ListView) dialog.findViewById(R.id.new_wizard_1step_contact_listview)).setAdapter(new ContactListAdapter(context, contactItems));
    }

    public void updateComTasTags(){
        LinearLayout parent = ((LinearLayout)dialog.findViewById(R.id.new_wizard_1step_comTas_tags));
        parent.removeAllViews();

        String sComTas = schedule.getComTas();
        List<String> listComTas = Arrays.asList(sComTas.split(";"));

        for (int i = 0; i < listComTas.size(); i++) {
            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "comtas"));
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "name", SearchEntry.Search.EQUAL, listComTas.get(i)));

            final NonSched nsComTas = (NonSched) DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).get(keys);

            if(!(nsComTas == null)) {
                View tagView = ((MainActivity) context).getLayoutInflater().inflate(R.layout.receiver_tag, null);
                ((TextView) tagView.findViewById(R.id.tag_display)).setText(nsComTas.getName());
                ((TextView) tagView.findViewById(R.id.tag_id)).setText(nsComTas.get_id());
                ((ImageButton) tagView.findViewById(R.id.tag_remove)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NewWizardDialog.this.getSchedule().removeComTas(nsComTas.getName());
                        NewWizardDialog.this.updateComTasTags();
                    }
                });

                parent.addView(tagView);
            }
        }
    }

    public void updateComTasList(String name){
        NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
        List<SearchEntry> keys = new ArrayList<SearchEntry>();

        keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "comtas"));

        List<NonSched> listNsComTas = (List<NonSched>)(List<?>)nonSchedHelper.find(keys);
        ((ListView) dialog.findViewById(R.id.new_wizard_1step_comTas_listview)).setAdapter(new NonSchedListAdapter(context, listNsComTas));
    }

    class DialogStep_1Step_Library extends DialogStep {
        @Override
        public int getResource() {
            return R.layout.new_wizard_1step_library;
        }

        public void setup() {
            super.setup();

            final EditText etCategory = ((EditText)findViewById(R.id.stCategory));
            final EditText etSubCategory = ((EditText)findViewById(R.id.stSubCategory));
            final EditText etName = ((EditText)findViewById(R.id.stName));
            final EditText etContent = ((EditText)findViewById(R.id.stContent));

            etCategory.setText(nonSched.getCat());
            etSubCategory.setText(nonSched.getSubcat());
            etName.setText(nonSched.getName());
            etContent.setText(nonSched.getContent());

            setRightButton("Add", new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    String sCategory = ((EditText)findViewById(R.id.stCategory)).getText().toString();
                    String sSubCategory = ((EditText)findViewById(R.id.stSubCategory)).getText().toString();
                    String sName = ((EditText)findViewById(R.id.stName)).getText().toString();
                    String sContent = ((EditText)findViewById(R.id.stContent)).getText().toString();
                    String sBulkAdd = ((EditText)findViewById(R.id.stBulkAdd)).getText().toString();

                    if(sBulkAdd.length() > 0) {
                        String[] sxItems = sBulkAdd.split("\\n");
                        boolean bSuccess = true;

                        nonSched.setCat(sCategory);
                        nonSched.setSubcat(sSubCategory);
                        nonSched.setAbbrev("");

                        //!!! will add abbreviations later
                        for(int i=0; i<sxItems.length; i++) {
                            nonSched.setContent(sxItems[i]);

                            bSuccess =
                                    (bSuccess
                                        &&
                                        DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).createOrUpdate(nonSched)
                                    );
                        }

                        if (bSuccess) {
                            Toast.makeText(context, "Self-talk saved.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Self-talk saving failed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else {
                        nonSched.setCat(sCategory);
                        nonSched.setSubcat(sSubCategory);
                        nonSched.setName(sName);
                        nonSched.setContent(sContent);

                        ((MainActivity) (context)).sSelectedLibraryCat = sCategory;

                        //!!! need another solution for editing :-\ createAndShift(NonSched model)
                        if (DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).createOrUpdate(nonSched)) {
                            Toast.makeText(context, "Self-talk saved.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Self-talk saving failed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    ((MainActivity) context).resetup();

                    dismiss();
                }
            });

            setLeftButton("Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    class DialogStep_1Step_Contacts extends DialogStep {

        @Override
        public int getResource() {
            return R.layout.new_wizard_1step_contact;
        }

        public void setup() {
            super.setup();

            if(schedule.getReceiver().length() > 0) {
                updateContactTags();
            }

            final Spinner spinCat = ((Spinner)dialog.findViewById(R.id.contacts_category));

            String sSelectedContactsSubcat = ((MainActivity) context).sSelectedContactsSubcat;
            spinCat.setSelection(getSubCatSelected(contactCategories, sSelectedContactsSubcat));

            spinCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    schedule.setSubcategory(spinCat.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            final ListView listView = (ListView)findViewById(R.id.new_wizard_1step_contact_listview);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    ContactItem contactItem = (ContactItem)listView.getItemAtPosition(position);
                    EditText myEditText = (EditText) findViewById(R.id.new_wizard_1step_contact_phone);
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);

                    Schedule schedTemp = getSchedule();
                    schedTemp.setReceiver(contactItem.getPhone());
                    schedTemp.setReceiverName(contactItem.getName());
                    updateContactTags();
                }
            });

            ((EditText)dialog.findViewById(R.id.new_wizard_1step_contact_phone)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String phone = ((EditText) dialog.findViewById(R.id.new_wizard_1step_contact_phone)).getText().toString();
                    if (!phone.equals("")) {
                        updateContactList(phone);
                    }
                }
            });

            ((ImageButton)dialog.findViewById(R.id.new_wizard_1step_contact_phone_add)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = ((EditText) dialog.findViewById(R.id.new_wizard_1step_contact_phone)).getText().toString();
                    if (phone.equals("")) {
                        Toast.makeText(context, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                    } else {
                        ContactItem contactItem;
                        contactItem = (ContactItem) DatabaseHelper.getInstance().getHelper(ContactItemHelper.class).getBy("_id", phone);

                        if (contactItem == null) {
                            contactItem = new ContactItem(phone);
                        }

                        schedule.setReceiver(contactItem.getPhone());
                        schedule.setReceiverName(contactItem.getName(phone));
                        updateContactTags();
                        ((EditText) dialog.findViewById(R.id.new_wizard_1step_contact_phone)).setText("");
                    }
                    EditText myEditText = (EditText) findViewById(R.id.new_wizard_1step_contact_phone);
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
                }
            });

            ((ImageButton) dialog.findViewById(R.id.new_wizard_1step_contact_sendDate_btn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                            android.R.style.Theme_Holo_Dialog_NoActionBar, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            schedule.getNextDue().set(Calendar.DAY_OF_MONTH, day);
                            schedule.getNextDue().set(Calendar.MONTH, month);
                            schedule.getNextDue().set(Calendar.YEAR, year);

                            ((TextView) dialog.findViewById(R.id.new_wizard_1step_contact_sendDate)).setText(dateFormat.format(schedule.getNextDue().getTime()));
                        }
                    },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.setCancelable(false);
                    datePickerDialog.setTitle("Select a date");
                    datePickerDialog.show();
                }
            });

            ((TextView) dialog.findViewById(R.id.new_wizard_1step_contact_sendDate)).setText(dateFormat.format(schedule.getNextDue().getTime()));
            ((TextView) dialog.findViewById(R.id.new_wizard_1step_contact_message)).setText(schedule.getMessage());

            ((TimePicker)dialog.findViewById(R.id.new_wizard_1step_contact_time)).setCurrentHour(0);
            ((TimePicker)dialog.findViewById(R.id.new_wizard_1step_contact_time)).setCurrentMinute(0);
            ((NumberPicker)dialog.findViewById(R.id.new_wizard_1step_contact_numMin)).setMaxValue(200);

            setRightButton("Add", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    schedule.setCategory("contacts");
                    schedule.setSubcategory(spinCat.getSelectedItem().toString());

                    final EditText etMessage = ((EditText)dialog.findViewById(R.id.new_wizard_1step_contact_message));
                    schedule.setMessage(etMessage.getText().toString());

                    final NumberPicker numberPicker = (NumberPicker)dialog.findViewById(R.id.new_wizard_1step_contact_numMin);
                    final TimePicker timePicker = (TimePicker)dialog.findViewById(R.id.new_wizard_1step_contact_time);

                    if(numberPicker.getValue() > 0) {
                        Calendar instCal = Calendar.getInstance();
                        instCal.add(Calendar.MINUTE, numberPicker.getValue());

                        schedule.getNextDue().set(Calendar.HOUR_OF_DAY, instCal.get(Calendar.HOUR_OF_DAY));
                        schedule.getNextDue().set(Calendar.MINUTE, instCal.get(Calendar.MINUTE));
                    }
                    else {
                        schedule.getNextDue().set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                        schedule.getNextDue().set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    }

                    schedule.setRepeatEnable(String.valueOf(false));

                    schedule.setNextExecute(schedule.getNextDue());//set next execute as the schedule date
                    schedule.set_state("active");
                    schedule.set_frame("");

                    // returns boolean
                    if (DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).createOrUpdate(schedule)) {
                        Toast.makeText(context, "Schedule saved.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Schedule saving failed.", Toast.LENGTH_SHORT).show();
                    }

                    ((MainActivity) context).resetup();
                    dismiss();
                }
            });

            setLeftButton("Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

    }

    class DialogStep_1Step_Events extends DialogStep {

        @Override
        public int getResource() {
            return R.layout.new_wizard_1step_events;
        }

        public void setup() {
            super.setup();

            updateComTasList("");
            updateComTasTags();

            final ToggleButton btn_min_1 = ((ToggleButton) dialog.findViewById(R.id.new_wizard_1step_min_1));
            final ToggleButton btn_min_2 = ((ToggleButton) dialog.findViewById(R.id.new_wizard_1step_min_2));
            final ToggleButton btn_min_3 = ((ToggleButton) dialog.findViewById(R.id.new_wizard_1step_min_3));
            final ToggleButton btn_min_4 = ((ToggleButton) dialog.findViewById(R.id.new_wizard_1step_min_4));

            btn_min_1.setChecked(true);

            btn_min_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btn_min_1.isChecked()) {
                        btn_min_2.setChecked(false);
                        btn_min_3.setChecked(false);
                        btn_min_4.setChecked(false);
                    }
                    else {
                        btn_min_1.setChecked(true);
                    }
                }
            });

            btn_min_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btn_min_2.isChecked()) {
                        btn_min_1.setChecked(false);
                        btn_min_3.setChecked(false);
                        btn_min_4.setChecked(false);
                    }
                    else {
                        btn_min_2.setChecked(true);
                    }
                }
            });

            btn_min_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btn_min_3.isChecked()) {
                        btn_min_1.setChecked(false);
                        btn_min_2.setChecked(false);
                        btn_min_4.setChecked(false);
                    }
                    else {
                        btn_min_3.setChecked(true);
                    }
                }
            });

            btn_min_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btn_min_4.isChecked()) {
                        btn_min_1.setChecked(false);
                        btn_min_2.setChecked(false);
                        btn_min_3.setChecked(false);
                    }
                    else {
                        btn_min_4.setChecked(true);
                    }
                }
            });

            final Spinner spinCat = ((Spinner)dialog.findViewById(R.id.events_category));

            String sSelectedEventsSubcat = ((MainActivity) context).sSelectedEventsSubcat;
            spinCat.setSelection(getSubCatSelected(eventCategories, sSelectedEventsSubcat));

            spinCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    schedule.setSubcategory(spinCat.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            final ToggleButton btn_rep_5 = ((ToggleButton) dialog.findViewById(R.id.new_wizard_1step_btn_rep_5));
            final EditText etCustom = ((EditText)dialog.findViewById(R.id.new_wizard_1step_custom));

            btn_rep_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    etCustom.setText("");
                }
            });

            etCustom.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String sCustom = ((EditText) dialog.findViewById(R.id.new_wizard_1step_custom)).getText().toString();
                    if(sCustom.length() == 0){
                        btn_rep_5.setChecked(true);
                    }
                    else {
                        btn_rep_5.setChecked(false);
                    }
                }
            });

            final EditText etHours = ((EditText)dialog.findViewById(R.id.new_wizard_1step_hrs));
            etHours.setText("*12");

            final EditText etIncr = ((EditText)dialog.findViewById(R.id.new_wizard_1step_incr));
            etIncr.setText("0");

            final Button btnHrsPlus = ((Button) dialog.findViewById(R.id.new_wizard_1step_hrs_plus));
            final Button btnHrsMinus = ((Button) dialog.findViewById(R.id.new_wizard_1step_hrs_minus));

            btnHrsPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sOldHours = etHours.getText().toString();
                    if(sOldHours.charAt(0) == '*') {
                        int iOldHours = Integer.valueOf(sOldHours.substring(1));
                        if(iOldHours == 11) {
                            etHours.setText("12");
                        }
                        else if(iOldHours == 12) {
                            etHours.setText("*1");
                        }
                        else {
                            etHours.setText("*" + String.valueOf(iOldHours + 1));
                        }
                    }
                    else {
                        int iOldHours = Integer.valueOf(sOldHours);

                        if(iOldHours == 11) {
                            etHours.setText("*12");
                        }
                        else if(iOldHours == 12) {
                            etHours.setText("1");
                        }
                        else {
                            etHours.setText(String.valueOf(Integer.parseInt(sOldHours) + 1));
                        }
                    }
                }
            });

            btnHrsMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sOldHours = etHours.getText().toString();
                    if(sOldHours.charAt(0) == '*') {
                        int iOldHours = Integer.valueOf(sOldHours.substring(1));
                        if(iOldHours == 1) {
                            etHours.setText("*12");
                        }
                        else if(iOldHours == 12) {
                            etHours.setText("11");
                        }
                        else {
                            etHours.setText("*" + String.valueOf(iOldHours - 1));
                        }
                    }
                    else {
                        int iOldHours = Integer.valueOf(sOldHours);

                        if(iOldHours == 1) {
                            etHours.setText("12");
                        }
                        else if(iOldHours == 12) {
                            etHours.setText("*11");
                        }
                        else {
                            etHours.setText(String.valueOf(Integer.parseInt(sOldHours) - 1));
                        }
                    }
                }
            });

            final Button btnIncrPlus = ((Button) dialog.findViewById(R.id.new_wizard_1step_incr_plus));
            final Button btnIncrMinus = ((Button) dialog.findViewById(R.id.new_wizard_1step_incr_minus));

            btnIncrPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int iCurrent = Integer.parseInt(etIncr.getText().toString());
                    if(iCurrent == 0) {
                        etIncr.setText(String.valueOf(1));
                    }
                    else if (iCurrent > 30) {
                        etIncr.setText(String.valueOf(iCurrent + 20));
                    }
                    else {
                        etIncr.setText(String.valueOf(iCurrent * 2));
                    }
                }
            });

            btnIncrMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sIncr = etIncr.getText().toString();

                    if(!sIncr.equals("0")) {
                        etIncr.setText(String.valueOf(Integer.parseInt(sIncr) - 1));
                    }
                }
            });

            String sRemindInterval = schedule.getRemindInterval();
            if (sRemindInterval.equals("5")) {
                btn_rep_5.setChecked(true);
            }

            final CheckBox isRepeatEnabled = ((CheckBox)dialog.findViewById(R.id.new_wizard_1step_repeat_enable));
            boolean isProject = Boolean.valueOf(schedule.getRepeatEnable());
            isRepeatEnabled.setChecked(isProject);
            if(!isProject) {
                dialog.findViewById(R.id.new_wizard_1step_repeat_value).setEnabled(false);
                dialog.findViewById(R.id.new_wizard_1step_repeat_type).setEnabled(false);
                dialog.findViewById(R.id.new_wizard_1step_btn_done_1).setEnabled(false);
                dialog.findViewById(R.id.new_wizard_1step_btn_done_2).setEnabled(false);
                dialog.findViewById(R.id.new_wizard_1step_prep_window).setEnabled(false);
                dialog.findViewById(R.id.new_wizard_1step_prep_window_type).setEnabled(false);
            }

            isRepeatEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    dialog.findViewById(R.id.new_wizard_1step_repeat_value).setEnabled(b);
                    dialog.findViewById(R.id.new_wizard_1step_repeat_type).setEnabled(b);
                    dialog.findViewById(R.id.new_wizard_1step_btn_done_1).setEnabled(b);
                    dialog.findViewById(R.id.new_wizard_1step_btn_done_2).setEnabled(b);
                    dialog.findViewById(R.id.new_wizard_1step_prep_window).setEnabled(b);
                    dialog.findViewById(R.id.new_wizard_1step_prep_window_type).setEnabled(b);
                    schedule.setRepeatEnable(String.valueOf(b));
                }
            });

            int prepCount = Integer.parseInt(schedule.getPrepCount());
            final ToggleButton btn_done_1 = ((ToggleButton) dialog.findViewById(R.id.new_wizard_1step_btn_done_1));
            final ToggleButton btn_done_2 = ((ToggleButton) dialog.findViewById(R.id.new_wizard_1step_btn_done_2));

            if (prepCount == 1) {
                btn_done_1.setChecked(true);
            } else if (prepCount == 2) {
                btn_done_1.setChecked(true);
                btn_done_2.setChecked(true);
            }

            ((ImageButton) dialog.findViewById(R.id.new_wizard_1step_sendDate_btn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                            android.R.style.Theme_Holo_Dialog_NoActionBar, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                            schedule.getNextDue().set(Calendar.DAY_OF_MONTH, day);
                            schedule.getNextDue().set(Calendar.MONTH, month);
                            schedule.getNextDue().set(Calendar.YEAR, year);

                            ((TextView) dialog.findViewById(R.id.new_wizard_1step_sendDate)).setText(dateFormat.format(schedule.getNextDue().getTime()));
                        }
                    },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.setCancelable(false);
                    datePickerDialog.setTitle("Select a date");
                    datePickerDialog.show();
                }
            });

            final Spinner repeat_type = ((Spinner)dialog.findViewById(R.id.new_wizard_1step_repeat_type));
            repeat_type.setSelection(schedule.getRepeatTypeSelected(repeatTypes));
            repeat_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    schedule.setRepeatType(((Spinner) dialog.findViewById(R.id.new_wizard_1step_repeat_type)).getSelectedItem().toString().toUpperCase());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            ((EditText)dialog.findViewById(R.id.new_wizard_1step_repeat_value)).setText(schedule.getRepeatValue());

            final Spinner prep_window_type = ((Spinner)dialog.findViewById(R.id.new_wizard_1step_prep_window_type));
            prep_window_type.setSelection(schedule.getPrepWindowTypeSelected(repeatTypes));
            prep_window_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    schedule.setPrepWindowType(((Spinner) dialog.findViewById(R.id.new_wizard_1step_prep_window_type)).getSelectedItem().toString().toUpperCase());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            ((EditText)dialog.findViewById(R.id.new_wizard_1step_prep_window)).setText(schedule.getPrepWindow());

            ((TextView) dialog.findViewById(R.id.new_wizard_1step_message)).setText(schedule.getMessage());
            ((TextView) dialog.findViewById(R.id.new_wizard_1step_sendDate)).setText(dateFormat.format(schedule.getNextDue().getTime()));

            ((EditText)dialog.findViewById(R.id.new_wizard_1step_comTas)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String sComTas = ((EditText) dialog.findViewById(R.id.new_wizard_1step_comTas)).getText().toString();

                    // don't waste processing
                    if(sComTas.length() <= 5) {
                        updateComTasList(sComTas);
                    }
                }
            });

            ((ImageButton)dialog.findViewById(R.id.new_wizard_1step_comTas_add)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sComTas = ((EditText) dialog.findViewById(R.id.new_wizard_1step_comTas)).getText().toString();
                    if (sComTas.equals("")) {
                        Toast.makeText(context, "Please enter a comTas", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        int iBuf = sComTas.indexOf("-=");
                        String sName = sComTas.substring(0,iBuf).trim();
                        String sContent = sComTas.substring(iBuf + 2).trim();

                        NonSched  nsComTas = new NonSched();
                        nsComTas.setCat("comtas");
                        nsComTas.setName(sName);
                        nsComTas.setContent(sContent);

                        // returns boolean
                        if (DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).createOrUpdate(nsComTas)) {
                            ((EditText) dialog.findViewById(R.id.new_wizard_1step_comTas)).setText("");
                            updateComTasList("");

                            Toast.makeText(context, "Schedule saved.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Schedule saving failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            final ListView listViewComTas = (ListView)findViewById(R.id.new_wizard_1step_comTas_listview);

            listViewComTas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    final NonSched comTas = (NonSched) listViewComTas.getItemAtPosition(position);

                    AlertDialog.Builder alertOptions = new AlertDialog.Builder(context);
                    List<String> optsList = new ArrayList<String>();

                    optsList.add("Add To");
                    optsList.add("Delete");

                    final String[] options = optsList.toArray(new String[]{});
                    alertOptions.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, options), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Schedule schedTemp = getSchedule();

                            if (options[i].equalsIgnoreCase("ADD TO")) {
                                schedTemp.setComTas(schedTemp.getComTas() + comTas.getName() + ";");
                                updateComTasTags();
                                dialogInterface.dismiss();
                            }
                            else if (options[i].equalsIgnoreCase("DELETE")) {
                                Toast.makeText(context, "Schedule deleted.", Toast.LENGTH_SHORT).show();
                                DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).delete(comTas.get_id());
                                updateComTasList("");
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

            setRightButton("Add", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // recipient doesn't matter because
                    // these messages will only be broadcast
                    schedule.setReceiver("");
                    schedule.setReceiverName("");

                    schedule.setCategory(category);
                    schedule.setSubcategory(spinCat.getSelectedItem().toString());

                    final EditText etMessage = ((EditText)dialog.findViewById(R.id.new_wizard_1step_message));
                    schedule.setMessage(etMessage.getText().toString());

                    if(etHours.getText().toString().equalsIgnoreCase("*12")
                            && btn_min_1.isChecked()
                            && etIncr.getText().toString().equalsIgnoreCase("0")) {

                    }
                    else if(!(etIncr.getText().toString().equalsIgnoreCase("0"))) {
                        Calendar instCal = Calendar.getInstance();
                        instCal.add(Calendar.MINUTE, Integer.valueOf(etIncr.getText().toString()));

                        schedule.getNextDue().set(Calendar.HOUR_OF_DAY, instCal.get(Calendar.HOUR_OF_DAY));
                        schedule.getNextDue().set(Calendar.MINUTE, instCal.get(Calendar.MINUTE));
                    }
                    else {
                        String sHour = etHours.getText().toString();
                        int iHour = 0;

                        if(sHour.equalsIgnoreCase("12")) {
                            iHour = 0;
                        }
                        else if(sHour.charAt(0) == '*') {
                            if(sHour.equalsIgnoreCase("*12")) {
                                iHour = 12;
                            }
                            else {
                                iHour = 12 + Integer.valueOf(sHour.substring(1));
                            }
                        }
                        else {
                            iHour = Integer.valueOf(sHour);
                        }

                        schedule.getNextDue().set(Calendar.HOUR_OF_DAY, iHour);

                        Random rand = new Random();
                        int iMinute = rand.nextInt(15);

                        if(btn_min_2.isChecked()) {
                            iMinute += 15;
                        }
                        else if (btn_min_3.isChecked()) {
                            iMinute += 30;
                        }
                        else if (btn_min_4.isChecked()) {
                            iMinute += 45;
                        }

                        schedule.getNextDue().set(Calendar.MINUTE, iMinute);
                    }

                    String sRemindInterval = "";
                    if(btn_rep_5.isChecked()) {
                        sRemindInterval = "5";
                    }
                    else {
                        sRemindInterval = etCustom.getText().toString().trim();
                        if(sRemindInterval.length() == 0) {
                            sRemindInterval = "5";
                        }
                    }
                    schedule.setRemindInterval(sRemindInterval);

                    schedule.set_state("active");
                    String sPrepCount = schedule.getPrepCount();

                    if(btn_done_2.isChecked()) {
                        if(sPrepCount.equals("1")) { // did prepCount increase?
                            schedule.set_frame("inactive");
                            schedule.set_state("inactive");
                        }
                        sPrepCount = "2";
                    }
                    else if(btn_done_1.isChecked()) { // did prepCount increase?
                        if(sPrepCount.equals("0")) {
                            schedule.set_frame("inactive");
                            schedule.set_state("inactive");
                        }
                        sPrepCount = "1";
                    }
                    else {
                        sPrepCount = "0";
                    }

                    schedule.setPrepCount(sPrepCount);

                    //m/ temporary, until find a place in ui
                    schedule.setRepeatInflexible(String.valueOf(true));

                    final CheckBox isRepeatEnabled = ((CheckBox)dialog.findViewById(R.id.new_wizard_1step_repeat_enable));
                    final EditText sRepeatValue = ((EditText)dialog.findViewById(R.id.new_wizard_1step_repeat_value));
                    final Spinner optRepeatType = ((Spinner)dialog.findViewById(R.id.new_wizard_1step_repeat_type));

                    schedule.setRepeatEnable(String.valueOf(isRepeatEnabled.isChecked()));
                    schedule.setRepeatValue(sRepeatValue.getText().toString());
                    schedule.setRepeatType(optRepeatType.getSelectedItem().toString().toUpperCase());

                    if(isRepeatEnabled.isChecked()) {
                        schedule.set_frame("inactive");
                        schedule.set_state("inactive");
                    }
                    else {
                        schedule.set_frame("");
                        schedule.set_state("active");
                    }

                    final EditText sPrepWindow = ((EditText)dialog.findViewById(R.id.new_wizard_1step_prep_window));
                    final Spinner optPrepWindowType = ((Spinner)dialog.findViewById(R.id.new_wizard_1step_prep_window_type));

                    schedule.setPrepWindow(sPrepWindow.getText().toString());
                    schedule.setPrepWindowType(optPrepWindowType.getSelectedItem().toString().toUpperCase());

                    schedule.setNextExecute(schedule.getNextDue());

                    // returns boolean
                    if (DatabaseHelper.getInstance().getHelper(ScheduleHelper.class).createOrUpdate(schedule)) {
                        Toast.makeText(context, "Schedule saved.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Schedule saving failed.", Toast.LENGTH_SHORT).show();
                    }

                    ((MainActivity) context).resetup();

                    dismiss();

                }
            });

            setLeftButton("Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    public int getSubCatSelected(String[] subcat, String sMatch){
        for (int i = 0; i < subcat.length; i++) {
            if(subcat[i].equalsIgnoreCase(sMatch)){
                return i;
            }
        }
        return 0;
    }

}