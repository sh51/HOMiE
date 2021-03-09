package com.cs65.homie.ui.profile.view;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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

import androidx.core.text.HtmlCompat;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.cs65.homie.FirebaseHelper;
import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.ThreadPerTaskExecutor;
import com.cs65.homie.Utilities;
import com.cs65.homie.ui.ProfileSettingsActivity;
import com.cs65.homie.ui.carousel.ImageCarouselFragment;
import com.cs65.homie.ui.ImageFullScreenActivity;
import com.cs65.homie.ui.login.ui.login.LoginActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

// FIXME Does the view handle no image carousel correctly?

/**
 * Static profile view activity
 */
@SuppressWarnings("Convert2Diamond")
public class ProfileViewFragment
    extends Fragment
    implements Consumer<Location>, LocationListener
{

    private FirebaseHelper mHelper;

    @SuppressWarnings("unused")
    public static final String BUNDLE_KEY_MY_ID
        = "BUNDLE_KEY_PROFILE_VIEW_MY_ID";
    @SuppressWarnings("unused")
    public static final String BUNDLE_KEY_USER_ID
        = "BUNDLE_KEY_PROFILE_VIEW_USER_ID";
    public static final String CURRENT_LOC_FORMAT_STR
        = "Your current location:%n%s";
    public static final String PLACE_FORMAT_STR
        = "%s %.1f miles from you";
    public static final String PLACE_HAS_STR = "Has a place";
    public static final String PLACE_WANTS_STR = "Is looking for a place";
    public static final String RADIUS_FORMAT_STR
        = "Finding Homies within <b>%.1f</b> miles";
    public static final double RADIUS_LIMIT = 2000;
    public static final String PRICE_FORMAT_STR = "$%.2f";

    private ImageCarouselFragment fragImageCarousel = null;
    private Geocoder geocoder = null;
    private LocationManager locationManager = null;
    private String locationProvider = null;
    private final HandlerThread workerThread;
    private ImageView viewAvatar = null;
    private TextView viewBathroom = null;
    private TextView viewBio = null;
    private FragmentContainerView viewCarousel = null;
    private TextView viewGender = null;
    private TextView viewLoc = null;
    private TextView viewName = null;
    private TextView viewPets = null;
    private TextView viewPriceMin = null;
    private TextView viewPriceMax = null;
    private TextView viewRadius = null;
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

    public void firebaseCallback(Task<QuerySnapshot> task)
    {
        if (task.isSuccessful()) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                Log.d(
                    MainActivity.TAG + " firebase",
                    document.getId() + " => " + document.getData()
                );

                this.vm.getBathroom().postValue(
                    (boolean)document.getData().get("privateBathroom")
                );
                this.vm.getBio().postValue(
                    (String)document.getData().get("bio")
                );
                this.vm.getPets().postValue(
                    (boolean)document.getData().get("petFriendly")
                );
                this.vm.getPlace().postValue(
                    (boolean)document.getData().get("hasApartment")
                );
                this.vm.getSmoking().postValue(
                    (boolean)document.getData().get("smoking")
                );
                this.vm.getProfileName().postValue(
                    (String)document.getData().get("firstName")
                );

                int genderCode = Math.toIntExact(
                    (long)document.getData().get("gender")
                );
                if (genderCode == 1) {
                    this.vm.getGender().postValue("Female");
                } else {
                    this.vm.getGender().postValue("Male");
                }
            }

        } else {
            Log.d(
                "firebase - homie", "Error getting documents: ",
                task.getException()
            );
        }
    }

    public void onAvatarClick(View view)
    {

        if (
            this.vm.getAvatarUri().getValue() != null
        )
        {
            Intent intent = new Intent(
                this.getContext(), ImageFullScreenActivity.class
            );
            intent.putExtra(
                ImageFullScreenActivity.BUNDLE_ARG_KEY_URI,
                this.vm.getAvatarUri().getValue()
            );
            this.startActivity(intent);
        }

    }

    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        if (this.vm == null)
        {
            // Get the view model instance
            // ViewModel can never be null
            this.vm = new ViewModelProvider(this).get(
                ProfileViewFragmentViewModel.class
            );
        }

