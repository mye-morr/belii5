package com.better_computer.habitaid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class FragmentFbIntegrate extends AbstractBaseFragment {

    protected ScheduleHelper scheduleHelper;

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

}