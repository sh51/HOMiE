package com.cs65.homie;

import android.content.Intent;
import android.os.Bundle;

import com.cs65.homie.ui.login.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    // Needs to be "homies" with an 's' and not "homie" because the package
    // name "homie" has too many matches in regex logging mode
    public static final String TAG = "HOMIES";

    // Fake app user ID for testing/demoing before Firebase
    // Profile view needs it
    // This eventually needs to be saved to the device somewhere for profile
    // view to operate
    public long getFakeMyId()
    {
        return 42L;
    }
    // Fake profile user ID for testing/demoing before Firebase
    // Profile view needs it
    public long getFakeUserId()
    {
        return 41L;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_messages,
                R.id.navigation_dashboard,
                R.id.navigation_profile
        ).build();
        NavController navController = Navigation.findNavController(
            this, R.id.nav_host_fragment
        );
        NavigationUI.setupActionBarWithNavController(
            this, navController, appBarConfiguration
        );
        NavigationUI.setupWithNavController(navView, navController);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

}