//        // quick fix
//        mHelper = FirebaseHelper.getInstance();
//        this.vm.setMyId(mHelper.getUid());


        // If no current USERid, go to profile page
        if (MainActivity.userId == null) {
            if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("profiles").whereEqualTo("id", FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("firebase - homie", document.getId() + " => " + document.getData());
                                        MainActivity.userId = document.getId();
                                    }
                                } else {
                                    Log.d("firebase - homie", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            } else {
                Intent myIntent = new Intent(ProfileViewFragment.this.getActivity(), ProfileSettingsActivity.class);
                startActivity(myIntent);
            }
        } else {
            Log.d(
                MainActivity.TAG,
                this.getClass().getCanonicalName()
                + " location permissions: "
                + Utilities.checkPermissionLocation(this.getActivity())
            );

            // Setup My ID
            // FIXME Default user ID (empty string) is magic
            if (this.vm.getMyId().equals(""))
            {

                if (savedInstanceState != null)
                {
                    this.vm.setMyId(savedInstanceState.getString(
                        BUNDLE_KEY_MY_ID, ""
                    ));
                }
                else if (this.getArguments() != null)
                {
                    this.vm.setMyId(this.getArguments().getString(
                        BUNDLE_KEY_MY_ID, ""
                    ));
                }
                // FIXME There IS a race condition here
                this.vm.setMyId(MainActivity.userId);

            }
            // FIXME Default user ID (empty string) is magic
            if (this.vm.getMyId().equals(""))
            {
                // TODO Handle
                Log.d(
                    MainActivity.TAG, String.format(
                        "%s.onCreate(), MyId is %s",
                        this.getClass().getCanonicalName(),
                        this.vm.getMyId()
                    )
                );
                return;
            }

            // Setup User ID
            // FIXME Default user ID (empty string) is magic
            if (this.vm.getUserId().equals(""))
            {
                if (savedInstanceState != null)
                {
                    this.vm.setUserId(savedInstanceState.getString(
                        BUNDLE_KEY_USER_ID, ""
                    ));
                }
                else if (this.getArguments() != null)
                {
                    this.vm.setUserId(this.getArguments().getString(
                        BUNDLE_KEY_USER_ID, ""
                    ));
                }
                // FIXME There IS a race condition here
                // FIXME This is using fake data effectively
                // This should NOT be set to the app user's ID
                this.vm.setMyId(MainActivity.userId);
            }
            // FIXME Default user ID (empty string) is magic
            if (this.vm.getUserId().equals(""))
            {
                // TODO Handle
                Log.d(MainActivity.TAG, String.format(
                    "%s.onCreate(), UserID is %s",
                    this.getClass().getCanonicalName(),
                    this.vm.getUserId()
                ));
                return;
            }

            // Setup the location services if we need it.
            this.geocoder = new Geocoder(this.getContext(), Locale.getDefault());
            this.locationManager
                = (LocationManager) this.requireActivity().getSystemService(
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

            // Fetch Firebase data asynchronously (eventually, somehow)
            this.pingFirebase();

        }
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
        if (locationManager != null) {
            this.locationManager.removeUpdates(this);
        }
        if (workerThread != null) {
            this.workerThread.quitSafely();
        }
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

    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY_MY_ID, this.vm.getMyId());
        outState.putString(BUNDLE_KEY_USER_ID, this.vm.getUserId());
    }

    // Unimplemented. We do not care about location provider status events
    public void onStatusChanged (String provider, int status, Bundle extras) {}

    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        // Set up the observers for all the relevant views
        this.viewAvatar = view.findViewById(R.id.profileViewAvatarImageView);
        if (this.viewAvatar != null)
        {
            this.viewAvatar.setOnClickListener(this::onAvatarClick);
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
            //noinspection ConstantConditions
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
            //noinspection ConstantConditions
            this.updateViewPets(this.vm.getPets().getValue());
        }
        this.viewPriceMin = view.findViewById(
            R.id.profileViewMinPriceValueTextView
        );
        if (this.viewPriceMin != null)
        {
            if (this.isMe())
            {
                this.vm.getPriceMin().observe(
                    this.getViewLifecycleOwner(), this::updateViewMinPrice
                );
                this.updateViewMinPrice(this.vm.getPriceMin().getValue());
            }
        }
        this.viewPriceMax = view.findViewById(
            R.id.profileViewMaxPriceValueTextView
        );
        if (this.viewPriceMax != null)
        {
            if (this.isMe())
            {
                this.vm.getPriceMax().observe(
                    this.getViewLifecycleOwner(), this::updateViewMaxPrice
                );
                this.updateViewMaxPrice(this.vm.getPriceMax().getValue());
            }
        }
        this.viewRadius = view.findViewById(R.id.profileViewRadiusTextView);
        if (this.viewRadius != null)
        {
            if (this.isMe())
            {
                this.vm.getRadius().observe(
                    this.getViewLifecycleOwner(), this::updateViewRadius
                );
                this.updateViewRadius(this.vm.getRadius().getValue());
                this.viewRadius.setVisibility(View.VISIBLE);
            }
        }
        this.viewSmoking = view.findViewById(R.id.profileViewSmokingTextView);
        if (this.viewSmoking != null)
        {
            this.vm.getSmoking().observe(
                this.getViewLifecycleOwner(), this::updateViewSmoking
            );
            //noinspection ConstantConditions
            this.updateViewSmoking(this.vm.getSmoking().getValue());
        }

        // Set the non-view observer for the location live field
        this.vm.getMyLocLive().observe(
            this.getViewLifecycleOwner(), this::onUpdateLocLive
        );

        // Load the image carousel
        this.viewCarousel = view.findViewById(R.id.profileViewCarouselFragView);
        this.fragImageCarousel
            = (ImageCarouselFragment)this.getChildFragmentManager()
            .findFragmentById(R.id.profileViewCarouselFragView);
        if (this.fragImageCarousel != null && this.viewCarousel != null)
        {

            this.vm.getImages().observe(
                this.getViewLifecycleOwner(), this::setImages
            );
            if (this.vm.getImages().getValue().isEmpty())
            {
                this.viewCarousel.setVisibility(View.GONE);
            }
            else
            {
                this.fragImageCarousel.setImages(vm.getImages().getValue());
            }

        }

        if (this.isMe())
        {
            View priceLayoutView = view.findViewById(
                R.id.profileViewPriceLayout
            );
            if (priceLayoutView != null)
            {
                priceLayoutView.setVisibility(View.VISIBLE);
            }
        }



    }

    public void setImages(List<Uri> images)
    {

        if (images == null)
        {
            return;
        }

        if (this.viewCarousel != null)
        {
            if (images.isEmpty())
            {
                this.viewCarousel.setVisibility(View.GONE);
            } else
            {
                this.viewCarousel.setVisibility(View.VISIBLE);
            }
        }
        if (this.fragImageCarousel != null)
        {
            this.fragImageCarousel.setImages(images);
        }

    }

    public void updateViewAvatar(Uri avatar)
    {
        if (avatar == null)
        {
            String name = this.vm.getProfileName().getValue();
            assert name != null;
            if (name.equals(""))
            {
                this.viewAvatar.setImageResource(R.drawable.ic_profile_24px);
            }
            else
            {
                this.viewAvatar.setImageBitmap(Utilities.nameToDrawable(name));
            }
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
            //noinspection ConstantConditions
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
            //noinspection ConstantConditions
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
    @SuppressWarnings("StatementWithEmptyBody")
    public void updateViewLocByMyLoc(LatLng myLoc)
    {

        if (this.isMe())
        {

            //noinspection ConstantConditions
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
            assert theirLoc != null;
            if (myLoc.equals(zero) || theirLoc.equals(zero))
            {
                return;
            }

            //noinspection ConstantConditions
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
        //noinspection ConstantConditions
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
        assert myLoc != null;
        if (myLoc.equals(zero) || userLoc.equals(zero))
        {
            return;
        }

        //noinspection ConstantConditions
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

    public void updateViewMinPrice(double price)
    {
        if (this.viewPriceMin != null)
        {

            if (price <= 0)
            {
                this.viewPriceMin.setText(
                    R.string.profile_view_min_price_value_placeholder
                );
            }
            else
            {
                this.viewPriceMin.setText(String.format(
                    Locale.getDefault(),
                    PRICE_FORMAT_STR,
                    price
                ));
            }

        }
    }

    public void updateViewMaxPrice(double price)
    {
        if (this.viewPriceMax != null)
        {

            if (price <= 0)
            {
                this.viewPriceMax.setText(
                    R.string.profile_view_max_price_value_placeholder
                );
            }
            else
            {
                this.viewPriceMax.setText(String.format(
                    Locale.getDefault(),
                    PRICE_FORMAT_STR,
                    price
                ));
            }

        }
    }

    public void updateViewRadius(double radius)
    {
        if (this.viewRadius != null)
        {

            if (radius <= 0 || radius > RADIUS_LIMIT)
            {
                this.viewRadius.setText(
                    R.string.profile_view_radius_placeholder
                );
            }
            else
            {
                // The HTML hack is to bold the distance in a string that's
                // probably too long for this purpose
                this.viewRadius.setText(HtmlCompat.fromHtml(
                    String.format(
                        Locale.getDefault(),
                        RADIUS_FORMAT_STR,
                        radius

                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ));
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
     * @return  Whether or not this profile view is of the app user
     */
    private boolean isMe()
    {
        if (this.vm.getMyId().equals(""))
        {
            return false;
        }
        return this.vm.getMyId().equals(this.vm.getUserId());
    }

    /**
     * Load fake data in lieu of Firebase, for testing
     */
    private void loadFakeData()
    {

        this.vm.getLoc().setValue(new LatLng(43.624794, -72.323171));
        this.vm.getMyLoc().setValue(new LatLng(43.704166, -72.288762));
        this.vm.getMyLocLive().setValue(true);
        this.vm.getMyLocStr().setValue("Sanborn");
        this.vm.getAvatarUri().setValue(null);

        List<Uri> images = new ArrayList<Uri>();
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart0));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart1));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart2));
        images.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart3));
        this.vm.getImages().setValue(images);

        this.vm.getPriceMin().setValue(42.42);
        this.vm.getPriceMax().setValue(4242.42);
        this.vm.getRadius().setValue(42.42);

    }

    private void pingFirebase()
    {

        // Get firebase wrapper (in-built)
        // Fetch profiles and loads them

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try
        {
            db.collection("profiles").whereEqualTo(
                FieldPath.documentId(), this.vm.getUserId()
            ).get().addOnCompleteListener(this::firebaseCallback);
        }
        // User ID doesn't exist
        catch (IllegalArgumentException ignored) {
            Log.d(
                MainActivity.TAG,
                this.getClass().getCanonicalName()
                + ".pingFirebase(), user does not exist in Firebase: "
                + this.vm.getUserId()
            );
        }

        //this.loadFakeData();

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