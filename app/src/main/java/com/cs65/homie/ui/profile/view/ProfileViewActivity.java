package com.cs65.homie.ui.profile.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.ThreadPerTaskExecutor;
import com.cs65.homie.Utilities;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;
import java.util.function.Consumer;


public class ProfileViewActivity
    extends AppCompatActivity
    implements Consumer<Location>
{

    public static final String BUNDLE_KEY_MY_ID
        = "BUNDLE_KEY_PROFILE_VIEW_MY_ID";
    public static final String BUNDLE_KEY_USER_ID
        = "BUNDLE_KEY_PROFILE_VIEW_USER_ID";

    private Geocoder geocoder = null;
    private LocationManager locationManager = null;
    private String locationProvider = null;
    private ImageView viewAvatar = null;
    private TextView viewBathroom = null;
    private TextView viewBio = null;
    private TextView viewGender = null;
    private TextView viewLoc = null;
    private TextView viewName = null;
    private TextView viewPets = null;
    private TextView viewSmoking = null;
    private ProfileViewActivityViewModel vm = null;

    public void accept(Location location)
    {
        if (location != null)
        {
            this.vm.getMyLoc().postValue(new LatLng(
                location.getLatitude(),
                location.getLongitude()
            ));
        }
    }

    public void updateAvatarUri(Uri avatar)
    {
        this.viewAvatar.setImageURI(null);
        this.viewAvatar.setImageURI(avatar);
    }

    public void updateBathroomTextBathroom(boolean bathroom)
    {
        if (this.vm == null)
        {
            Log.d(
                MainActivity.TAG,
                this.getClass().getCanonicalName()
                + ".updateBathroomTextBathroom(), VM is null"
            );
        }
        else
        {
            //noinspection ConstantConditions
            this.updateBathroomText(bathroom, this.vm.getPlace().getValue());
        }
    }
    public void updateBathroomTextPlace(boolean place)
    {
        if (this.vm != null)
        {
            //noinspection ConstantConditions
            this.updateBathroomText(this.vm.getBathroom().getValue(), place);
        }
    }

    public void updateBathroomText(boolean apartment, boolean bathroom)
    {
        if (this.viewBathroom == null)
        {
            return;
        }

        // FIXME Magic strings
        String ownership = "Has";
        if (!apartment)
        {
            ownership = "Wants";
        }
        // TODO The intention is that the final version will use icons rather
        // than unicode, but this is good for now
        String which = "✓";
        if (!bathroom)
        {
            which = "✖";
        }
        this.viewBathroom.setText(String.format(
            "%s Private Bathroom?  %s", ownership, which
        ));

    }

    public void updateBioView(String bio)
    {
        if (this.viewBio != null)
        {
            this.viewBio.setText(bio);
        }
    }

    public void updateGenderView(String gender)
    {
        // TODO Need to limit the length of this string before passing it along
        if (this.viewGender != null)
        {
            this.viewGender.setText(gender);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void updateLocViewMyLoc(LatLng myLoc)
    {

        if (this.isMe())
        {

            if (
                this.viewLoc != null
                && this.geocoder != null
                && this.vm.getMyLocLive().getValue()
            )
            {

                Address address = Utilities.latLngToAddress(
                    this.geocoder, myLoc
                );
                if (address != null)
                {
                    String addrStr = address.getAddressLine(0);
                    // FIXME Magic string
                    this.viewLoc.setText("Your current location: " + addrStr);
                }

            }
            else
            {
                // This branch represents a static location address that the
                // user has set
                // This is handled by updateMyLocStr and should not be updated
                // by this method
                return;
            }

        }

        LatLng zero = new LatLng(0, 0);
        LatLng theirLoc = this.vm.getLoc().getValue();
        if (myLoc.equals(zero) || theirLoc.equals(zero))
        {
            return;
        }

        this.updateLoc(myLoc, theirLoc, this.vm.getPlace().getValue());

    }

    @SuppressWarnings("ConstantConditions")
    public void updateLocViewUserLoc(LatLng userLoc)
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

        this.updateLoc(myLoc, userLoc, this.vm.getPlace().getValue());

    }

    public void updateLocViewMyStr(String myLoc)
    {

        //noinspection ConstantConditions
        if (
            this.viewLoc == null
            || !this.isMe()
            || this.vm.getMyLocLive().getValue()
        )
        {
            return;
        }

        this.viewLoc.setText(myLoc);

    }

    public void updateNameView(String name)
    {
        if (this.viewName != null)
        {
            this.viewName.setText(name);
        }
    }

    public void updatePetsView(boolean pets)
    {

        if (this.viewPets == null)
        {
            return;
        }

        // TODO The intention is that the final version will use icons rather
        // than unicode, but this is good for now
        // FIXME Magic strings
        String which;
        if (pets)
        {
            which = "✓";
        }
        else
        {
            which = "✖";
        }
        this.viewBathroom.setText(String.format(
            Locale.getDefault(),
            "Pet Friendly?  %s", which
        ));

    }

    public void updateSmokingView(boolean smoking)
    {

        if (this.viewSmoking == null)
        {
            return;
        }

        // TODO The intention is that the final version will use icons rather
        // than unicode, but this is good for now
        // FIXME Magic strings
        String which;
        if (smoking)
        {
            which = "\uD83D\uDEAC";
        }
        else
        {
            which = "\uD83D\uDEAD";
        }
        this.viewSmoking.setText(String.format(
            Locale.getDefault(),
            "Smoking?  %s", which
        ));

    }

    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        // Get the view model instance
        this.vm = new ViewModelProvider(this).get(
            ProfileViewActivityViewModel.class
        );
        if (this.vm == null)
        {
            // TODO Handle
            Log.d(
                MainActivity.TAG,
                this.getClass().getCanonicalName() + ".onCreate(), ViewModel is null"
            );
            return;
        }

        // Setup My ID
        if (this.vm.getMyId() < 0 && this.getIntent() != null)
        {
            this.vm.setMyId(this.getIntent().getLongExtra(
                BUNDLE_KEY_MY_ID, -1
            ));
        }
        if (this.vm.getMyId() < 0)
        {
            // TODO Handle
            Log.d(
                MainActivity.TAG, String.format(
                    "%s.onCreate(), MyId is %d",
                    this.getClass().getCanonicalName(), this.vm.getMyId()
                )
            );
            return;
        }

        // Setup User ID
        if (this.vm.getUserId() < 0 && this.getIntent() != null)
        {
            this.vm.setUserId(this.getIntent().getLongExtra(
                BUNDLE_KEY_USER_ID, -1
            ));
        }
        if (this.vm.getUserId() < 0)
        {
            // TODO Handle
            Log.d(
                MainActivity.TAG, String.format(
                    "%s.onCreate(), UserID is %d",
                    this.getClass().getCanonicalName(), this.vm.getUserId()
                )
            );
            return;
        }

        // Fetch Firebase data asynchronously
        this.pingFirebase();

        // Setup the location services if we need it.
        this.geocoder = new Geocoder(this, Locale.getDefault());
        this.locationManager
            = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Criteria locCriteria = new Criteria();
        locCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        this.locationProvider
            = this.locationManager.getBestProvider(locCriteria, true);

        // Load the layout
        this.setContentView(R.layout.activity_profile_view);

        // Set up the observers for all the relevant views
        this.viewAvatar = this.findViewById(R.id.profileViewAvatarImageView);
        if (this.viewAvatar != null)
        {
            this.vm.getAvatarUri().observe(this, this::updateAvatarUri);
            Uri avatarUri = this.vm.getAvatarUri().getValue();
            if (avatarUri != null)
            {
                this.updateAvatarUri(avatarUri);
            }
        }
        this.viewBathroom = this.findViewById(R.id.profileViewBathroomTextView);
        if (this.viewBathroom != null)
        {
            this.vm.getBathroom().observe(this, this::updateBathroomTextBathroom);
            this.vm.getPlace().observe(this, this::updateBathroomTextPlace);
            this.updateBathroomText(
                this.vm.getBathroom().getValue(),
                this.vm.getPlace().getValue()
            );
        }
        this.viewBio = this.findViewById(R.id.profileViewBioTextView);
        if (this.viewBio == null)
        {
            this.vm.getBio().observe(this, this::updateBioView);
            this.updateBioView(this.vm.getBio().getValue());
        }
        this.viewGender = this.findViewById(R.id.profileViewGenderTextView);
        if (this.viewGender != null)
        {
            this.vm.getGender().observe(this, this::updateGenderView);
            this.updateGenderView(this.vm.getGender().getValue());
        }
        this.viewLoc = this.findViewById(R.id.profileViewLocationTextView);
        if (this.viewLoc != null)
        {
            this.vm.getLoc().observe(this, this::updateLocViewUserLoc);
            this.vm.getMyLoc().observe(this, this::updateLocViewMyLoc);
            this.vm.getMyLocStr().observe(this, this::updateLocViewMyStr);
            // Don't prime with default values
        }
        this.viewName = this.findViewById(R.id.profileViewNameTextView);
        if (this.viewName != null)
        {
            this.vm.getProfileName().observe(this, this::updateNameView);
            this.updateNameView(this.vm.getProfileName().getValue());
        }
        this.viewPets = this.findViewById(R.id.profileViewPetsTextView);
        if (this.viewPets != null)
        {
            this.vm.getPets().observe(this, this::updatePetsView);
            this.updatePetsView(this.vm.getPets().getValue());
        }
        this.viewSmoking = this.findViewById(R.id.profileViewSmokingTextView);
        if (this.viewSmoking != null)
        {
            this.vm.getSmoking().observe(this, this::updateSmokingView);
            this.updateSmokingView(this.vm.getSmoking().getValue());
        }

    }

    protected void onResume()
    {

        super.onResume();
        //noinspection ConstantConditions
        if (
            this.vm.getMyLocLive().getValue()
            && Utilities.checkPermissionLocation(this)
        )
        {
            this.getCurrentLoc();
            LatLng loc = this.getLastLoc();
            if (loc != null)
            {
                this.vm.getMyLoc().setValue(loc);
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void getCurrentLoc()
    {

        if (this.locationManager != null && this.locationProvider != null)
        {
            this.locationManager.getCurrentLocation(
                this.locationProvider,
                null,
                new ThreadPerTaskExecutor(),
                this
            );
        }

    }

    @SuppressLint("MissingPermission")
    private LatLng getLastLoc()
    {

        LatLng loc = null;

        if (this.locationManager != null && this.locationProvider != null)
        {
            Location location = this.locationManager.getLastKnownLocation(
                this.locationProvider
            );
            if (location != null)
            {
                loc = new LatLng(
                    location.getLatitude(),
                    location.getLongitude()
                );
            }

        }

        return loc;

    }

    private boolean isMe()
    {
        return this.vm.getMyId() == this.vm.getUserId();
    }

    private void pingFirebase() {}

    private void updateLoc(LatLng myLoc, LatLng theirLoc, boolean place)
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

        // FIXME Magic strings
        String placeStr;
        if (place)
        {
            placeStr = "Has a place";
        }
        else
        {
            placeStr = "Is looking for a place";
        }
        this.viewLoc.setText(String.format(
            Locale.getDefault(),
            "%s %.1f miles from you",
            placeStr, distance
        ));

    }

}