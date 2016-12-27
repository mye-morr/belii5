package com.better_computer.habitaid;

import android.app.Application;

import com.better_computer.habitaid.util.DynaArray;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.stetho.Stetho;

/**
 * Created by tedwei on 11/29/16.
 */

public class MyApplication extends Application {

    private CallbackManager callbackManager;
    private DynaArray dynaArray = new DynaArray();

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    public DynaArray getDynaArray() {
        return dynaArray;
    }


    public DynaArray resetDynaArray() {
        dynaArray = new DynaArray();
        return dynaArray;
    }

}
