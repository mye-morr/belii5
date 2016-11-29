package com.better_computer.habitaid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.os.Bundle;

/*
https://developer.android.com/training/permissions/requesting.html
 */
public class SplashActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST = 100;
    private static final String[] allRequestedPermissions = new String[] {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkAllRequestedPermissions()) {
            ActivityCompat.requestPermissions(this, allRequestedPermissions, MY_PERMISSIONS_REQUEST);
        } else {
            startMainActivity();
        }
    }

    private boolean checkAllRequestedPermissions() {
        for (String permission : allRequestedPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (checkAllRequestedPermissions()) {
                    startMainActivity();
                } else {
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
