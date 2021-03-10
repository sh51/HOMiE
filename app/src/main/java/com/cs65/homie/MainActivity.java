package com.cs65.homie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cs65.homie.ui.ProfileSettingsActivity;
import com.cs65.homie.ui.chats.ChatFragment;
import com.cs65.homie.ui.login.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import org.jetbrains.annotations.NotNull;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    // TODO: We need a better pattern
    public static String userId = null;

    // Needs to be "homies" with an 's' and not "homie" because the package
    // name "homie" has too many matches in regex logging mode
    public static final String TAG = "HOMIES";

    private static final String BUNDLE_KEY_IN_CHAT_BOOL
        = "MAIN_ACTIVITY_BUNDLE_KEY_IN_CHAT_BOOL";
    private static final String BUNDLE_KEY_MATCH_TRANSITION_BOOL
        = "MAIN_ACTIVITY_BUNDLE_KEY_MATCH_TRANSITION_BOOL";
    private static final String MATCH_TEXT_FORMAT = "%s is a Homie!";
    private static final int RC_LOGIN = 0;
    private static final int EDIT_PROFILE = 0;

    private FirebaseHelper mHelper;
    private Intent loginIntent;
    // the menu, login and logout action button
    private MenuItem mLogout, mLogin;
    private View hostView = null;
    // Whether or not the chat fragment is active
    // State must be tracked so that nav can nav back to chat.
    private boolean inChat = false;
    // Whether or not we are in a "You Have a Match!" transition
    // State must be tracked through rotation
    private boolean inMatchTransition = false;
    private NavController navController = null;
    private BottomNavigationView navView = null;
//    private String userId = null;

    // Fake app user ID for testing/demoing before Firebase
    // Profile view needs it
    // This eventually needs to be saved to the device somewhere for profile
    // view to operate
    public String getFakeMyId() {
        return "42";
    }

    // Fake profile user ID for testing/demoing before Firebase
    // Profile view needs it
    public String getFakeUserId() {
        return "41";
    }

    /**
     * Hide the navigation bar view, and extend the fragment container's
     * margins so that the container fills the entire containing view
     * (because the navigation bar is hidden)
     */
    public void hideNavView() {

        if (
            this.hostView != null
                && this.navView != null
                && this.navView.getVisibility() == View.VISIBLE
        ) {

            this.navView.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams params
                    = (ViewGroup.MarginLayoutParams) this.hostView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            this.hostView.setLayoutParams(params);

        }

    }

    /**
     * Handle the event when the chat conversation fragment is popped off
     * of the fragment stack
     */
    public void onChatPopped()
    {

        if (this.inChat)
        {

            this.inChat = false;

            if (this.navController != null)
            {
                this.navController.navigate(R.id.navigation_chats);
            }

            @SuppressWarnings("ConstantConditions")
            FragmentManager activeFragManager
                = this.getSupportFragmentManager().findFragmentById(
                R.id.nav_host_fragment
            ).getChildFragmentManager();
            activeFragManager.removeOnBackStackChangedListener(
                this::onChatPopped
            );

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.inChat = savedInstanceState.getBoolean(
                BUNDLE_KEY_IN_CHAT_BOOL, false
            );
            this.inMatchTransition = savedInstanceState.getBoolean(
                BUNDLE_KEY_MATCH_TRANSITION_BOOL, false
            );
        }

        setContentView(R.layout.activity_main);

        this.hostView = findViewById(R.id.nav_host_fragment);
        this.navView = findViewById(R.id.nav_view);

        mHelper = FirebaseHelper.getInstance();
        mHelper.loadServerKey(this);

        loginIntent = new Intent(this, LoginActivity.class);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_chats,
                R.id.navigation_match,
                R.id.navigation_profile
        ).build();
        this.navController = Navigation.findNavController(
                this, R.id.nav_host_fragment
        );
        NavigationUI.setupActionBarWithNavController(
                this, this.navController, appBarConfiguration
        );
        NavigationUI.setupWithNavController(this.navView, this.navController);

        // If we get here, it means that the MainActivity was killed
        // (likely rotation), and so the active fragment manager is also dead.
        // We need to re-register
        if (this.inChat)
        {
            FragmentManager activeFragManager
                = this.getSupportFragmentManager().findFragmentById(
                    R.id.nav_host_fragment
                ).getChildFragmentManager();
            activeFragManager.addOnBackStackChangedListener(this::onChatPopped);
        }

    }

    // set up the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), ProfileSettingsActivity.class);
        startActivityForResult(intent, EDIT_PROFILE);


