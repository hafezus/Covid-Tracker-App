package com.example.project_testapp2;

import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class loginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button loginBtn;
    private SharedPreferences loginInfo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        loginBtn = (Button) findViewById(R.id.btn_login);
        loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_login){
                    //Toast.makeText(getApplicationContext(),"Inner Anonymous class implementation",Toast.LENGTH_SHORT).show();
                    validateUser();
                }
            }
        });
        //validateUser();

    }

    Boolean validateUser(){
        //SharedPreferences.Editor editor = savedText.edit();

        if(!username.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
            DocumentReference docRef = db.collection("users").document(username.getText().toString());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Success", "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d("Failed", "No such document");
                        }
                    } else {
                        Log.d("Error", "get failed with ", task.getException());
                    }
                }
            });
        }

        return false;
    }
}