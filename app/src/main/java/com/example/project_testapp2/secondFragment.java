package com.example.project_testapp2;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class secondFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleMap mMap;
    private MainApp app;
    public List geofenceList = new ArrayList();
    private GeofencingClient geofencingClient;
    private static final float Geofence_Radius = 1000;
    private Marker geoFence;
    private PendingIntent geoFencePendingIntent;
    private Circle geoFenceLimits;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private SharedPreferences sp_login;
    private GeofenceHelper geofenceHelper;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;



    private PendingIntent createGeofencePendingIntent() {
        Log.d("Geofence", "createGeofencePendingIntent");
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Intent intent = new Intent(getContext(), TracingService.class);
        return PendingIntent.getService(
                getContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public secondFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        geofencingClient = LocationServices.getGeofencingClient(getContext());
        mGeofencePendingIntent = null;
        googleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000 * 60 * 60)
                .setFastestInterval(1000 * 60 * 60);
        geofenceHelper = new GeofenceHelper(this.getContext());



        sp_login = getActivity().getSharedPreferences("loginInfo", MODE_PRIVATE);

        //Boolean val = getArguments().getBoolean("covidStatus");
        ;

        // Inflate the layout for this fragment
        app = (MainApp) getActivity().getApplication();
        final View root = inflater.inflate(R.layout.fragment_second, container, false);

        //SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        //Log.d("Covid Status passed",savedInstanceState.get("covidStatus") + "");

        Bundle args = this.getArguments();
        if (args != null) {
            Log.d("BundleVals", args.getBoolean("covidStatus") + "");
        }
        //Log.d("BundleVals", savedInstanceState + "");
        return root;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final LatLng DUBAI = new LatLng(25.2048, 55.2708);

        if (app != null) {

            for (CovidEntry i : app.getLatestLocations()) {

                Log.d("Marker", i.getLng() + ", " + i.getLng());
                final String tempID = i.toString();

                geofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId(tempID)
                        .setCircularRegion(
                                i.getLat(),
                                i.getLng(),
                                Geofence_Radius
                        )
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
                addCircle(new LatLng(i.getLat(), i.getLng()), Geofence_Radius);
                addGeofence(new LatLng(i.getLat(), i.getLng()), Geofence_Radius);


                /*Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, new LatLng(i.getLat(), i.getLng()), Geofence_Radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
                GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
                PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(i.getLat(), i.getLng()))
                        .strokeColor(Color.argb(50, 0, 0, 128))
                        .fillColor(Color.argb(100, 135, 206, 250))
                        .radius(Geofence_Radius);
                geoFenceLimits = mMap.addCircle(circleOptions);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "onSuccess: Geofence Added...");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String errorMessage = geofenceHelper.getErrorString(e);
                                Log.d("TAG", "onFailure: " + errorMessage);
                            }
                        });


                GeofencingRequest request = new GeofencingRequest.Builder()
                        // Notification to trigger when the Geofence is created
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofence(new Geofence.Builder()
                                // Set the request ID of the geofence. This is a string to identify this
                                // geofence.
                                .setRequestId(tempID)
                                .setCircularRegion(
                                        i.getLat(),
                                        i.getLng(),
                                        100000
                                )
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                        Geofence.GEOFENCE_TRANSITION_EXIT)
                                .build())  // add a Geofence
                        .build();*/


            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DUBAI, 10.5f));
        }
    }
    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    private Marker geoFenceMarker;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                //coordinatesTextView.setText(location.getLatitude() + "|" + location.getLongitude());
                Log.d("TS", "" + location.getLatitude() + "|" + location.getLongitude());
            }
            //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (SecurityException s) {
            Log.d("TS", "Not able to run location services...");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 123)
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                onConnected(new Bundle());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Put code to run if connection fails here
        // ex. print out an error message !
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onPause() {
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        }
        super.onPause();
    }

    public void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d("TAG", "onFailure: " + errorMessage);
                    }
                });
    }

}
