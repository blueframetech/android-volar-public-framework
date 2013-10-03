package com.volarvideo.demoapp;

import android.app.Application;

import com.testflightapp.lib.TestFlight;

public class VolarApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TestFlight.takeOff(this, "4f0c4b57-aeeb-455b-8cd8-331c1c080341");
    }
}
