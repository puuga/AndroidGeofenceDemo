package com.puuga.androidgeofencedemo.util;

/**
 * Created by siwaweswongcharoen on 2/23/2017 AD.
 */

public class Constants {
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 48;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
}
