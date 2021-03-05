package com.cs65.homie;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cs65.homie.ui.chats.ChatFragment;
import com.cs65.homie.ui.login.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
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
    private View hostView = null;
    private BottomNavigationView navView = null;

    // Fake app user ID for testing/demoing before Firebase
    // Profile view needs it
    // This eventually needs to be saved to the device somewhere for profile
    // view to operate
    public String getFakeMyId()
    {
        return "42";
    }
    // Fake profile user ID for testing/demoing before Firebase
    // Profile view needs it
    public String getFakeUserId()
    {
        return "41";
    }

    public void hideNavView()
    {

        if (this.hostView != null && this.navView != null)
        {

            this.navView.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams params
                = (ViewGroup.MarginLayoutParams)this.hostView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            this.hostView.setLayoutParams(params);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.hostView = findViewById(R.id.nav_host_fragment);
        this.navView = findViewById(R.id.nav_view);

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

    public void showNavView()
    {

        if (this.hostView != null && this.navView != null)
        {

            this.navView.setVisibility(View.VISIBLE);

            ViewGroup.MarginLayoutParams params
                = (ViewGroup.MarginLayoutParams)this.hostView.getLayoutParams();
            params.setMargins(0, 0, 0, this.navView.getHeight());
            this.hostView.setLayoutParams(params);

        }

    }

    // TODO Comment
    public void spawnChatFragment(String userId)
    {

        // TODO Add animation

        Bundle args = new Bundle();
        args.putString(ChatFragment.ARG_KEY_USER_ID, userId);
        @SuppressWarnings("ConstantConditions")
        FragmentManager activeFragManager
            = this.getSupportFragmentManager().findFragmentById(
            R.id.nav_host_fragment
        ).getChildFragmentManager();
        FragmentTransaction transaction = activeFragManager.beginTransaction();
        transaction.remove(activeFragManager.getFragments().get(0));
        transaction.add(R.id.nav_host_fragment, ChatFragment.class, args);
        transaction.setReorderingAllowed(true);
        transaction.addToBackStack(null);
        transaction.commit();
        activeFragManager.executePendingTransactions();

    }

}