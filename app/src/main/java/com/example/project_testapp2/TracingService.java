package com.example.project_testapp2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class TracingService extends Service {

    final static String SERVICE_LOG = "Covid Tracer Service";
    private MainApp app;
    private Timer timer;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private SharedPreferences sp_login;

    public TracingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.v(SERVICE_LOG, "Service bound - not used!");
        return null;
    }

    @Override
    public void onCreate(){
        Log.v(SERVICE_LOG, "Service created");
        app = (MainApp) getApplication();
        //sp_login = getSharedPreferences("loginInfo", MODE_PRIVATE);
        startTimer();
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
                    //Log.v(SERVICE_LOG, "Updated feed available.");
                    //cases += "\n" + newFeed;

                    // display notification
                    final long yourmilliseconds = System.currentTimeMillis();
                    //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                    //Date currentTime = new Date(yourmilliseconds);

                        /*
                        db.collection("locations").addSnapshotListener(new EventListener<QuerySnapshot>() { //Retrieve locations from locations->username->locations subcollection

                            //Need to filter locations and retrieve locations only within the past 72hours. So locations retrieved <= 72*24*3600 seconds
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.v(SERVICE_LOG, e.getMessage());
                                }

                                for (final DocumentChange doc1 : documentSnapshots.getDocumentChanges()) {
                                    if (doc1.getType() == DocumentChange.Type.ADDED && doc1.getDocument().getBoolean("covidStatus") == true) { //Now retrieving subcollections only of covid-positive locations
                                        Log.d("Get User Name (Documents)", doc1.getDocument().getId());
                                        doc1.getDocument().getReference().collection("locations").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d("", "Error : " + e.getMessage());
                                                }

                                                for (final DocumentChange doc2 : documentSnapshots.getDocumentChanges()) {
                                                    if (doc2.getType() == DocumentChange.Type.ADDED && (doc2.getDocument().getTimestamp("timestamp").getSeconds() <= System.currentTimeMillis() - 259200 * 1000)) { //259200 is 3 days

                                                        Log.d("Coordinates (Subcollection documents)", doc2.getDocument().getGeoPoint("location").toString() + ", Timestamp: " + doc2.getDocument().getTimestamp("timestamp").getSeconds()); //Retrieving all locations of covid-positive users
                                                        Log.d("Current time in millis", System.currentTimeMillis() + ""); //Retrieving all locations of covid-positive users

                                                        final CovidEntry tempCovidEntry
                                                                = new CovidEntry(
                                                                doc2.getDocument().getGeoPoint("location").getLatitude(),
                                                                doc2.getDocument().getGeoPoint("location").getLongitude(),
                                                                doc2.getDocument().getTimestamp("timestamp").getSeconds()
                                                        );

                                                        Log.d("HashMaps", tempCovidEntry.getLat() + " " + tempCovidEntry.getLng()
                                                                + " " + tempCovidEntry.getTimestamp());
                                                        try {
                                                            app.latestLocations.add(tempCovidEntry);
                                                            //tempArray.add(tempCovidEntry);
                                                            //Log.d("tempArray", tempArray.toString());
                                                            //app.setLatestLocations(tempArray);
                                                        } catch (Exception error) {
                                                            Log.d("ERROR", error.getMessage() + ", Nothing to add to array");
                                                        }
                                                        //app.latestLocations.;

                                                        //Get only locations where location.timestamp < 3 days
                                                        //1) Store this data in application context ArrayList variable
                                                    }
                                                }
                                            }
                                        });


                                    }
                                }

                            }
                        });
                         */

                    CollectionReference locationsRef = db.collection("Locations");
                    Query positiveCases = locationsRef.whereEqualTo("covidStatus", true);
                    //Log.d("positive cases", positiveCases +"");

                    positiveCases.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if((System.currentTimeMillis() - document.getTimestamp("timestamp").getSeconds()*1000) < 259200000){ //259200
                                        Log.d("Positive case", document.getString("username"));
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
                    Log.d(SERVICE_LOG, app.getLatestLocations() + "");
                } catch (NullPointerException e) {
                    Log.d("ERROR", "latestLocations is empty");
                }
            }


        };

        timer = new Timer(true);
        int delay = 1000;      // 1 second
        int interval = 5 * 1000;   // updates 5 seconds
        timer.schedule(task, delay, interval);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void sendNotification(/*String cases, */Date currentTime)
    {
        //Uri viewUri = Uri.parse("https://www.worldometers.info/coronavirus/");
        //Intent viewIntent = new Intent(Intent.ACTION_VIEW, viewUri);
        //startActivity(viewIntent);

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // create the intent for the notification

        // create the pending intent
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags);

        // create the variables for the notification

        int icon = R.drawable.ic_locations_icon; //CHANGE ICON
        CharSequence tickerText = "Nearby Case Traced!!";
        CharSequence contentTitle = currentTime.toString();
        CharSequence contentText = "You are currently in a COVID-prone area";

        NotificationChannel notificationChannel =
                new NotificationChannel("Channel_ID", "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);


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

    private void saveCase(/*String covidCase, */long timeinmillis)
    {

    } // end class saveContact

}
