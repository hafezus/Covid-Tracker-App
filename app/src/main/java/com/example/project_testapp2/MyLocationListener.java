package com.example.project_testapp2;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyLocationListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private SharedPreferences sp_login;
    private Context context;

    public MyLocationListener(GoogleApiClient googleApiClient, SharedPreferences sp_login, Context context){
        this.googleApiClient = googleApiClient;
        this.sp_login = sp_login;
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000*60*60)
                .setFastestInterval(1000*60*60);
        this.context =  context;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        /*if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);*/
        //Log.d("Here", "Reached");
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                //coordinatesTextView.setText(location.getLatitude() + "|" + location.getLongitude());
                Log.d("TS", "" + location.getLatitude() + "|" + location.getLongitude());
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
        }
        catch (SecurityException s){
            Log.d("TS","Not able to run location services...");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("LocationChanged", location.getLatitude() + "|" + location.getLongitude());
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference locationsRef = db.collection("Locations");
        DocumentReference newCityRef = db.collection("cities").document();
        final Map<String, Object> data = new HashMap<>();


        try {
            final DocumentReference docRef = db.collection("users").document(sp_login.getString("username", ""));

            final Boolean tempBool = false;
            final Location tempLoc = new Location(location);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("Valid Location doc", document.getId() + ", Status: " + document.getBoolean("covidStatus"));
                        if (document.exists()) {
                            Log.d("Success", "DocumentSnapshot data: " + document.getData());
                            data.put("covidStatus", document.getBoolean("covidStatus"));
                            data.put("location", new GeoPoint(tempLoc.getLatitude(), tempLoc.getLongitude()));
                            data.put("username", sp_login.getString("username", ""));
                            data.put("timestamp", new Timestamp(new Date(System.currentTimeMillis())));
                            Log.d("TransactionComplete", "Completed");
                            db.collection("Locations")
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("TAG", "Error adding document", e);
                                        }
                                    });

                        } else {
                            //Toast.makeText(getContext(), "Logging in...", Toast.LENGTH_SHORT).show();;
                        }
                    } else {
                        Log.d("Error", "Failed with ", task.getException());
                    }
                }
            });
        }
        catch(RuntimeException e){
            Log.d("Error Fetching docs from MyLocationListener", e.getLocalizedMessage());
        }

        Log.d("LocationData", data.toString());
    }

}
