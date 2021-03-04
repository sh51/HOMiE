package com.cs65.homie.ui.profile.view;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.ThreadPerTaskExecutor;
import com.cs65.homie.Utilities;
import com.cs65.homie.ui.carousel.ImageCarouselFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;


/**
 * Static profile view activity
 */
@SuppressWarnings("Convert2Diamond")
public class ProfileViewFragment
    extends Fragment
    implements Consumer<Location>, LocationListener
{

    public static final String BUNDLE_KEY_MY_ID
        = "BUNDLE_KEY_PROFILE_VIEW_MY_ID";
    public static final String BUNDLE_KEY_USER_ID
        = "BUNDLE_KEY_PROFILE_VIEW_USER_ID";
    public static final String CURRENT_LOC_FORMAT_STR
        = "Your current location:%n%s";
    public static final int GENDER_TEXT_VIEW_CHAR_LIMIT = 30;
    public static final String PLACE_FORMAT_STR
        = "%s %.1f miles from you";
    public static final String PLACE_HAS_STR = "Has a place";
    public static final String PLACE_WANTS_STR = "Is looking for a place";

    private Geocoder geocoder = null;
    private LocationManager locationManager = null;
    private String locationProvider = null;
    private final HandlerThread workerThread;
    private ImageView viewAvatar = null;
    private TextView viewBathroom = null;
    private TextView viewBio = null;
    private TextView viewGender = null;
    private TextView viewLoc = null;
    private TextView viewName = null;
    private TextView viewPets = null;
    private TextView viewSmoking = null;
    protected ProfileViewFragmentViewModel vm = null;

    public ProfileViewFragment()
    {
        this.workerThread = new HandlerThread(
            this.getClass().getCanonicalName(),
            Process.THREAD_PRIORITY_BACKGROUND
        );
        this.workerThread.start();
    }

    ///// ///// /////

    public void accept(Location location)
    {
        if (location != null)
        {
            LatLng latLng = new LatLng(
                location.getLatitude(), location.getLongitude()
            );
            if (latLng.latitude != 0 || latLng.longitude != 0)
            {
                this.vm.getMyLoc().postValue(latLng);
            }
        }
    }

    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        Log.d(
            MainActivity.TAG,
            this.getClass().getCanonicalName()
                + " location permissions: " +
                Utilities.checkPermissionLocation(this.getActivity())
        );

        // Get the view model instance
        this.vm = new ViewModelProvider(this).get(
            ProfileViewFragmentViewModel.class
        );
        if (this.vm == null)
        {
            // TODO Handle
            // If it can even happen
            Log.d(
                MainActivity.TAG,
                this.getClass().getCanonicalName()
                    + ".onCreate(), ViewModel is null"
            );
            return;
        }

        // Setup My ID
        if (this.vm.getMyId() < 0 && this.getActivity() != null)
        {
            // FIXME Using fake data
            this.vm.setMyId(((MainActivity)this.getActivity()).getFakeMyId());
        }
        if (this.vm.getMyId() < 0)
        {
            // TODO Handle
            Log.d(
                MainActivity.TAG, String.format(
                    "%s.onCreate(), MyId is %d",
                    this.getClass().getCanonicalName(),
                    this.vm.getMyId()
                ));
            return;
        }

        // Setup User ID
        if (this.vm.getUserId() < 0 && this.getActivity() != null)
        {
            // FIXME Using fake data
            this.vm.setUserId(((MainActivity)this.getActivity()).getFakeMyId());
        }
        if (this.vm.getUserId() < 0)
        {
            // TODO Handle
            Log.d(MainActivity.TAG, String.format(
                "%s.onCreate(), UserID is %d",
                this.getClass().getCanonicalName(),
                this.vm.getUserId()
            ));
            return;
        }

        // Setup the location services if we need it.
        this.geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        this.locationManager
            = (LocationManager) this.getActivity().getSystemService(
            Context.LOCATION_SERVICE
        );
        Criteria locCriteria = new Criteria();
        locCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        this.locationProvider = this.locationManager.getBestProvider(
            locCriteria, true
        );
        Log.d(
            MainActivity.TAG,
            this.getClass().getCanonicalName()
                + " location provider: "
                + this.locationProvider
        );

    }

    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    )
    {
        // Load the layout
        return inflater.inflate(
            R.layout.fragment_profile_view, container, false
        );
    }

    public void onDestroy()
    {
        this.workerThread.quitSafely();
        super.onDestroy();
    }

    public void onLocationChanged(Location location)
    {
        if (location.getLatitude() != 0 || location.getLongitude() != 0)
        {
            this.vm.getMyLoc().postValue(
                new LatLng(location.getLatitude(), location.getLongitude())
            );
        }
    }

    public void onUpdateLocLive(boolean locLive)
    {
        if (locLive)
        {
            this.requestLocUpdate();
        }
    }

    public void onResume()
    {
        super.onResume();
        // If the user uses a live location, update the location on resume
        // At the moment we don't bother listening to the location
        // Excessive
        if (this.vm.getMyLocLive().getValue())
        {
            this.requestLocUpdate();
        }
    }

    // Unimplemented. We do not care about location provider status events
    public void onStatusChanged (String provider, int status, Bundle extras) {}

    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        // Set up the observers for all the relevant views
        this.viewAvatar = view.findViewById(R.id.profileViewAvatarImageView);
        if (this.viewAvatar != null)
        {
            this.vm.getAvatarUri().observe(
                this.getViewLifecycleOwner(), this::updateViewAvatar
            );
            Uri avatarUri = this.vm.getAvatarUri().getValue();
            if (avatarUri != null)
            {
                this.updateViewAvatar(avatarUri);
            }
        }
        this.viewBathroom = view.findViewById(R.id.profileViewBathroomTextView);
        if (this.viewBathroom != null)
        {
            this.vm.getBathroom().observe(
                this.getViewLifecycleOwner(), this::updateViewBathroomByBathroom
            );
            this.vm.getPlace().observe(
                this.getViewLifecycleOwner(), this::updateViewBathroomByPlace
            );
            this.updateViewBathroom(
                this.vm.getBathroom().getValue(),
                this.vm.getPlace().getValue()
            );
        }
        this.viewBio = view.findViewById(R.id.profileViewBioTextView);
        if (this.viewBio != null)
        {
            this.vm.getBio().observe(
                this.getViewLifecycleOwner(), this::updateViewBio
            );
            this.updateViewBio(this.vm.getBio().getValue());
        }
        this.viewGender = view.findViewById(R.id.profileViewGenderTextView);
        if (this.viewGender != null)
        {
            this.vm.getGender().observe(
                this.getViewLifecycleOwner(), this::updateViewGender
            );
            this.updateViewGender(this.vm.getGender().getValue());
        }
        this.viewLoc = view.findViewById(R.id.profileViewLocationTextView);
        if (this.viewLoc != null)
        {
            this.vm.getLoc().observe(
                this.getViewLifecycleOwner(), this::updateViewLocByUserLoc);
            this.vm.getMyLoc().observe(
                this.getViewLifecycleOwner(), this::updateViewLocByMyLoc
            );
            this.vm.getMyLocStr().observe(
                this.getViewLifecycleOwner(), this::updateViewLocByMyString
            );
            // Don't prime locations with default values
        }
        this.viewName = view.findViewById(R.id.profileViewNameTextView);
        if (this.viewName != null)
        {
            this.vm.getProfileName().observe(
                this.getViewLifecycleOwner(), this::updateViewName
            );
            this.updateViewName(this.vm.getProfileName().getValue());
        }
        this.viewPets = view.findViewById(R.id.profileViewPetsTextView);
        if (this.viewPets != null)
        {
            this.vm.getPets().observe(
                this.getViewLifecycleOwner(), this::updateViewPets
            );
            this.updateViewPets(this.vm.getPets().getValue());
        }
        this.viewSmoking = view.findViewById(R.id.profileViewSmokingTextView);
        if (this.viewSmoking != null)
        {
            this.vm.getSmoking().observe(
                this.getViewLifecycleOwner(), this::updateViewSmoking
            );
            this.updateViewSmoking(this.vm.getSmoking().getValue());
        }

        // Set the non-view observer for the location live field
        this.vm.getMyLocLive().observe(
            this.getViewLifecycleOwner(), this::onUpdateLocLive
        );

        // Load the image carousel
        ImageCarouselFragment carouselFrag
            = (ImageCarouselFragment)this.getChildFragmentManager()
            .findFragmentById(R.id.profileViewCarouselFragView);
        if (carouselFrag != null)
        {
            this.vm.getimages().observe(
                this.getViewLifecycleOwner(), carouselFrag::setImages
            );
        }

        // Fetch Firebase data asynchronously (eventually, somehow)
        this.pingFirebase();

    }

    public void updateViewAvatar(Uri avatar)
    {

        if (avatar == null)
        {
            // TODO This resource should not be the final placeholder
            this.viewAvatar.setImageResource(R.drawable.ic_dartmouth_seal);
        }
        else
        {
            this.viewAvatar.setImageURI(null);
            this.viewAvatar.setImageURI(avatar);
        }

    }

    /**
     * Update the bathroom text view on a change in the private bathroom state
     *
     * @param bathroom  Whether or not a private bathroom is applicable
     */
    public void updateViewBathroomByBathroom(boolean bathroom)
    {
        if (this.vm == null)
        {
            Log.d(
                MainActivity.TAG,
                this.getClass().getCanonicalName()
                + ".updateViewBathroomByBathroom(), VM is null"
            );
        }
        else
        {
            this.updateViewBathroom(bathroom, this.vm.getPlace().getValue());
        }
    }

    /**
     * Update the bathroom text view on a change in the place state
     *
     * @param place Whether or not the user in question has a place
     */
    public void updateViewBathroomByPlace(boolean place)
    {
        if (this.vm == null)
        {
            Log.d(
                MainActivity.TAG,
                this.getClass().getCanonicalName()
                + ".updateViewBathroomByPlace(), VM is null"
            );
        }
        else
        {
            this.updateViewBathroom(this.vm.getBathroom().getValue(), place);
        }
    }

    public void updateViewBio(String bio)
    {
        if (this.viewBio != null)
        {
            this.viewBio.setText(bio);
        }
    }

    public void updateViewGender(String gender)
    {
        if (this.viewGender != null)
        {
            this.viewGender.setText(gender);
        }
    }

    /**
     * Update the location text view upon a change to the app user's location
     *
     * @param myLoc     The app user's location
     */
    public void updateViewLocByMyLoc(LatLng myLoc)
    {

        if (this.isMe())
        {

            //noinspection StatementWithEmptyBody
            if (
                this.viewLoc != null
                && this.geocoder != null
                && this.vm.getMyLocLive().getValue()
            )
            {

                // Get address represented by the user's current location
                Address address = Utilities.latLngToAddress(
                    this.geocoder, myLoc
                );
                if (address != null)
                {
                    // Usually there's only one line, and it contains
                    // the whole address
                    String addrStr = address.getAddressLine(0);
                    if (addrStr !=  null)
                    {
                        this.viewLoc.setText(String.format(
                            Locale.getDefault(),
                            CURRENT_LOC_FORMAT_STR,
                            addrStr
                        ));
                    }
                }

            }
            else
            {
                // This branch represents a static location address string
                // that the user has set
                // This is handled by updateMyLocStr and should not be updated
                // by this method since this method is only invoked on the myLoc
                // change.
            }

        }
        else
        {

            // Show the distance from the app user to the profile's user
            LatLng zero = new LatLng(0, 0);
            LatLng theirLoc = this.vm.getLoc().getValue();
            if (myLoc.equals(zero) || theirLoc.equals(zero))
            {
                return;
            }

            this.updateViewLoc(myLoc, theirLoc, this.vm.getPlace().getValue());

        }

    }

    /**
     * Update the location text view upon a change to the app user's location
     * string
     *
     * @param myLoc     The app user's location string
     */
    public void updateViewLocByMyString(String myLoc)
    {
        if (
            this.viewLoc != null
            && this.isMe()
            && !this.vm.getMyLocLive().getValue()
        )
        {
            this.viewLoc.setText(myLoc);
        }
    }

    /**
     * Update the location text view upon a change to the profile user's
     * location string
     *
     * @param userLoc   The profile user's location
     */
    public void updateViewLocByUserLoc(LatLng userLoc)
    {

        LatLng zero = new LatLng(0, 0);
        if (this.isMe())
        {
            return;
        }
        LatLng myLoc = this.vm.getMyLoc().getValue();
        if (myLoc.equals(zero) || userLoc.equals(zero))
        {
            return;
        }

        this.updateViewLoc(myLoc, userLoc, this.vm.getPlace().getValue());

    }

    public void updateViewName(String name)
    {
        if (this.viewName != null)
        {
            this.viewName.setText(name);
        }
    }

    public void updateViewPets(boolean pets)
    {
        if (this.viewPets != null)
        {
            // TODO The intention is that the final version will use icons
            // rather than unicode, but this is good for now
            if (pets)
            {
                this.viewPets.setText(R.string.profile_view_pets_yes);
            }
            else
            {
                this.viewPets.setText(R.string.profile_view_pets_no);
            }
        }
    }

    public void updateViewSmoking(boolean smoking)
    {
        if (this.viewSmoking != null)
        {
            // TODO The intention is that the final version will use icons
            // rather than unicode, but this is good for now
            if (smoking)
            {
                this.viewSmoking.setText(R.string.profile_view_smoking_yes);
            }
            else
            {
                this.viewSmoking.setText(R.string.profile_view_smoking_no);
            }
        }
    }


    ///// ///// /////

    /**
     * Whether or not this profile view is of the app user
     *
     * @return  Whether or not this profile viwe is of the app user
     */
    private boolean isMe()
    {
        return this.vm.getMyId() == this.vm.getUserId();
    }

    /**
     * Load fake data in lieu of Firebase, for testing
     */
    private void loadFakeData()
    {

        this.vm.getBathroom().setValue(false);
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
        this.vm.getGender().setValue("Male");
        this.vm.getLoc().setValue(new LatLng(43.624794, -72.323171));
        this.vm.getMyLoc().setValue(new LatLng(43.704166, -72.288762));
        this.vm.getMyLocLive().setValue(true);
        this.vm.getMyLocStr().setValue("Sanborn");
        this.vm.getPets().setValue(false);
        this.vm.getPlace().setValue(false);
        this.vm.getProfileName().setValue("John");
        this.vm.getSmoking().setValue(false);

        List<Uri> images = new ArrayList<Uri>();
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart0));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart1));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart2));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart3));
        this.vm.getimages().setValue(images);

    }

    private void pingFirebase()
    {
        this.loadFakeData();
    }

    @SuppressLint("MissingPermission")
    private void requestLocUpdate()
    {
        if (this.locationManager != null && this.locationProvider != null)
        {

            // Before the async call, get the last location in the mean time
            Location loc = this.locationManager.getLastKnownLocation(
                this.locationProvider
            );
            if (
                loc != null
                && (loc.getLongitude() != 0 || loc.getLatitude() != 0)
            )
            {
                this.vm.getMyLoc().postValue(new LatLng(
                    loc.getLatitude(), loc.getLongitude()
                ));
            }

            if (Build.VERSION.SDK_INT >= Utilities.GET_CURRENT_LOC_SDK)
            {
                this.locationManager.getCurrentLocation(
                    this.locationProvider,
                    null,
                    new ThreadPerTaskExecutor(),
                    this
                );
            }
            else
            {
                //noinspection deprecation
                this.locationManager.requestSingleUpdate(
                    this.locationProvider,
                    this,
                    this.workerThread.getLooper()
                );
            }
        }
    }

    private void updateViewBathroom(boolean bathroom, boolean place)
    {
        if (this.viewBathroom != null)
        {
            // TODO The intention is that the final version will use icons
            // rather than unicode, but this is good for now
            if (bathroom)
            {
                if (place)
                {
                    this.viewBathroom.setText(
                        R.string.profile_view_bathroom_yes_place_yes
                    );
                }
                else
                {
                    this.viewBathroom.setText(
                        R.string.profile_view_bathroom_yes_place_no
                    );
                }
            }
            else
            {
                if (place)
                {
                    this.viewBathroom.setText(
                        R.string.profile_view_bathroom_no_place_yes
                    );
                }
                else
                {
                    this.viewBathroom.setText(
                        R.string.profile_view_bathroom_no_place_no
                    );
                }
            }
        }
    }

    private void updateViewLoc(LatLng myLoc, LatLng theirLoc, boolean place)
    {

        if (this.viewLoc == null)
        {
            return;
        }

        double distance = Utilities.distanceHaversine(
            myLoc, theirLoc, 0, 0
        );

        // TODO Handle unit preference once that is established on the editable
        // end

        String placeStr;
        if (place)
        {
            placeStr = PLACE_HAS_STR;
        }
        else
        {
            placeStr = PLACE_WANTS_STR;
        }
        this.viewLoc.setText(String.format(
            Locale.getDefault(),
            PLACE_FORMAT_STR,
            placeStr, distance
        ));

    }

}