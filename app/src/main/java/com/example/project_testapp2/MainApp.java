package com.example.project_testapp2;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class MainApp extends Application {

    private long timeMillis = -1;
    //private ArrayList<HashMap<HashMap<Double, Double>, Long>> latestLocations;

    public ArrayList<CovidEntry> latestLocations;
    /*
    Each entry consists of:
    (CovidTest: Time in millis, (Latitude: Float, Longitude: Float))

        [
            (177734313, (25.123124, 54.1235344)),
            (154723412, (25.123342, 54.1231123)),
            (124535435, (25.123342, 54.1231123)),
            (168796852, (25.132134, 55.3245132)),
        ]

        If any one the entries contain True send COVID alert;

    */

    public void setTimeMillis(long timeMillis){
        this.timeMillis = timeMillis;
    }

    public long getTimeMillis(){
        return this.timeMillis;
    }

    public void setLatestLocations(ArrayList<CovidEntry> latestLocations){
        this.latestLocations = latestLocations;
    }
    /*public void setLatestLocations(ArrayList<HashMap<HashMap<Double, Double>, java.lang.Long>> latestLocations){
        this.latestLocations = latestLocations;
    }*/

    public ArrayList<CovidEntry> getLatestLocations(){
        return latestLocations;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application", "Started COVID Tracing Application");
    }
}
