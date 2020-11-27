package com.example.project_testapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link thirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class thirdFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CompoundButton covidStatus_toggle;
    private TextView covidStatus_tv;
    private SharedPreferences sp_login;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public thirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment firstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static thirdFragment newInstance(String param1, String param2) {
        thirdFragment fragment = new thirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root =  inflater.inflate(R.layout.fragment_third, container, false);
        //final TextView textView = root.findViewById(R.id.active_today_label);
        //textView.setClickable(true);
        sp_login = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        Log.d("Shared Prefs in fragment", sp_login.getString("username", null));
        covidStatus_toggle = (CompoundButton) root.findViewById(R.id.covidStatus_toggle);
        covidStatus_tv = (TextView) root.findViewById(R.id.covidStatus_tv);

        final Runnable runnable = new Runnable() {
            @Override public void run() {
                // Replace with your logic.
                covidStatus_toggle.setEnabled(true);
            }
        };

        final DocumentReference docRef = db.collection("locations").document(sp_login.getString("username", ""));

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("Valid Location doc", document.getId() + ", Status: " + document.getBoolean("covidStatus"));
                    if (document.exists()) {
                        Log.d("Success", "DocumentSnapshot data: " + document.getData());
                        //Toast.makeText(loginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                        covidStatus_toggle.setChecked(document.getBoolean("covidStatus"));
                    }
                    else {
                        Toast.makeText(getContext(), "Logging in...", Toast.LENGTH_SHORT).show();;
                    }
                } else {
                    Log.d("Error", "Failed with ", task.getException());
                }
            }
        });
        //set Button based on current covid status on db

        covidStatus_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final DocumentReference docRef = db.collection("locations").document(sp_login.getString("username", ""));

                if (covidStatus_toggle.isChecked()) {
                    //update firestore locations record set covidStatus field : True
                    Log.d("ToggleButton", "ON");
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.d("Valid Location doc", document.getId() + ", Status: " + document.getBoolean("covidStatus"));
                                if (document.exists()) {
                                    Log.d("Success", "DocumentSnapshot data: " + document.getData());
                                    //Toast.makeText(loginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                                    docRef.update("covidStatus", true);

                                }
                                else {

                                }
                            } else {
                                Log.d("Error", "Failed with ", task.getException());
                            }
                        }
                    });

                }
                else {
                    //update firestore locations record set covidStatus field : False
                    Log.d("ToggleButton", "OFF");

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.d("Valid Location doc", document.getId() + ", Status: " + document.getBoolean("covidStatus"));
                                if (document.exists()) {
                                    Log.d("Success", "DocumentSnapshot data: " + document.getData());
                                    //Toast.makeText(loginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                                    docRef.update("covidStatus", false);

                                }
                                else {

                                }
                            } else {
                                Log.d("Error", "Failed with ", task.getException());
                            }
                        }
                    });
                }
                covidStatus_toggle.setEnabled(false);
                covidStatus_toggle.postDelayed(runnable, 5000);
            }});



        //return inflater.inflate(R.layout.fragment_third, container, false);
        return root;
    }


}