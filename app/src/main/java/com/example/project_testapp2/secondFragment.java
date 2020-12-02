package com.example.project_testapp2;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


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

    public secondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        app = (MainApp) getActivity().getApplication();
        final View root =  inflater.inflate(R.layout.fragment_second, container, false);
        //final TextView textView = root.findViewById(R.id.textView3);
        //textView.setClickable(true);
        //final Map
        /*textView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.navigateToFirst);
            }
        });*/

        //.setOnClickListener(Navigation.findNavController(root).navigate(R.id.navigateToSecond));
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

            for (CovidEntry i :app.getLatestLocations()) {
                LatLng loc = new LatLng(i.getLat(), i.getLng());
                mMap.addMarker(new MarkerOptions().position(loc).title("Lat: " + i.getLat() + "\nLong: " + i.getLng()));
                //mMap.addMarker(new MarkerOptions().position(loc).title("Marker in UAE"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(i.getLat(), i.getLng())));
                Log.d("Marker", i.getLng() + ", " + i.getLng());

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
}