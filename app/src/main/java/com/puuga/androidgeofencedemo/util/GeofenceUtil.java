package com.puuga.androidgeofencedemo.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.puuga.androidgeofencedemo.R;
import com.puuga.androidgeofencedemo.geofence.GeofenceErrorMessages;
import com.puuga.androidgeofencedemo.service.GeofenceTransitionsIntentService;

import java.util.ArrayList;

/**
 * Created by siwaweswongcharoen on 2/23/2017 AD.
 */

public class GeofenceUtil implements ResultCallback<Status> {
    protected static final String TAG = "GeofenceUtil";

    protected ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private boolean mGeofencesAdded;

    private double mLat;
    private double mLng;
    private float mRadius;
    private Context mContext;

    public GeofenceUtil(Context context) {
        mContext = context;
        mGeofenceList = new ArrayList<>();

        mGeofencesAdded = DataStoreUtil.getInstance().getGeofencesAdded();
    }

    public void startGeofencing(GoogleApiClient googleApiClient, double lat, double lng, float radius) {
        if (!googleApiClient.isConnected()) {
            Toast.makeText(mContext, mContext.getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        mLat = lat;
        mLng = lng;
        mRadius = radius;

        Log.d(TAG, "mLat: " + mLat);
        Log.d(TAG, "mLng: " + mLng);
        Log.d(TAG, "mRadius: " + mRadius);

        populateGeofenceList();

        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    public void stopGeofencing(GoogleApiClient googleApiClient) {
        if (!googleApiClient.isConnected()) {
            Toast.makeText(mContext, mContext.getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        mGeofenceList.clear();

        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    googleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private void populateGeofenceList() {
        Geofence geofence = new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("Target")

                // Set the circular region of this geofence.
                .setCircularRegion(mLat, mLng, mRadius)

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build();

        mGeofenceList.add(geofence);
        Log.d(TAG, "mGeofenceList: " + mGeofenceList.size());
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            mGeofencesAdded = !mGeofencesAdded;
            DataStoreUtil.getInstance().setGeofencesAdded(mGeofencesAdded);
            String result = mContext.getString(mGeofencesAdded ? R.string.geofences_added :
                    R.string.geofences_removed);

            Toast.makeText(
                    mContext,
                    result,
                    Toast.LENGTH_SHORT
            ).show();
            Log.d(TAG, result);


        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(mContext,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }
}
