package com.better_computer.habitaid;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.ContactItemHelper;
import com.better_computer.habitaid.form.history.HistoryPopulator;
import com.better_computer.habitaid.form.schedule.SchedulePopulator;
import com.better_computer.habitaid.navigation.DrawerFragment;
import com.better_computer.habitaid.scheduler.SchedulerService;
import com.facebook.CallbackManager;

public class MainActivity extends ActionBarActivity
        implements DrawerFragment.NavigationDrawerCallbacks {

    public static final int PAGE_EVENTS = 1;
    public static final int PAGE_CONTACTS = 2;
    public static final int PAGE_HISTORY = 3;
    public static final int PAGE_FB_INTEGRATE = 4;
    public static final int PAGE_GAMES = 5;
    public static final int PAGE_LIBRARY = 6;
    public static final int PAGE_NEW_PLAYER = 7;
    public static final int PAGE_PLAYER = 8;
    public static final int PAGE_ONTRACK = 9;

    public static final int PAGE_SETTING = 10;
    public static final int SETTING_RESULT = 2;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private DrawerFragment mDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int mIcon;
    private Integer menuResource;

    //populators
    private HistoryPopulator historyPopulator;
    private SchedulePopulator schedulePopulator;

    public String sSelectedLibraryCat = "";
    public String sSelectedLibrarySubcat = "";
    public String sSelectedPlayerCat = "";
    public String sSelectedPlayerSubcat = "";

    public HistoryPopulator getHistoryPopulator() {
        return historyPopulator;
    }

    public SchedulePopulator getSchedulePopulator() {
        return schedulePopulator;
    }

    public DrawerFragment getmDrawerFragment() {
        return mDrawerFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize database helper
        DatabaseHelper.init(getApplicationContext());

        //service init
        if (!isServiceRunning(SchedulerService.class)) {
            startService(new Intent(this, SchedulerService.class));
        }

        historyPopulator = new HistoryPopulator(this);
        schedulePopulator = new SchedulePopulator(this);

        mDrawerFragment = (DrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        DatabaseHelper.getInstance().getHelper(ContactItemHelper.class).fetchAndUpdate();

        String messageId = getIntent().getStringExtra("message_id");
        if (messageId != null) {
            //@todo open message
            loadPage(PAGE_HISTORY, true);
        } else {
            loadPage(PAGE_LIBRARY, true);
        }
    }

    int currentPage = -1;

    @Override
    protected void onStart() {
        super.onStart();

        if (currentPage == -1) {
            loadPage(PAGE_LIBRARY, true);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        loadPage(position + 1, false);
    }

    public void loadPage(int pageId, boolean manual) {
        currentPage = pageId;
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(pageId, this))
                .commit();
        if (manual) {
            onSectionAttached(pageId);
            supportInvalidateOptionsMenu();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {

            case PAGE_EVENTS:
                mTitle = getString(R.string.title_section_events);
                menuResource = R.menu.menu_events;
                break;

            case PAGE_CONTACTS:
                mTitle = getString(R.string.title_section_contacts);
                menuResource = R.menu.menu_contacts;
                break;

            case PAGE_HISTORY:
                mTitle = getString(R.string.title_section_history);
                menuResource = R.menu.menu_history;
                break;

            case PAGE_FB_INTEGRATE:
                mTitle = getString(R.string.title_section_fb_integrate);
                menuResource = R.menu.menu_blank;
                break;

            case PAGE_GAMES:
                mTitle = getString(R.string.title_section_games);
                menuResource = R.menu.menu_games;
                break;

            case PAGE_LIBRARY:
                mTitle = getString(R.string.title_section_library);
                menuResource = R.menu.menu_library;
                break;

            case PAGE_NEW_PLAYER:
                mTitle = getString(R.string.title_section_new_player);
                menuResource = R.menu.menu_blank;
                break;

            case PAGE_PLAYER:
                mTitle = getString(R.string.title_section_player);
                menuResource = R.menu.menu_blank;
                break;

            case PAGE_ONTRACK:
                mTitle = getString(R.string.title_section_ontrack);
                menuResource = R.menu.menu_ontrack;
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setIcon(mIcon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //if (!mDrawerFragment.isDrawerOpen()) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        if (menuResource != null) {
            getMenuInflater().inflate(menuResource, menu);
           /*     SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                switch (menuResource){
                    case R.menu.menu_history:
                        if(!settings.getBoolean("help_menu_history", false)) {
                            new ShowcaseView.Builder(this)
                                    .setTarget(new ActionItemTarget(this, R.id.action_clear_history))
                                    .setContentTitle("Clear message history")
                                    .setContentText("You can clear the message history archive instantly by clicking this Button")
                                    .hideOnTouchOutside()
                                    .build();
                            settings.edit().putBoolean("help_menu_history", true).commit();
                        }
                        break;
                    case R.menu.menu_schedule:
                        if(!settings.getBoolean("help_menu_schedule", false)) {
                            new ShowcaseView.Builder(this)
                                    .setTarget(new ActionItemTarget(this, R.id.action_schedule_new))
                                    .setContentTitle("Create new Schedule")
                                    .setContentText("You can create a new SMS Schedule by a wizard by clicking this Button.")
                                    .hideOnTouchOutside()
                                    .build();
                            settings.edit().putBoolean("help_menu_schedule", true).commit();
                        }
                        break;
                }*/
        }
        restoreActionBar();
        //  return true;
        //}
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_refresh:
                schedulePopulator.resetup();
                break;
            case R.id.action_schedule_new_events:
                schedulePopulator.setupNew("events");
                break;
            case R.id.action_schedule_new_contacts:
                schedulePopulator.setupNew("contacts");
                break;
            case R.id.action_clear_history:
                historyPopulator.setupClearHistory();
                break;
            case R.id.action_clear_games:
                schedulePopulator.setupClearGames();
                break;
            case R.id.action_library_new:
                schedulePopulator.setupNew("library");
                break;
            case R.id.action_schedule_new_ontrack:
                schedulePopulator.setupNew("ontrack");
                break;
            case R.id.action_settings:
                startSettingsActivity();
                currentPage = -1;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSettingsActivity() {
        startActivityForResult(new Intent(getApplicationContext(), SettingsActivity.class), SETTING_RESULT);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public Context context;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Context context) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.context = context;
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {

                case PAGE_EVENTS:
                    rootView = inflater.inflate(R.layout.fragment_schedule_events, container, false);
                    ((MainActivity) context).getSchedulePopulator().setup(rootView, "events");
                    break;
                case PAGE_CONTACTS:
                    rootView = inflater.inflate(R.layout.fragment_schedule_contacts, container, false);
                    ((MainActivity) context).getSchedulePopulator().setup(rootView, "contacts");
                    break;
                case PAGE_HISTORY:
                    rootView = inflater.inflate(R.layout.fragment_overview, container, false);
                    ((MainActivity) context).getHistoryPopulator().setup(rootView, "history");
                    break;
                case PAGE_FB_INTEGRATE:
                    rootView = inflater.inflate(R.layout.fragment_fb_integrate, container, false);
                    ((MainActivity) context).getSchedulePopulator().setup(rootView, "fb_integrate");
                    break;
                case PAGE_GAMES:
                    rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
                    ((MainActivity) context).getSchedulePopulator().setup(rootView, "games");
                    break;
                case PAGE_LIBRARY:
                    rootView = inflater.inflate(R.layout.fragment_schedule_library, container, false);
                    ((MainActivity) context).getSchedulePopulator().setup(rootView, "library");
                    break;
                case PAGE_NEW_PLAYER:
                    rootView = inflater.inflate(R.layout.fragment_schedule_new_player, container, false);
                    ((MainActivity) context).getSchedulePopulator().setup(rootView, "new_player");
                    break;
                case PAGE_PLAYER:
                    rootView = inflater.inflate(R.layout.fragment_schedule_old_player, container, false);
                    ((MainActivity) context).getSchedulePopulator().setup(rootView, "player");
                    break;
                case PAGE_ONTRACK:
                    rootView = inflater.inflate(R.layout.fragment_schedule_ontrack, container, false);
                    ((MainActivity) context).getSchedulePopulator().setup(rootView, "ontrack");
                    break;
                case PAGE_SETTING:
                    ((MainActivity)context).startSettingsActivity();
                    ((MainActivity)context).currentPage = -1;
                    break;
            }
            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            CallbackManager callbackManager = ((com.better_computer.habitaid.MyApplication)getActivity().getApplication()).getCallbackManager();
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
