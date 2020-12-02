package com.example.project_testapp2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            Log.d("Broadcast Receiver", "Error");
            return;
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();


        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            //String locId = triggeringGeofences.get(0).getRequestId();
            //sendNotification(locId, context);

            Intent serviceIntent = new Intent(context, TracingService.class);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(serviceIntent);
            }
            else{
                context.startService(serviceIntent);
            }

        }
        else{
            Log.d("Broadcast Receiver", "Error");
        }

    }

    private void sendNotification(String locId, Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.ic_results_icon)
                .setContentTitle("Entered Location")
                .setContentText(" you reached" + locId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());


    }
}
