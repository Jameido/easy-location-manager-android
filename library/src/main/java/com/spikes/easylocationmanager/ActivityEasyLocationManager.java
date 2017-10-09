/*
 * Copyright 2017.  Luca Rossi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.spikes.easylocationmanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by Luca Rossi on 05/07/2017.
 */

public class ActivityEasyLocationManager extends EasyLocationManager {

    private static final int PERMISSION_LOCATION = 100;
    int mPermissionCode = PERMISSION_LOCATION;
    private Activity mActivity;

    public ActivityEasyLocationManager(Activity activity) {
        super(activity);
        mActivity = activity;
    }

    @SuppressLint("NewApi")
    @Override
    public void requestLocationUpdates() {
        if (hasPermissionGranted(mActivity)) {
            super.requestLocationUpdates();
        } else if (shouldShowRequestPermissionRationale()) {
            showPermissionsRationaleAlertDialog();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

    @Override
    public Location getLastKnownLocation() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 &&
                PermissionsCompat.isPermissionGranted(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            return super.getLastKnownLocation();
        }
        return null;
    }

    /**
     * Call inside {@link Activity#onRequestPermissionsResult(int, String[], int[])} ()}
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode != mPermissionCode) {
            return;
        }

        requestLocationUpdates();
    }

    /**
     * Call inside {@link Activity#onDestroy()}
     */
    public void onDestroy() {
        removeLocationUpdates();
        mActivity = null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void requestPermissions(String[] permissions) {
        mActivity.requestPermissions(permissions, mPermissionCode);
    }

    public boolean shouldShowRequestPermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void showPermissionsRationaleAlertDialog() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.location_permissions_title)
                .setMessage(R.string.location_permissions_rationale)
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                .setNeutralButton(
                        R.string.location_permission_button_settings,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                openAppSettings();
                            }
                        }
                ).create()
                .show();
    }

    private void openAppSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + mActivity.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(myAppSettings);
    }
}
