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

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Luca Rossi on 05/07/2017.
 */

public class EasyLocationManager implements LocationListener {

    protected static final String TAG = EasyLocationManager.class.getSimpleName();

    private static final int BETTER_LOCATION_MIN_TIME = 1000 * 60 * 2;
    private static final int MIN_TIME = 1000 * 15;
    private static final int MIN_DISTANCE = 50;

    private long mMinTime = MIN_TIME;
    private float mMinDistance = MIN_DISTANCE;

    private OnLocationChangedListener mOnLocationChangedListener;
    private Location mLastKnownLocation = null;
    private LocationManager mLocationManager;

    public EasyLocationManager(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, getLastKnownLocation())) {
            mLastKnownLocation = location;
            if (mOnLocationChangedListener != null) {
                mOnLocationChangedListener.onLocationChanged(mLastKnownLocation);
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void setMinTime(long minTime) {
        mMinTime = minTime;
    }

    public void setMinDistance(float minDistance) {
        mMinDistance = minDistance;
    }

    public void setOnLocationChangedListener(OnLocationChangedListener listener) {
        mOnLocationChangedListener = listener;
    }

    @SuppressWarnings({"MissingPermission"})
    public void requestLocationUpdates() {
        if (mLocationManager != null) {
            if (mLocationManager.getProviders(true).contains(LocationManager.NETWORK_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mMinTime, mMinDistance, this);
            } else if (mLocationManager.getProviders(true).contains(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mMinTime, mMinDistance, this);
            }
        }
    }

    @SuppressWarnings({"MissingPermission"})
    public void removeLocationUpdates() {
        if (mLocationManager != null)
            mLocationManager.removeUpdates(this);
    }

    @SuppressWarnings({"MissingPermission"})
    public Location getLastKnownLocation() {
        initLastKnownLocation();
        return mLastKnownLocation;
    }

    protected void initLastKnownLocation(){
        if (mLocationManager != null && mLastKnownLocation == null) {
            if (mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            //If the location is still null we check the gps
            else if (mLastKnownLocation == null && mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param newLocation            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    private boolean isBetterLocation(Location newLocation, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > BETTER_LOCATION_MIN_TIME;
        boolean isSignificantlyOlder = timeDelta < -BETTER_LOCATION_MIN_TIME;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public interface OnLocationChangedListener {
        void onLocationChanged(Location location);
    }
}
