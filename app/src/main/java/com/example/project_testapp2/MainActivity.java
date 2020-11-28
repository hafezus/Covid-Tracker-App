package com.example.project_testapp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private MenuItem logout;

    private SharedPreferences sp_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent service = new Intent(MainActivity.this, TracingService.class);
        startService(service);
        //NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView = findViewById(R.id.navigationView);
        drawer =findViewById(R.id.drawerLayout);
        logout = findViewById(R.id.logout);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.firstFragment) //Initialize R.id.secondFragment and R.id.thirdFragment if you want to change the way drawer menu works
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        sp_login = getSharedPreferences("loginInfo", MODE_PRIVATE);

        //Start service

        //Check if already logged in

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id==R.id.logout){
                    Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                    Log.v("Logout Button CLicked: ", "Reached Here");
                    SharedPreferences.Editor editor = sp_login.edit();
                    editor.putString("username", ""); //Clear shared preferences
                    editor.putString("password", ""); //Clear shared preferences
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, loginActivity.class);

                    MainActivity.this.startActivity(intent);
                }
                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true); //Login once only, prevents backtrack to login page
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout) {
            // Handle the camera action
            Log.v("Logout Button CLicked: ", "Reached Here");
            SharedPreferences.Editor editor = sp_login.edit();
            editor.putString("username", "");
            editor.putString("password", "");
            Intent intent = new Intent(MainActivity.this, loginActivity.class);

            MainActivity.this.startActivity(intent);
        }


        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v("Menu Item Click:", String.valueOf(item.getItemId()));
        switch (item.getItemId()) {
            case R.id.logout:
                Log.v("Logout Button CLicked: ", "Reached Here");
                SharedPreferences.Editor editor = sp_login.edit();
                editor.putString("username", "");
                editor.putString("password", "");
                Intent intent = new Intent(MainActivity.this, loginActivity.class);

                MainActivity.this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





}