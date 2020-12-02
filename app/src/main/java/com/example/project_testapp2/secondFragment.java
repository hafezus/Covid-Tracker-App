package com.example.project_testapp2;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class secondFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleMap mMap;
    private MainApp app;
    private List geofenceList = new ArrayList();
    private GeofencingClient geofencingClient;
    private static final float Geofence_Radius = 1606;
    private Marker geoFence;
    private PendingIntent geoFencePendingIntent;

    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;

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


        // Inflate the layout for this fragment
        app = (MainApp) getActivity().getApplication();
        final View root = inflater.inflate(R.layout.fragment_second, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

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
                LatLng loc = new LatLng(i.getLat(), i.getLng());
                mMap.addMarker(new MarkerOptions().position(loc).title("Lat: " + i.getLat() + "\nLong: " + i.getLng()));
                //mMap.addMarker(new MarkerOptions().position(loc).title("Marker in UAE"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(i.getLat(), i.getLng())));
                Log.d("Marker", i.getLng() + ", " + i.getLng());
                final String tempID = i.toString();
                /*Geofence geofence = createGeofence( new LatLng(i.getLat(), i.getLng()), Geofence_Radius );
                GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
                addGeofence( geofenceRequest );*/

                //startGeofence();
                //markerForGeofence(new LatLng(i.getLat(), i.getLng()));
                geofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId(tempID)

                        .setCircularRegion(
                                i.getLat(),
                                i.getLng(),
                                Geofence_Radius
                        )
                        .setExpirationDuration(259200 * 1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());

            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DUBAI, 10.5f));



            /*mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View markerView = getLayoutInflater().inflate(R.layout.replace_this_with_text_to_display_info, null);
                    markerView.setLayoutParams(new RelativeLayout.LayoutParams(300, RelativeLayout.LayoutParams.WRAP_CONTENT));

                    return markerView;
                }
            });*/

        }
        //Log.d("Message","Reached this activity");
    }
//}



    private Marker geoFenceMarker;

    private void markerForGeofence(LatLng coords) {

        String title = coords.latitude + ", " + coords.longitude;

        MarkerOptions markerOptions = new MarkerOptions()
                .position(coords)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        if (mMap != null) {
            if (geoFenceMarker != null) {
                geoFenceMarker.remove();
            }
            geoFenceMarker = mMap.addMarker(markerOptions);

        }
        startGeofence();
    }

    private void startGeofence() {
        if (geoFenceMarker == null) {
            Geofence geofence = createGeofence(geoFenceMarker.getPosition(), Geofence_Radius);
            GeofencingRequest geofencingRequest = createGeofenceRequest(geofence);
            addGeofence(geofencingRequest);
        }
    }

    private GeofencingRequest createGeofenceRequest(Geofence geofence) {
        Log.d("Geofence", "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private void addGeofence(GeofencingRequest request) {
        Log.d("Geofence", "addGeofence");

        if (ActivityCompat.checkSelfPermission(app.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        LocationServices.getGeofencingClient(app.getApplicationContext()).addGeofences(
                request,
                createGeofencePendingIntent()
        );
    }

    private Geofence createGeofence( LatLng latLng, float radius ) {
        Log.d("Geofence", "createGeofence");
        return new Geofence.Builder()
                .setRequestId("X")
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( Geofence.NEVER_EXPIRE )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }
}
