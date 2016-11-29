package com.better_computer.habitaid;

import android.app.Application;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by tedwei on 11/29/16.
 */

public class MyApplication extends Application {

    private CallbackManager callbackManager;

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }
}
