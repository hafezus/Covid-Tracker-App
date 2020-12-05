package com.example.project_testapp2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.QueryListener;
import com.google.protobuf.NullValue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class TracingService extends Service implements LocationListener {

    final static String SERVICE_LOG = "Covid Tracer Service";
    private MainApp app;
    private Timer timer;
    private Timer timer2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private SharedPreferences sp_login;

    private static final float Geofence_Radius = 1000;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    private GeofenceHelper geofenceHelper;


    protected MyLocationListener locationListener;

    protected LocationManager locationManager;
    //GPSTracker mGPS = new GPSTracker(this);
    //private SharedPreferences sp_login;
    //double currentLat;
    //double currentLng;

    public TracingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.v(SERVICE_LOG, "Service bound - not used!");
        return null;
    }

    @Override
    public void onCreate() {
        Log.v(SERVICE_LOG, "Service created");
        app = (MainApp) getApplication();
        sp_login = getApplicationContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        locationListener = new MyLocationListener(googleApiClient, sp_login, getApplicationContext());
        googleApiClient = new GoogleApiClient.Builder(getApplication().getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this.locationListener)
                .addOnConnectionFailedListener(this.locationListener)
                .build();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000*60*60)
                .setFastestInterval(1000*60*60);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationListener = new MyLocationListener(googleApiClient, sp_login);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*10, 0, (LocationListener) locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60*30, 0, locationListener);
        //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) locationListener);
        //sp_login = getSharedPreferences("loginInfo", MODE_PRIVATE);
        //GeofencingClient geofencingClient = LocationServices.getGeofencingClient(getApplicationContext());
        geofenceHelper = new GeofenceHelper(getApplicationContext());
        startTimer();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(SERVICE_LOG, "Service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.v(SERVICE_LOG, "Service destroyed");
        stopTimer();
    }


    private void startTimer() {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                Log.v(SERVICE_LOG, "Timer task started");

                //io.downloadFile();
                //Log.v(SERVICE_LOG, "File downloaded");

                //final String newFeed = io.readFile();
                //Log.v(SERVICE_LOG, "File read");

                // if new feed is newer than old feed
                if (app != null) {
                    // display notification
                    final long yourmilliseconds = System.currentTimeMillis();

                    CollectionReference locationsRef = db.collection("Locations");
                    Query positiveCases = locationsRef.whereEqualTo("covidStatus", true);

                    positiveCases.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                app.latestLocations.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if ((System.currentTimeMillis() - document.getTimestamp("timestamp").getSeconds() * 1000) <= 259200000) { //259200 is 3 days in seconds
                                        Log.d("Positive case", document.getString("username"));

                                        //1,606,851,792,000
                                        //259,200,000
                                        //Log.d("Compare time of System.time vs. Firestore.getTimeStamp", System.currentTimeMillis() + ", " + document.getTimestamp("timestamp").getSeconds()*1000);
                                        app.latestLocations.add(new CovidEntry(
                                                document.getGeoPoint("location").getLatitude(),
                                                document.getGeoPoint("location").getLongitude(),
                                                document.getTimestamp("timestamp").getSeconds()
                                        ));
                                    }
                                }
                            } else {
                                Log.d("ERROR", "Error getting that document");
                            }
                        }
                    });
                }

                //3) Send current user's location every 30 minutes (create a function for this)

                //4) Retrieve current user's locations from last 3 days (create a function for this)

                //5) Compare each covid-positive location with every location of current user (Use nested loop)


                //Store into db here as well
                //sendNotification(/*newFeed,*/ currentTime);
                try {
                    for (CovidEntry entry : app.latestLocations) {
                        Log.d(SERVICE_LOG, entry.getLat() + ", " + entry.getLng() + ", " + entry.getTimestamp());
                    }
                } catch (NullPointerException e) {
                    Log.d("ERROR", "latestLocations is empty");
                }
            }


        };

        TimerTask task2 = new TimerTask() {

            @Override
            public void run() {
                Log.v(SERVICE_LOG, "Timer task started");
                if (app != null && googleApiClient.isConnected()) {
                    final long yourmilliseconds = System.currentTimeMillis();
                    /*CollectionReference locationsRef = db.collection("Locations");*/
                    //1) get current location
                    //2) send current location coordinates, timestamp, and COVID status
                    //3) check if current in COVID-positive range and send notification if true

                    sendNotification();
                }

                try {
                    for (CovidEntry entry : app.latestLocations) {
                        Log.d(SERVICE_LOG, entry.getLat() + ", " + entry.getLng() + ", " + entry.getTimestamp());
                    }
                } catch (NullPointerException e) {
                    Log.d("ERROR", "latestLocations is empty");
                }
            }


        };

        timer = new Timer(true);
        int delay = 1000;      // 1 second
        int interval = 5 * 1000;   // updates 5 seconds
        timer.schedule(task, delay, interval);

        timer2 = new Timer(true);
        int interval2 = 6*1000;   // updates 1 min
        timer2.schedule(task2, delay, interval2);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void sendNotification(/*String cases, Date currentTime*/)
    {
        //Uri viewUri = Uri.parse("https://www.worldometers.info/coronavirus/");
        //Intent viewIntent = new Intent(Intent.ACTION_VIEW, viewUri);
        //startActivity(viewIntent);

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // create the intent for the notification

        // create the pending intent
        //int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        /*PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags);*/

        // create the variables for the notification
        //geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        new secondFragment().addGeofence(new LatLng(0,0), 0);
        int icon = R.drawable.ic_locations_icon; //CHANGE ICON
        CharSequence tickerText = "COVID ALERT!";
        CharSequence contentTitle = "Joe Mama";
        CharSequence contentText = "You are currently in a COVID-prone area";

        NotificationChannel notificationChannel =
                new NotificationChannel("Channel_ID", "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);

        GeofenceHelper geofenceHelper = new GeofenceHelper(getApplicationContext());;
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        // create the notification and set its data
        Notification notification = new NotificationCompat
                .Builder(this, "Channel_ID")
                .setSmallIcon(icon)
                .setTicker(tickerText)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setChannelId("Channel_ID")
                .build();

        final int NOTIFICATION_ID = 1;

        startForeground(NOTIFICATION_ID, notification); //Also allow permission for foreground service in Manifest.xml
    }
    @Override
    public void onLocationChanged(Location location) {


    }

    private void saveCase(/*String covidCase, */long timeinmillis)
    {

    } // end class saveContact

}