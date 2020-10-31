package com.example.project_testapp2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class loginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button loginBtn;
    private SharedPreferences sp_login;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.loading_splash);

        sp_login = getSharedPreferences("loginInfo", MODE_PRIVATE);
        //SharedPreferences.Editor editor = sp_login.edit();
        //editor.putString("username", "");
        //editor.putString("password", "");
        //editor.commit();
        checkUser(sp_login.getString("username",""), sp_login.getString("password",""));
        //validateUser();
    }
    public Boolean validateLogin() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //SharedPreferences.Editor editor = savedText.edit();
        if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            /*if(sp_login.getString("username","").equals("") && sp_login.getString("username","").equals("")){

            }*/
            DocumentReference docRef = db.collection("users").document(username.getText().toString());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Success", "DocumentSnapshot data: " + document.getData());
                            Toast.makeText(loginActivity.this, "Saving data...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(loginActivity.this, MainActivity.class);
                            intent.putExtra("username",document.getId());
                            intent.putExtra("password",document.getString("password"));

                            SharedPreferences.Editor editor = sp_login.edit();
                            editor.putString("username", document.getId());
                            editor.putString("password", document.getString("password"));
                            editor.commit();
                            loginActivity.this.startActivity(intent);;
                        } else {
                            Log.d("Failed", "Wrong Credentials");
                        }
                    } else {
                        Log.d("Error", "Failed with ", task.getException());
                    }
                }
            });
        }
        return false;
    }

    public void checkUser(final String username, final String password){
        if (!username.equals("") && !password.equals("")){
            DocumentReference docRef = db.collection("users").document(username);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && username.equals(document.getId()) && password.equals(document.getString("password"))) {
                            Log.d("Success", "DocumentSnapshot data: " + document.getData());
                            Toast.makeText(loginActivity.this, "Saving data...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(loginActivity.this, MainActivity.class);

                            intent.putExtra("username", username);
                            intent.putExtra("password", password);

                            //SharedPreferences.Editor editor = sp_login.edit();
                            //editor.putString("username", document.getId());
                            //editor.putString("password", document.getString("password"));
                            //editor.commit();
                            loginActivity.this.startActivity(intent);
                            //finish(); //See if this is what we need to stop backtracking to login
                        } else {
                            setContentView(R.layout.activity_login);
                            initializeGui();
                            Log.d("Failed", "Wrong Credentials");
                        }
                    } else {
                        Log.d("Error", "Failed with ", task.getException());
                    }
                }
            });
        }
        else{
            //do nothing, continue
            return;
        }
    }

    public void initializeGui(){

        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        loginBtn = (Button) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_login){
                    //Toast.makeText(getApplicationContext(),"Inner Anonymous class implementation",Toast.LENGTH_SHORT).show();
                    try {
                        validateLogin();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}