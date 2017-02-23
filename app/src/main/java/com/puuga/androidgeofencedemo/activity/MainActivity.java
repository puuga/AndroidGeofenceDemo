package com.puuga.androidgeofencedemo.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.puuga.androidgeofencedemo.R;
import com.puuga.androidgeofencedemo.util.DataStoreUtil;
import com.puuga.androidgeofencedemo.util.GeofenceUtil;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = "MainActivity";
    protected static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 19;

    private EditText edtLat;
    private EditText edtLng;
    private EditText edtRadius;
    private Button btnStart;
    private Button btnStop;

    protected GoogleApiClient mGoogleApiClient;

    private GeofenceUtil mGeofenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();

        initInstance();
        loadRecentValue();

        mGeofenceUtil = new GeofenceUtil(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startGeofencing();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initInstance() {
        edtLat = (EditText) findViewById(R.id.edtLat);
        edtLng = (EditText) findViewById(R.id.edtLng);
        edtRadius = (EditText) findViewById(R.id.edtRadius);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    private void loadRecentValue() {
        edtLat.setText(String.valueOf(DataStoreUtil.getInstance().getLat()));
        edtLng.setText(String.valueOf(DataStoreUtil.getInstance().getLng()));
        edtRadius.setText(String.valueOf(DataStoreUtil.getInstance().getRadius()));
    }

    @Override
    public void onClick(View view) {
        if (view == btnStart) {
            startGeofencing();
        } else if (view == btnStop) {
            stopGeofencing();
        }
    }

    private void startGeofencing() {
        if (edtLat.getText().toString().equals("")
                || edtLng.getText().toString().equals("")
                || edtRadius.getText().toString().equals("")) {
            return;
        }

        double lat = Double.parseDouble(edtLat.getText().toString());
        double lng = Double.parseDouble(edtLng.getText().toString());
        float radius = Float.parseFloat(edtRadius.getText().toString());

        DataStoreUtil.getInstance().setLat(lat);
        DataStoreUtil.getInstance().setLng(lng);

        hideKeyboard();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        mGeofenceUtil.startGeofencing(mGoogleApiClient, lat, lng, radius);
    }

    private void stopGeofencing() {
        mGeofenceUtil.stopGeofencing(mGoogleApiClient);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }
}
