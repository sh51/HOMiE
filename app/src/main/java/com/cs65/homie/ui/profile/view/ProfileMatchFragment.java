package com.cs65.homie.ui.profile.view;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.Utilities;
import com.cs65.homie.ui.gestures.OnSwipeGestureListener;
import com.cs65.homie.ui.gestures.SwipeGesture;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    private FloatingActionButton buttonMatch = null;
    private FloatingActionButton buttonReject = null;

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

        // FIXME Using fake data, need to set to the other user
        super.vm.setUserId(((MainActivity)this.requireActivity()).getFakeUserId());

        super.onViewCreated(view, savedInstanceState);

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
    }

    /**
     * Load fake data in lieu of Firebase, for testing
     */
    private void loadFakeData()
    {

        this.vm.getBathroom().setValue(true);
        this.vm.getBio().setValue(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do "
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
        this.vm.getPets().setValue(true);
        this.vm.getPlace().setValue(true);
        this.vm.getProfileName().setValue("Jane");
        this.vm.getSmoking().setValue(true);
        this.vm.getAvatarUri().setValue(
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.ai)
        );

        List<Uri> images = new ArrayList<Uri>();
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart0));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart1));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart2));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart3));
        this.vm.getimages().setValue(images);

    }

}
