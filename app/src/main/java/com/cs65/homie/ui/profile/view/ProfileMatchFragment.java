package com.cs65.homie.ui.profile.view;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.Utilities;
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

// TODO Clicking the avatar should put it into full screen (if set)


/**
 * Matching fragment, to handle matching functionality
 */
@SuppressWarnings("Convert2Diamond")
public class ProfileMatchFragment
    extends ProfileViewFragment
    implements OnSwipeGestureListener,
    View.OnClickListener,
    View.OnTouchListener
{

    private FloatingActionButton buttonMatch = null;
    private FloatingActionButton buttonReject = null;

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

    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    )
    {
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

        super.onViewCreated(view, savedInstanceState);

        // FIXME Using fake data, need to set to the other user
        super.vm.setUserId(((MainActivity)this.requireActivity()).getFakeUserId());

        View scrollLayout = view.findViewById(R.id.profileViewScrollLayout);
        if (scrollLayout != null)
        {
            new SwipeGesture(
                this.getContext(), scrollLayout, this
            );
        }

        View topLayout = view.findViewById(R.id.profileViewLayout);
        if (topLayout != null)
        {
            // The match buttons require more padding
            // FIXME At least comment the magic numbers
            topLayout.setPadding(
                0, 0, 0,
                (int)Math.round(Utilities.pixelDensity(this.requireContext(), 80.0))
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
        this.loadFakeData();

    }

    ///// ///// /////

    private void handleMatch()
    {
        // TODO Need a real implementation
        Utilities.showErrorToast(
            R.string.profile_view_match_accept_description,
            this.getActivity());
    }

    private void handleReject()
    {
        // TODO Need a real implementation
        Utilities.showErrorToast(
            R.string.profile_view_match_reject_description,
            this.getActivity()
        );
    }

    /**
     * Load fake data in lieu of Firebase, for testing
     */
    private void loadFakeData()
    {
        // TODO: Where will we get this? We aren't currently storing it
        this.vm.getLoc().setValue(new LatLng(43.624794, -72.323171));
        this.vm.getMyLoc().setValue(new LatLng(43.704166, -72.288762));
        this.vm.getMyLocLive().setValue(true);
        this.vm.getMyLocStr().setValue("Sanborn");
        this.vm.getAvatarUri().setValue(
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.ai)
        );
        List<Uri> images = new ArrayList<Uri>();
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart0));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart1));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart2));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart3));
        this.vm.getimages().setValue(images);


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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // Update UI
                                bathroom.setValue((boolean)document.getData().get("privateBathroom"));
                                bio.setValue((String)document.getData().get("bio"));
                                pets.setValue((boolean)document.getData().get("isPetFriendly"));
                                hasPlace.setValue((boolean)document.getData().get("hasApartment"));
                                isSmoking.setValue((boolean)document.getData().get("isSmoking"));
                                name.setValue((String)document.getData().get("firstname"));

                                int genderCode = Math.toIntExact((long)document.getData().get("gender"));
                                if (genderCode == 1) {
                                    gender.setValue("Female");
                                } else {
                                    gender.setValue("Male");
                                }
                            }
                        } else {
                            Log.w("firebase - homies", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

}
