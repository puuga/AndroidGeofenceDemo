package com.puuga.androidgeofencedemo;

import android.app.Application;

import com.puuga.androidgeofencedemo.util.Contextor;

/**
 * Created by siwaweswongcharoen on 2/23/2017 AD.
 */

public class AndroidGeoFenceDemo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Contextor.getInstance().init(getApplicationContext());
    }
}
