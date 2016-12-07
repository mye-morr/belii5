package com.better_computer.habitaid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.util.StopwatchUtil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentFbIntegrate extends AbstractBaseFragment {

    protected ScheduleHelper scheduleHelper;
    private Handler uiHander;

    private TextView stopwatchView;

    @Override
    public void refresh() {
    }

    public FragmentFbIntegrate() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fb_integrate, container, false);

        stopwatchView = (TextView) rootView.findViewById(R.id.stopwatch);

        rootView.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopwatchUtil.resetStopwatchStartTime(context);
                if (uiHander == null) {
                    uiHander = new Handler();
                }
                uiHander.post(updateStopwatchRunnable);
                stopwatchView.setText("");
            }
        });

        rootView.findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uiHander = null;
                if (StopwatchUtil.getStopwatchStopTime(context) < 0) {
                    StopwatchUtil.setStopwatchStopTime(context, System.currentTimeMillis());

                    long passedTime = StopwatchUtil.getStopwatchPassedTime(context);
                    long passedSeconds = passedTime / 1000;
                    String strPassedTime = String.format("%d:%02d", passedSeconds / 60, passedSeconds % 60);
                    stopwatchView.setText(strPassedTime);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.scheduleHelper = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CallbackManager callbackManager = ((com.better_computer.habitaid.MyApplication)getActivity().getApplication()).getCallbackManager();
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (StopwatchUtil.getStopwatchStopTime(context) < 0) {
            // is running
            if (uiHander == null) {
                uiHander = new Handler();
            }
            uiHander.post(updateStopwatchRunnable);
        } else {
            updateStopwatchRunnable.run();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHander = null;
    }

    private Runnable updateStopwatchRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                /* we don't want to see the elapsed time until end
                long passedTime = StopwatchUtil.getStopwatchPassedTime(context);
                long passedSeconds = passedTime / 1000;
                String strPassedTime = String.format("%d:%02d", passedSeconds / 60, passedSeconds % 60);
                stopwatchView.setText(strPassedTime);
                */
            } finally {
                if (uiHander != null && StopwatchUtil.getStopwatchStopTime(context) < 0) {
                    uiHander.postDelayed(updateStopwatchRunnable, 1000L);
                }
            }
        }
    };
}