//        switch (item.getItemId()) {
//            case R.id.menu_item_logout:
//                FirebaseAuth.getInstance().signOut();
//                // switch to login button after logout
//                if (mLogin != null && mLogout != null) {
//                    mLogin.setVisible(true);
//                    mLogout.setVisible(false);
//                }
//                return false;
//            case R.id.menu_item_login:
//                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivityForResult(intent, RC_LOGIN);
//        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        mLogout = menu.findItem(R.id.menu_item_logout);
//        mLogin = menu.findItem(R.id.menu_item_login);
//        if (mLogin != null && mLogout != null) {
//            boolean authenticated = mAuth.getCurrentUser() != null;
//            mLogin.setVisible(!authenticated);
//            mLogout.setVisible(authenticated);
//        }
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

    /**
     * Transition from match to chat, showing a pop-up on match
     *
     * @param name Name of the matchee
     */
    public void matchTransition(String name) {

        if (this.inMatchTransition) {
            return;
        }
        this.inMatchTransition = true;

        View popupView = this.getLayoutInflater().inflate(
                R.layout.popup_match, null
        );
        TextView nameView = popupView.findViewById(R.id.matchPopupTextView);
        if (nameView != null)
        {
            if (name == null || name.equals(""))
            {
                nameView.setText(R.string.profile_view_match_celebration);
            }
            else
            {
                nameView.setText(String.format(
                    Locale.getDefault(),
                    MATCH_TEXT_FORMAT,
                    name
                ));
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);
        builder.setOnCancelListener((d) -> this.finishMatchTransition());
        builder.show();

        // The dialog dies on rotation.
        // We accept that, and just move the user onto the chat view

        // TODO The chat must be read with the new matched chat upon the transition

    }

    /**
     * Show the navigation bar view if hidden
     */
    public void showNavView() {

        if (
            this.hostView != null
                && this.navView != null
                && this.navView.getVisibility() == View.GONE
        ) {

            this.navView.setVisibility(View.VISIBLE);

            ViewGroup.MarginLayoutParams params
                = (ViewGroup.MarginLayoutParams) this.hostView.getLayoutParams();
            params.setMargins(0, 0, 0, this.navView.getHeight());
            this.hostView.setLayoutParams(params);

        }

    }

    /**
     * Spawn a chat fragment between the app's user and the given user Id
     *
     * @param userId The other user in the chat
     */
    public void spawnChatFragment(String userId) {

        // Spawned fragment needs to know whose messages to query
        Bundle args = new Bundle();
        args.putString(ChatFragment.ARG_KEY_USER_ID, userId);

        FragmentManager activeFragManager
            = this.getSupportFragmentManager().findFragmentById(
                R.id.nav_host_fragment
            ).getChildFragmentManager();

        FragmentTransaction transaction = activeFragManager.beginTransaction();
        transaction.setCustomAnimations(
            R.anim.frag_enter_left, R.anim.frag_exit_left,
            R.anim.frag_enter_pop_left, R.anim.frag_exit_pop_left
        );

        // Since there are more than one fragment in the navigator, we must
        // remove the specific fragment we desire (chats). We cannot use
        // replace()
        // It is assumed that the chats fragment is active, so it will be first
        // in the given list
        // That is the only workflow that can spawn a chat fragment
        transaction.remove(activeFragManager.getFragments().get(0));
        transaction.add(R.id.nav_host_fragment, ChatFragment.class, args);

        // The next two calls makes the back button bring up the chats fragment
        transaction.setReorderingAllowed(true);
        transaction.addToBackStack(null);
        transaction.commit();
        activeFragManager.executePendingTransactions();

        this.inChat = true;
        activeFragManager.addOnBackStackChangedListener(this::onChatPopped);

    }

    protected void onResume() {

        super.onResume();
        // If we were in a match transition upon destruction, immediately
        // navigate to the chats fragment
        // The dialog was destroyed
        if (this.inMatchTransition) {
            this.finishMatchTransition();
        }
    }

    protected void onSaveInstanceState(@NotNull Bundle outBundle) {
        super.onSaveInstanceState(outBundle);
        outBundle.putBoolean(
            BUNDLE_KEY_IN_CHAT_BOOL, this.inChat
        );
        outBundle.putBoolean(
            BUNDLE_KEY_MATCH_TRANSITION_BOOL, this.inMatchTransition
        );
    }

    /**
     * Complete a match transition upon destruction of the pop-up dialog
     */
    private void finishMatchTransition() {
        if (this.inMatchTransition) {
            this.inMatchTransition = false;
            if (this.navController != null) {
                this.navController.navigate(R.id.navigation_chats);
            }
        }
    }

}