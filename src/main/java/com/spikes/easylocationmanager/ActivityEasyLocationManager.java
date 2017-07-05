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
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca Rossi on 05/07/2017.
 */

public class ActivityEasyLocationManager extends EasyLocationManager {

    public static final int PERMISSION_LOCATION = 100;
    protected int mPermissionCode = PERMISSION_LOCATION;
    protected Activity mActivity;
    private CoordinatorLayout mCoordinatorLayout;

    private OnPermissionResult mOnPermissionResult = new OnPermissionResult() {
        @Override
        public void onPermissionsGranted() {
            requestLocationUpdates();
        }

        @Override
        public void onPermissionsDenied(String[] permissions) {
            checkPermissions();
        }
    };


    public ActivityEasyLocationManager(Activity activity) {
        super(activity);
        mActivity = activity;
    }

    @Override
    public void requestLocationUpdates() {
        if (checkPermissions()) {
            super.requestLocationUpdates();
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
        if (requestCode != mPermissionCode || null == mOnPermissionResult) {
            return;
        }
        List<String> permissionsDenied = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                permissionsDenied.add(permissions[i]);
            }
        }

        if (permissionsDenied.size() > 0) {
            checkPermissions();
            mOnPermissionResult.onPermissionsDenied(permissionsDenied.toArray(new String[permissionsDenied.size()]));
        } else {
            mOnPermissionResult.onPermissionsGranted();
        }
    }

    /**
     * Call inside {@link Activity#onDestroy()}
     */
    public void onDestroy() {
        removeLocationUpdates();
        mActivity = null;
    }

    public void setCoordinatorLayout(CoordinatorLayout coordinatorLayout) {
        mCoordinatorLayout = coordinatorLayout;
    }

    public void setOnPermissionResult(OnPermissionResult onPermissionResult) {
        mOnPermissionResult = onPermissionResult;
    }

    protected boolean checkPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!PermissionsCompat.isPermissionGranted(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (mActivity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showRationalePermissions();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                }
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void requestPermissions(String[] permissions) {
        mActivity.requestPermissions(permissions, mPermissionCode);
    }

    /**
     * If a coordinator layout is avaiable a snackbar is shown otherwise an alertdialog is shown
     */
    private void showRationalePermissions() {
        if (null != mCoordinatorLayout) {
            showRationalePermissionsSnackbar();
        } else {
            showRationalePermissionsAlertDialog();
        }
    }

    private void showRationalePermissionsSnackbar() {
        Snackbar snackbarRationale = Snackbar.make(mCoordinatorLayout, R.string.location_permissions_rationale, Snackbar.LENGTH_INDEFINITE);
        TextView tv = (TextView) snackbarRationale.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbarRationale.setAction(android.R.string.ok, new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
            }
        });
        snackbarRationale.setActionTextColor(ContextCompat.getColor(mActivity, android.R.color.white));
        snackbarRationale.show();
    }

    private void showRationalePermissionsAlertDialog() {

        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.location_permissions_title)
                .setMessage(R.string.location_permissions_rationale)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                    }
                })
                .create()
                .show();
    }

    public interface OnPermissionResult {
        void onPermissionsGranted();

        void onPermissionsDenied(String[] permissions);
    }
}
