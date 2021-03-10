package com.cs65.homie.ui.profile.view;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.cs65.homie.FirebaseHelper;
import com.cs65.homie.Globals;
import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.Utilities;
import com.cs65.homie.models.GenderEnum;
import com.cs65.homie.models.Profile;
import com.cs65.homie.ui.ImageFullScreenActivity;
import com.cs65.homie.ui.carousel.ImageCarouselFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Static profile view activity
 */
@SuppressWarnings("Convert2Diamond")
public class ProfileViewFragment extends Fragment
{
    private FirebaseHelper mHelper;

    @SuppressWarnings("unused")
    public static final String BUNDLE_KEY_MY_ID
        = "BUNDLE_KEY_PROFILE_VIEW_MY_ID";
    @SuppressWarnings("unused")
    public static final String BUNDLE_KEY_USER_ID
        = "BUNDLE_KEY_PROFILE_VIEW_USER_ID";
    public static final String PLACE_FORMAT_STR
        = "%s %.1f miles from you";
    public static final String PLACE_HAS_STR = "Has a place";
    public static final String PLACE_WANTS_STR = "Is looking for a place";
    public static final String RADIUS_FORMAT_STR
        = "Finding Homies within <b>%.1f</b> miles";
    public static final double RADIUS_LIMIT = 2000;
    public static final String PRICE_FORMAT_STR = "$%.2f";

    private ImageCarouselFragment fragImageCarousel = null;
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

    // Aliases for easy access
    MutableLiveData<String> bio;
    MutableLiveData<Boolean> bathroom;
    MutableLiveData<Boolean> pets;
    MutableLiveData<Boolean> hasPlace;
    MutableLiveData<Boolean> isSmoking;
    MutableLiveData<String> name;

    ///// ///// /////

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

        // Get the view model instance
        // ViewModel can never be null
        if (this.vm == null)
        {
            this.vm = new ViewModelProvider(this).get(
                ProfileViewFragmentViewModel.class
            );
        }

        mHelper = FirebaseHelper.getInstance();
        bio = this.vm.getBio();
        bathroom = this.vm.getBathroom();
        pets = this.vm.getPets();
        hasPlace = this.vm.getPlace();
        isSmoking = this.vm.getSmoking();
        name = this.vm.getProfileName();

        // Setup User ID
        // This is not necessarily the app owner's ID
        if (this.vm.getUserId().equals(""))
        {
            if (this.getArguments() != null)
            {
                this.vm.setUserId(
                    this.getArguments().getString(BUNDLE_KEY_USER_ID, "")
                );
            }
            else if (savedInstanceState != null)
            {
                this.vm.setUserId(
                    savedInstanceState.getString(BUNDLE_KEY_USER_ID, "")
                );
            }
            else
            {
                this.vm.setUserId(this.mHelper.getUid());
            }
        }
    }

    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    )
    {
        // profile is loaded in onResume
//        this.getProfile(this.vm.getUserId());
        //this.loadFakeData();

        // Load the layout
        return inflater.inflate(
            R.layout.fragment_profile_view, container, false
        );
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        CardView cardView = view.findViewById(R.id.profileViewCardView);
        cardView.setRadius(0);

        // Set up the observers for all the relevant views
        this.viewAvatar = view.findViewById(R.id.profileViewAvatarImageView);
        if (this.viewAvatar != null)
        {
            this.viewAvatar.setOnClickListener(this::onAvatarClick);
            this.vm.getAvatarUri().observe(
                this.getViewLifecycleOwner(), this::updateViewAvatar
            );
            Uri avatarUri = this.vm.getAvatarUri().getValue();
            this.updateViewAvatar(avatarUri);
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
                //noinspection ConstantConditions
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
                //noinspection ConstantConditions
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
                //noinspection ConstantConditions
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
            //noinspection ConstantConditions
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

    @Override
    public void onResume() {
        super.onResume();

        this.getProfile(this.vm.getUserId());
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
            }
            else
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

        if (this.viewAvatar == null)
        {
            return;
        }

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

    public void updateViewGender(GenderEnum gender)
    {
        if (this.viewGender != null)
        {
            switch (gender)
            {
                case FEMALE:
                    this.viewGender.setText(R.string.profile_view_gender_male);
                    break;
                case MALE:
                    this.viewGender.setText(R.string.profile_view_gender_female);
                    break;
                default:
                    this.viewGender.setText(R.string.profile_view_gender_none);
            }
        }
    }

    /**
     * Update the location text view upon a change to the app user's location
     *
     * @param myLoc     The app user's location
     */
    public void updateViewLocByMyLoc(LatLng myLoc)
    {

        if (!this.isMe())
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
     * This husk of a function exists for historical reason, but should not be
     * removed due to observer dependencies
     *
     * @param myLoc     The app user's location string
     */
    public void updateViewLocByMyString(String myLoc)
    {
        if (
            this.viewLoc != null
            && this.isMe()
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

        if (this.isMe())
        {
            return;
        }
        LatLng zero = new LatLng(0, 0);
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
        this.updateViewAvatar(this.vm.getAvatarUri().getValue());
    }

    public void updateViewPets(boolean pets)
    {
        if (this.viewPets != null)
        {
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

    protected void loadProfile(Profile profile)
    {
        // Update UI
        name.setValue(profile.getFirstName());
        bathroom.setValue(profile.isPrivateBathroom());
        bio.setValue(profile.getBio());
        this.vm.getGender().setValue(
            GenderEnum.fromInt(profile.getGender())
        );
        pets.setValue(profile.isPetFriendly());
        hasPlace.setValue(profile.isHasApartment());
        isSmoking.setValue(profile.isSmoking());
        vm.getPriceMin().setValue(profile.getMinPrice());
        vm.getPriceMax().setValue(profile.getMaxPrice());
        if (profile.getAvatarImage() != null)
        {
            Log.d(
                MainActivity.TAG,
                this.getClass().getCanonicalName()
                + "loadProfile(), profile avatar image string: "
                + profile.getAvatarImage()
            );
            vm.getAvatarUri().setValue(Uri.parse(profile.getAvatarImage()));
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
        if (this.mHelper.getUid().equals(""))
        {
            return false;
        }
        return this.mHelper.getUid().equals(this.vm.getUserId());
    }

    /**
     * Load fake data in lieu of Firebase, for testing
     */
    @SuppressWarnings("unused")
    private void loadFakeData()
    {

        this.vm.getLoc().setValue(new LatLng(43.624794, -72.323171));
        this.vm.getMyLoc().setValue(new LatLng(43.704166, -72.288762));
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

    private void getProfile(String myId) {

        Profile p = mHelper.getProfile(myId);
        if (p == null)
        {
            Log.d(
                MainActivity.TAG,
                this.getClass().getCanonicalName()
                + ".loadProfile(), profile was null: "
                + myId
            );
        }
        else
        {
            this.loadProfile(p);
        }

    }

    private void updateViewBathroom(boolean bathroom, boolean place)
    {
        if (this.viewBathroom != null)
        {
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