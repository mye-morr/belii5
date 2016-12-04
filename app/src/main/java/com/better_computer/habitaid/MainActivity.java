package com.better_computer.habitaid;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.ContactItemHelper;
import com.better_computer.habitaid.form.history.HistoryPopulator;
import com.better_computer.habitaid.navigation.DrawerAdapter;
import com.better_computer.habitaid.navigation.DrawerItem;
import com.better_computer.habitaid.scheduler.SchedulerService;

public class MainActivity extends AppCompatActivity {

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

    //populators
    private HistoryPopulator historyPopulator;

    public String sSelectedLibraryCat = "";
    public String sSelectedLibrarySubcat = "";
    public String sSelectedPlayerCat = "";
    public String sSelectedPlayerSubcat = "";
    private int iLastPosition = 0;

    public HistoryPopulator getHistoryPopulator() {
        return historyPopulator;
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

        DatabaseHelper.getInstance().getHelper(ContactItemHelper.class).fetchAndUpdate();

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navigation_drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerItem[] drawerItem = new DrawerItem[9];
        drawerItem[0] = new DrawerItem(R.string.title_section_events, R.drawable.drawer_schedule);
        drawerItem[1] = new DrawerItem(R.string.title_section_contacts, R.drawable.drawer_schedule);
        drawerItem[2] = new DrawerItem(R.string.title_section_history, R.drawable.drawer_history);
        drawerItem[3] = new DrawerItem(R.string.title_section_fb_integrate, R.drawable.drawer_setting);
        drawerItem[4] = new DrawerItem(R.string.title_section_games, R.drawable.drawer_setting);
        drawerItem[5] = new DrawerItem(R.string.title_section_library, R.drawable.drawer_setting);
        drawerItem[6] = new DrawerItem(R.string.title_section_new_player, R.drawable.drawer_setting);
        drawerItem[7] = new DrawerItem(R.string.title_section_player, R.drawable.drawer_setting);
        drawerItem[8] = new DrawerItem(R.string.title_section_ontrack, R.drawable.drawer_schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerAdapter adapter = new DrawerAdapter(this, R.layout.fragment_navigation_drawer_item, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            iLastPosition = position;
            selectItem(position);
        }
    }

    public void resetup() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        ((AbstractBaseFragment)fragment).refresh();
    }

    private void selectItem(int position) {

        AbstractBaseFragment fragment = null;

        switch (position) {
            case 0:
                fragment = new FragmentEvents();
                break;
            case 1:
                fragment = new FragmentContacts();
                break;
            case 2:
                fragment = new FragmentHistory();
                break;
            case 3:
                fragment = new FragmentFbIntegrate();
                break;
            case 4:
                fragment = new FragmentGames();
                break;
            case 5:
                fragment = new FragmentLibrary();
                break;
            case 6:
                fragment = new FragmentNewPlayer();
                break;
            case 7:
                fragment = new FragmentPlayer();
                break;
            case 8:
                fragment = new FragmentOnTrack();
                break;
            default:
                break;
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.action_refresh:
//                schedulePopulator.resetup();
//                break;
//            case R.id.action_schedule_new_events:
//                schedulePopulator.setupNew("events");
//                break;
//            case R.id.action_schedule_new_contacts:
//                schedulePopulator.setupNew("contacts");
//                break;
//            case R.id.action_clear_history:
//                historyPopulator.setupClearHistory();
//                break;
//            case R.id.action_clear_games:
//                schedulePopulator.setupClearGames();
//                break;
//            case R.id.action_library_new:
//                schedulePopulator.setupNew("library");
//                break;
//            case R.id.action_schedule_new_ontrack:
//                schedulePopulator.setupNew("ontrack");
//                break;
//            case R.id.action_settings:
//                // startActivityForResult(new Intent(getApplicationContext(), SettingsActivity.class), SETTING_RESULT);
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
