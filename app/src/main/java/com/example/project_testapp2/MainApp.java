package com.example.project_testapp2;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class MainApp extends Application {

    private long timeMillis = -1;
    //private ArrayList<HashMap<HashMap<Double, Double>, Long>> latestLocations;

    public ArrayList<CovidEntry> latestLocations = new ArrayList<>();
    /*
    Each entry consists of:
    (CovidEntry: (Latitude: Double, Longitude: Double, Time in millis: Long)

        [
            (25.123124, 54.1235344, 1777343131),
            (25.199823, 55.4241321, 1606833814),
            (25.323424, 54.7564154, 1777343153),
            (25.313423, 54.4352436, 1777343143),
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
