package com.example.project_testapp2;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class MainApp extends Application {

    private long timeMillis = -1;
    private ArrayList<HashMap<HashMap<Float, Float>, Boolean>> latestLocations;

    /*
    Each entry consists of:
    (CovidTest: Boolean, (Latitude: Float, Longitude: Float))

        [
            (False, (25.123124, 54.1235344)),
            (False, (25.123342, 54.1231123)),
            (False, (25.123342, 54.1231123)),
            (False, (25.132134, 55.3245132)),
        ]

        If any one the entries contain True send COVID alert;

    */

    public void setTimeMillis(long timeMillis){
        this.timeMillis = timeMillis;
    }

    private long getTimeMillis(){
        return this.timeMillis;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Application", "Started COVID Tracing Application");
    }
}
