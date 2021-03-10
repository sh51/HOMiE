package com.cs65.homie.ui.profile.view;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.cardview.widget.CardView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.cs65.homie.FirebaseHelper;
import com.cs65.homie.Globals;
import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.Utilities;
import com.cs65.homie.models.GenderEnum;
import com.cs65.homie.models.Profile;
import com.cs65.homie.ui.gestures.OnSwipeGestureListener;
import com.cs65.homie.ui.gestures.SwipeGesture;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Matching fragment, to handle matching functionality
 * <p>
 * This fragment assumes that its parent is ALWAYS MainActivity,
 * and that's not a temporary fake-data dependency
 */
@SuppressWarnings("Convert2Diamond")
public class ProfileMatchFragment
        extends ProfileViewFragment
        implements OnSwipeGestureListener,
        View.OnClickListener,
        View.OnTouchListener {

    public static final String BUNDLE_KEY_MATCH_PROFILES_INDEX
            = "PROFILE_MATCH_FRAG_BUNDLE_KEY_PROFILES_INDEX";

    // Whether or not to show the matching buttons
    private static final boolean BUTTON = false;

    private static final int CARD_BACKGROUND_COLOR = 0xFFDDDDDD;
    private static final double CARD_ELEVATION = 5.0;
    private static final double CARD_MARGIN = 20.0;
    private static final double MATCH_BUTTONS_PADDING = 60.0;

    private FirebaseHelper mHelper;
    private FloatingActionButton buttonMatch = null;
    private FloatingActionButton buttonReject = null;
    private boolean inMatchEvent = false;

    Toast mToast;

    public void onClick(View view) {
        if (view.equals(buttonMatch)) {
            this.handleMatch();
        } else if (view.equals(this.buttonReject)) {
            this.handleReject();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

//        if (this.getArguments() != null) {
//            this.currentIndex = this.getArguments().getInt(
//                    BUNDLE_KEY_MATCH_PROFILES_INDEX, 0
//            );
//        } else if (savedInstanceState != null) {
//            this.currentIndex = savedInstanceState.getInt(
//                    BUNDLE_KEY_MATCH_PROFILES_INDEX, 0
//            );
//        }


        super.vm = new ViewModelProvider(this).get(
                ProfileViewFragmentViewModel.class
        );
        mHelper = FirebaseHelper.getInstance();

        // Call super last
        super.onCreate(savedInstanceState);

        mToast = Toast.makeText(getActivity(), "Liked!", Toast.LENGTH_SHORT);
    }

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        //this.loadFakeData();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(BUNDLE_KEY_MATCH_PROFILES_INDEX, this.currentIndex);
    }

    public void onSwipeLeft() {
        this.handleReject();
    }

    public void onSwipeRight() {
        this.handleMatch();
    }

    /**
     * Do nothing on up swipes
     */
    public void onSwipeDown() {
    }

    /**
     * Do nothing on down swipes
     */
    public void onSwipeUp() {
    }

    /**
     * Capture a touch event, and do not let it propagate (return true)
     *
     * @param view  View that was touched. Always the image carousel fragment.
     * @param event Touch event
     * @return True
     */
    public boolean onTouch(View view, MotionEvent event) {
        view.performClick();
        return true;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);


        ActionMenuItemView settings = getActivity().findViewById(R.id.menu_item_settingsgear);
        settings.setVisibility(View.GONE);

        View scrollLayout = view.findViewById(R.id.profileViewScrollLayout);
        if (scrollLayout != null) {
            new SwipeGesture(
                    this.getContext(), scrollLayout, this
            );
        }

        float dip = 10f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        CardView cardView = view.findViewById(R.id.profileViewCardView);
        cardView.setRadius(px);
        if (cardView != null) {

            // Sets up the card view for the matching view
            // By default the card view hides itself for the profile view

            LinearLayout.LayoutParams layoutParams
                    = (LinearLayout.LayoutParams) cardView.getLayoutParams();
            int margin = (int) Math.round(Utilities.pixelDensity(
                    this.requireContext(), CARD_MARGIN)
            );
            layoutParams.setMargins(margin, margin, margin, margin);
            cardView.setLayoutParams(layoutParams);
//            cardView.setCardBackgroundColor(CARD_BACKGROUND_COLOR);
            cardView.setCardBackgroundColor(Color.WHITE);
            int elevation = (int) Math.round(
                    Utilities.pixelDensity(this.requireContext(), CARD_ELEVATION)
            );
            cardView.setCardElevation(elevation);

        }

        View topLayout = view.findViewById(R.id.profileViewContainerLayout);
        if (topLayout != null && BUTTON) {
            // The match buttons require more padding
            topLayout.setPadding(
                    0, 0, 0, (int) Math.round(Utilities.pixelDensity(
                            this.requireContext(), MATCH_BUTTONS_PADDING
                    ))
            );
        }

        // We need to steal propagating touches from the carousel view
        // to not trigger swipes that are captured on the scroll view level
        View carouselView = view.findViewById(R.id.profileViewCarouselFragView);
        if (carouselView != null) {
            carouselView.setOnTouchListener(this);
        }

        // Set up the listener for the match buttons
        // And make them visible
        this.buttonMatch = view.findViewById(
                R.id.profileViewButtonMatchRight
        );
        if (this.buttonMatch != null && BUTTON) {
            buttonMatch.setOnClickListener(this);
            buttonMatch.setVisibility(View.VISIBLE);
        }
        this.buttonReject = view.findViewById(R.id.profileViewButtonMatchLeft);
        if (this.buttonReject != null && BUTTON) {
            buttonReject.setOnClickListener(this);
            buttonReject.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.loadProfile();
    }


    private void handleMatch() {
        // TODO replace the index based loading
        List<Profile> profiles = mHelper.getProfiles();
        int size = profiles.size();
//        Profile matchedProfile = profiles.get(currentIndex % size);
        Profile matchedProfile = mHelper.getSuggestedProfile();
        // only called when there is a match
        mHelper.like(matchedProfile.getId(), (matched) -> {
            if (matched)
                ((MainActivity) this.requireActivity()).matchTransition(
                        this.vm.getProfileName().getValue()
                );
            else {
                draw("left");
                mToast.show();
            }
        });
    }

    private void handleReject() {
        // TODO Need to notify Firebase of the rejection
        mToast.cancel();
        draw("right");
    }


    private void draw(String direction) {
        // Animate the NEW fragment into focus
        // The next match option will be handled by the NEW fragment instance,
        // not this one
        FragmentManager activeFragManager = this.getParentFragmentManager();
        FragmentTransaction transaction = activeFragManager.beginTransaction();
        if (direction.equals("right")) {
            transaction.setCustomAnimations(
//                    R.anim.frag_enter_right, R.anim.frag_exit_right,
//                    R.anim.frag_enter_pop_right, R.anim.frag_exit_pop_right
                    R.anim.frag_fade_in, R.anim.frag_exit_right,
                    R.anim.frag_fade_in, R.anim.frag_exit_pop_right
            );
        } else transaction.setCustomAnimations(
//                R.anim.frag_enter_left, R.anim.frag_exit_left,
//                R.anim.frag_enter_pop_left, R.anim.frag_exit_pop_left
                R.anim.frag_fade_in, R.anim.frag_exit_left,
                R.anim.frag_fade_in, R.anim.frag_exit_pop_left
        );

        // Put the next index into the new fragments args
        Bundle args = new Bundle();
//        args.putInt(BUNDLE_KEY_MATCH_PROFILES_INDEX, (++this.currentIndex % mHelper.getProfiles().size()));
        mHelper.suggestAnotherProfile();

        // Get the current fragment from the active manager (this fragment)
        transaction.remove(activeFragManager.getFragments().get(0));
        transaction.add(
                R.id.nav_host_fragment, ProfileMatchFragment.class, args
        );
        transaction.commit();
        activeFragManager.executePendingTransactions();

        // Don't finish() because you don't finish fragments
        // We're effectively finished though
//        loadProfile();
    }

    /**
     * Load fake data in lieu of Firebase, for testing
     */
    private void loadFakeData() {

        this.vm.getBathroom().setValue(true);
        this.vm.getBio().setValue(
                "i Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do "
                        + "eiusmod tempor incididunt ut labore et dolore magna aliqua. "
                        + "Ut enim ad minim veniam, quis nostrud exercitation ullamco "
                        + "laboris nisi ut aliquip ex ea commodo consequat. Duis aute "
                        + "irure dolor in reprehenderit in voluptate velit esse cillum "
                        + "dolore eu fugiat nulla pariatur. Excepteur sint occaecat "
                        + "cupidatat non proident, sunt in culpa qui officia deserunt "
                        + "mollit anim id est laborum."
        );
        this.vm.getGender().setValue(GenderEnum.FEMALE);
        this.vm.getLoc().setValue(new LatLng(43.624794, -72.323171));
        this.vm.getMyLoc().setValue(new LatLng(43.704166, -72.288762));
        this.vm.getMyLocStr().setValue("Loops");
        List<Uri> images = new ArrayList<Uri>();
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart0));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart1));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart2));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart3));
        this.vm.getImages().setValue(images);

    }

    private void loadProfile() {
        super.loadProfile(mHelper.getSuggestedProfile());
    }

}