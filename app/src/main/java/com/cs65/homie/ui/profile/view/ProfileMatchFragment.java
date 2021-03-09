package com.cs65.homie.ui.profile.view;

import android.content.ContentResolver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cs65.homie.FirebaseHelper;
import com.cs65.homie.Globals;
import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.Utilities;
import com.cs65.homie.models.Profile;
import com.cs65.homie.ui.gestures.OnSwipeGestureListener;
import com.cs65.homie.ui.gestures.SwipeGesture;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * Matching fragment, to handle matching functionality
 *
 * This fragment assumes that its parent is ALWAYS MainActivity,
 * and that's not a temporary fake-data dependency
 */
@SuppressWarnings("Convert2Diamond")
public class ProfileMatchFragment
    extends ProfileViewFragment
    implements OnSwipeGestureListener,
    View.OnClickListener,
    View.OnTouchListener
{

    private static final int CARD_BACKGROUND_COLOR = 0xFFDDDDDD;
    private static final double CARD_ELEVATION = 5.0;
    private static final double CARD_MARGIN = 20.0;

    // TODO add more logic to match suggestion
    private int currentIndex;

    private FirebaseHelper mHelper;
    private FloatingActionButton buttonMatch = null;
    private FloatingActionButton buttonReject = null;

    private List<Profile> matches = new ArrayList<>();

    public void onClick(View view)
    {

        // FIXME It's likely you can match more than once by spamming
        // the button or swiping
        // There's nothing stopping you from doing that at least

        // TODO After-matching action, ping to Firebase, "you have a match!"
        // etc happens here
        // For now, toasts
        if (view.equals(buttonMatch))
        {
            this.handleMatch();
        }
        else if (view.equals(this.buttonReject))
        {
            this.handleReject();
        }
    }

    private void loadMatches() {
        // TODO: Improve matches
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Profile newMatch = new Profile();
                                newMatch.setBio((String)document.getData().get("bio"));
                                newMatch.setPrivateBathroom((boolean)document.getData().get("privateBathroom"));
                                newMatch.setisPetFriendly((boolean)document.getData().get("petFriendly"));
                                newMatch.setHasApartment((boolean)document.getData().get("hasApartment"));
                                newMatch.setSmoking((boolean)document.getData().get("smoking"));
                                newMatch.setFirstName((String)document.getData().get("firstName"));
                                newMatch.setGender(Math.toIntExact((long)document.getData().get("gender")));

                                matches.add(newMatch);
                            }
                        } else {
                            Log.w("firebase - homies", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelper = FirebaseHelper.getInstance();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        loadMatches();
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void onSwipeLeft()
    {
        this.handleReject();
    }

    public void onSwipeRight()
    {
        this.handleMatch();
    }

    /**
     * Do nothing on up swipes
     */
    public void onSwipeDown() {}

    /**
     * Do nothing on down swipes
     */
    public void onSwipeUp() {}

    /**
     * Capture a touch event, and do not let it propagate (return true)
     *
     * @param view  View that was touched. Always the image carousel fragment.
     * @param event Touch event
     *
     * @return      True
     */
    public boolean onTouch(View view, MotionEvent event)
    {
        view.performClick();
        return true;
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        // FIXME Using fake data, need to set to the other user
//        super.vm.setUserId(((MainActivity)this.requireActivity()).getFakeUserId());
        super.vm.setUserId("pR7PsciIRpdL24u54ZNoP85efh83");

        super.onViewCreated(view, savedInstanceState);

        View scrollLayout = view.findViewById(R.id.profileViewScrollLayout);
        if (scrollLayout != null)
        {
            new SwipeGesture(
                this.getContext(), scrollLayout, this
            );
        }

        CardView cardView = view.findViewById(R.id.profileViewCardView);
        if (cardView != null)
        {

            // Sets up the card view for the matching view
            // By default the card view hides itself for the profile view

            LinearLayout.LayoutParams layoutParams
                = (LinearLayout.LayoutParams)cardView.getLayoutParams();
            int margin = (int)Math.round(Utilities.pixelDensity(
                this.requireContext(), CARD_MARGIN)
            );
            layoutParams.setMargins(margin, margin, margin, margin);
            cardView.setLayoutParams(layoutParams);
            cardView.setCardBackgroundColor(CARD_BACKGROUND_COLOR);
            int elevation = (int)Math.round(
                Utilities.pixelDensity(this.requireContext(), CARD_ELEVATION)
            );
            cardView.setCardElevation(elevation);

        }

        View topLayout = view.findViewById(R.id.profileViewContainerLayout);
        if (topLayout != null)
        {
            // The match buttons require more padding
            // FIXME At least comment the magic numbers
            topLayout.setPadding(
                0, 0, 0,
                (int)Math.round(Utilities.pixelDensity(this.requireContext(), 60.0))
            );
        }

        // We need to steal propagating touches from the carousel view
        // to not trigger swipes that are captured on the scroll view level
        View carouselView = view.findViewById(R.id.profileViewCarouselFragView);
        if (carouselView != null)
        {
            carouselView.setOnTouchListener(this);
        }

        // Set up the listener for the match buttons
        // And make them visible
        this.buttonMatch = view.findViewById(
            R.id.profileViewButtonMatchRight
        );
        if (this.buttonMatch != null)
        {
            buttonMatch.setOnClickListener(this);
            buttonMatch.setVisibility(View.VISIBLE);
        }
        this.buttonReject = view.findViewById(R.id.profileViewButtonMatchLeft);
        if (this.buttonReject != null)
        {
            buttonReject.setOnClickListener(this);
            buttonReject.setVisibility(View.VISIBLE);
        }
        // TODO Using fake data for now
//        this.loadFakeData();
        loadProfile();

    }

    ///// ///// /////

    private boolean isMatch()
    {
        // TODO A real implementation would somehow ping firebase (or a cache)
        // and see if userId has matched myId.
        return true;
    }


    private void handleMatch()
    {

        // TODO Need to send match to Firebase

        if (this.isMatch())
        {
            ((MainActivity)this.requireActivity()).matchTransition(
                this.vm.getProfileName().getValue()
            );

        }

    }

    private void handleReject()
    {
        // TODO Need a real implementation
        Utilities.showErrorToast(
            R.string.profile_view_match_reject_description,
            this.getActivity()
        );

        currentIndex++;
        loadProfile();
    }

    /**
     * Load fake data in lieu of Firebase, for testing
     */
    private void loadFakeData()
    {
        // TODO: Where will we get this? We aren't currently storing it
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
        this.vm.getGender().setValue("Female");
        this.vm.getLoc().setValue(new LatLng(43.624794, -72.323171));
        this.vm.getMyLoc().setValue(new LatLng(43.704166, -72.288762));
        this.vm.getMyLocLive().setValue(true);
        this.vm.getMyLocStr().setValue("Sanborn");
        //this.vm.getAvatarUri().setValue(
        //    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.ai)
        //);
        List<Uri> images = new ArrayList<Uri>();
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart0));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart1));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart2));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart3));
        this.vm.getImages().setValue(images);


        // Firebase section --------------------
        MutableLiveData<String> bio = this.vm.getBio();
        MutableLiveData<Boolean> bathroom = this.vm.getBathroom();
        MutableLiveData<String> gender = this.vm.getGender();
        MutableLiveData<Boolean> pets = this.vm.getPets();
        MutableLiveData<Boolean> hasPlace = this.vm.getPlace();
        MutableLiveData<Boolean> isSmoking = this.vm.getSmoking();
        MutableLiveData<String> name = this.vm.getProfileName();

        // Get firebase wrapper (in-built)
        // Fetch profiles and loads them
        // TODO: We need some stratagey of marking unliked and matched profiles to avoid showing the same profile twice and avoid showing our own profile
        loadProfile("pR7PsciIRpdL24u54ZNoP85efh83");
    }
    private void loadProfile(String uid) {
        updateUI(mHelper.getProfile(uid));
    }

    private void loadProfile() {
        List<Profile> profiles = mHelper.getProfiles();
        int size = profiles.size();
        updateUI(profiles.get(currentIndex % size));
    }

    // Update UI given a profile
    private void updateUI(Profile p) {
        name.setValue(p.getFirstName());
        bathroom.setValue(p.isPrivateBathroom());
        bio.setValue(p.getBio());
        pets.setValue(p.isPetFriendly());
        hasPlace.setValue(p.isHasApartment());
        isSmoking.setValue(p.isSmoking());
        vm.getPriceMin().setValue(p.getMinPrice());
        vm.getPriceMax().setValue(p.getMaxPrice());
        if (p.getAvatarImage() != null) vm.getAvatarUri().setValue(Uri.parse(p.getAvatarImage()));

        int genderCode = p.getGender();
        if (genderCode == 1) {
            updateViewGender("Female");
        } else {
            updateViewGender("Male");
        }
    }
}
