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
import androidx.lifecycle.ViewModel;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private static final double MATCH_BUTTONS_PADDING = 60.0;

    // TODO add more logic to match suggestion
    private int currentIndex;

    private FirebaseHelper mHelper;
    private FloatingActionButton buttonMatch = null;
    private FloatingActionButton buttonReject = null;

    private List<Profile> matches = new ArrayList<>();


    public void onClick(View view)
    {

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
            topLayout.setPadding(
                0, 0, 0, (int)Math.round(Utilities.pixelDensity(
                    this.requireContext(), MATCH_BUTTONS_PADDING
                ))
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

        loadProfile();

    }

    ///// ///// /////

    private boolean isMatch()
    {
        // TODO A real implementation would somehow ping firebase (or a cache)
        // and see if userId has matched myId.
        return true;
    }


    private void handleMatch() {
        if (this.isMatch()) {
            final Map<String, Object> likeMap = new HashMap<>();
            List<Profile> profiles = mHelper.getProfiles();
            int size = profiles.size();
            Profile matchedProfile = profiles.get(currentIndex % size);

            likeMap.put("likes", FieldValue.arrayUnion(matchedProfile.getId()));

            mHelper.updateProfile(mHelper.getUid(), likeMap);

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
    }
}
