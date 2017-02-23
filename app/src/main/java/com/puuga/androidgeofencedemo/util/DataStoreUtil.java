package com.puuga.androidgeofencedemo.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by siwaweswongcharoen on 2/23/2017 AD.
 */

public class DataStoreUtil {
    private static DataStoreUtil instance;

    public static DataStoreUtil getInstance() {
        if (instance == null)
            instance = new DataStoreUtil();
        return instance;
    }

    private Context mContext;

    private DataStoreUtil() {
        mContext = Contextor.getInstance().getContext();

        sharedPref = mContext.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    // key
    private final String tag = "DataStoreUtil";
    private final String SETTING_FILE = "SETTING_FILE";
    private final String KEY_LAT = "KEY_LAT";
    private final String KEY_LNG = "KEY_LNG";
    private final String KEY_RADUIS = "KEY_RADUIS";
    private final String KEY_GEOFENCES_ADDED = "KEY_GEOFENCES_ADDED";

    // SharedPreferences
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public double getLat() {
        return Double.parseDouble(sharedPref.getString(KEY_LAT, "0.0"));
    }

    public void setLat(double lat) {
        editor.putString(KEY_LAT, String.valueOf(lat));
        editor.commit();
    }

    public double getLng() {
        return Double.parseDouble(sharedPref.getString(KEY_LNG, "0.0"));
    }

    public void setLng(double lng) {
        editor.putString(KEY_LNG, String.valueOf(lng));
        editor.commit();
    }

    public float getRadius() {
        return Float.parseFloat(sharedPref.getString(KEY_RADUIS, "200"));
    }

    public void setRadius(float radius) {
        editor.putString(KEY_RADUIS, String.valueOf(radius));
        editor.commit();
    }

    public boolean getGeofencesAdded() {
        return sharedPref.getBoolean(KEY_GEOFENCES_ADDED, false);
    }

    public void setGeofencesAdded(boolean val) {
        editor.putBoolean(KEY_GEOFENCES_ADDED, val);
        editor.commit();
    }
}
