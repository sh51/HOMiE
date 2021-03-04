package com.cs65.homie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cs65.homie.ui.chats.ChatFragment;
import com.cs65.homie.ui.login.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    // Needs to be "homies" with an 's' and not "homie" because the package
    // name "homie" has too many matches in regex logging mode
    public static final String TAG = "HOMIES";
    private static final int RC_LOGIN = 0;

    private FirebaseAuth mAuth;
    // the menu, login and logout action button
    private MenuItem mLogout, mLogin;

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

        mAuth = FirebaseAuth.getInstance();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_chats,
                R.id.navigation_match,
                R.id.navigation_profile
        ).build();
        NavController navController = Navigation.findNavController(
            this, R.id.nav_host_fragment
        );
        NavigationUI.setupActionBarWithNavController(
            this, navController, appBarConfiguration
        );
        NavigationUI.setupWithNavController(navView, navController);

        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivityForResult(intent, RC_LOGIN);

        // TODO The landing activity should probably be profile?
        // It shouldn't be messages at least.

    }


    // set up the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_logout:
                FirebaseAuth.getInstance().signOut();
                // switch to login button after logout
                if (mLogin != null && mLogout != null) {
                    mLogin.setVisible(true);
                    mLogout.setVisible(false);
                }
                return false;
            case R.id.menu_item_login:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, RC_LOGIN);
        }
        return false;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        mLogout = menu.findItem(R.id.menu_item_logout);
        mLogin = menu.findItem(R.id.menu_item_login);
        if (mLogin != null && mLogout != null) {
            boolean authenticated = mAuth.getCurrentUser() != null;
            mLogin.setVisible(!authenticated);
            mLogout.setVisible(authenticated);
        }
        return true;
    }

    public void spawnChatFragment(String userId)
    {

        //final String TAG = "CHAT_FRAG_TAG";
        Bundle args = new Bundle();
        args.putString("CHAT_FRAG_ARGS_KEY_USER_ID", userId);
        FragmentManager activeFragManager = this.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment).getChildFragmentManager();
        FragmentTransaction transaction = activeFragManager.beginTransaction();
        transaction.remove(activeFragManager.getFragments().get(0));
        transaction.add(R.id.nav_host_fragment, ChatFragment.class, args);
        transaction.addToBackStack(null);
        transaction.commit();
        activeFragManager.executePendingTransactions();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_LOGIN) {
            Log.d(Globals.TAG, "RC_LOGIN");
            // switch to logout button after successful login
            if (mLogin != null && mLogout != null) {
                mLogin.setVisible(false);
                mLogout.setVisible(true);
            }
        }
    }

}