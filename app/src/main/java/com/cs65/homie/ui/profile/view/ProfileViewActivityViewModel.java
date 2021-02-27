package com.cs65.homie.ui.profile.view;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


/**
 * Static profile view view model
 */
@SuppressWarnings("Convert2Diamond")
public class ProfileViewActivityViewModel extends ViewModel
{

    private final MutableLiveData<Uri> avatarUri
        = new MutableLiveData<Uri>();
    private final MutableLiveData<Boolean> bathroom
        = new MutableLiveData<Boolean>(false);
    private final MutableLiveData<String> bio
        = new MutableLiveData<String>("");
    private final MutableLiveData<List<Uri>> imageUris
        = new MutableLiveData<List<Uri>>(
            new ArrayList<Uri>()
        );
    private final MutableLiveData<LatLng>    loc
        = new MutableLiveData<LatLng>(new LatLng(0, 0));
    private final MutableLiveData<String> gender
        = new MutableLiveData<String>("");
    private final MutableLiveData<LatLng> myLoc
        = new MutableLiveData<LatLng>(new LatLng(0, 0));
    private final MutableLiveData<Boolean> myLocLive
        = new MutableLiveData<Boolean>(false);
    private final MutableLiveData<String> myLocStr
        = new MutableLiveData<String>("");
    private long myId = -1;
    private final MutableLiveData<String> name
        = new MutableLiveData<String>("");
    private final MutableLiveData<Boolean> pets
        = new MutableLiveData<Boolean>(false);
    private final MutableLiveData<Boolean> place
        = new MutableLiveData<Boolean>(false);
    private final MutableLiveData<Boolean> smoking
        = new MutableLiveData<Boolean>(false);
    private long userId = -1;


    /**
     * Get the avatar URI
     *
     * @return  MutableLiveData wrapping he avatar URI.
     *          The underlying URI may be null
     */
    public MutableLiveData<Uri> getAvatarUri()
    {
        return this.avatarUri;
    }
    public MutableLiveData<Boolean> getBathroom()
    {
        return this.bathroom;
    }
    public MutableLiveData<String> getBio()
    {
        return this.bio;
    }
    public MutableLiveData<String> getGender()
    {
        return this.gender;
    }
    public MutableLiveData<List<Uri>> getimages()
    {
        return this.imageUris;
    }
    /**
     * Get the provided location of the profile user
     *
     * @return  MutableLiveData wrapping the provided location of the profile
     *          user. The underlying lat/long pair will never be null,
     *          but may be 0,0
     */
    public MutableLiveData<LatLng> getLoc()
    {
        return this.loc;
    }
    public MutableLiveData<Boolean> getPets()
    {
        return this.pets;
    }
    /**
     * Get the provided location of the app's user
     *
     * @return  MutableLiveData wrapping the provided location of the app's
     *          user. The underlying lat/long pair will never be null,
     *          but may be 0,0
     */
    public MutableLiveData<LatLng> getMyLoc()
    {
        return this.myLoc;
    }
    /**
     * Whether or not the app's user has elected to determine their location
     * in real time using GPS
     *
     * @return  MutableLiveData wrapping whether or not the app's user has
     *          elected to determine their location in real time using GPS
     */
    public MutableLiveData<Boolean> getMyLocLive()
    {
        return this.myLocLive;
    }
    /**
     * Get the location string of the app's user
     *
     * @return  MutableLiveData wrapping the location string the app's user
     *          has specified. The underlying string will never be null, but
     *          may be the empty string
     */
    public MutableLiveData<String> getMyLocStr()
    {
        return this.myLocStr;
    }

    /**
     * Get the Firebase identifier of the user of the app
     *
     * @return  The Firebase identifier of the user of the app
     */
    public long getMyId()
    {
        return this.myId;
    }

    /**
     * Get whether or not the profile user's has a place, rather than is
     * looking for a place
     *
     * @return  MutableLiveData wrapping whether or not the profile user has a
     *          place
     */
    public MutableLiveData<Boolean> getPlace()
    {
        return this.place;
    }
    public MutableLiveData<String> getProfileName()
    {
        return this.name;
    }
    public MutableLiveData<Boolean> getSmoking()
    {
        return this.smoking;
    }
    /**
     * Get the profile user's Firebase ID
     *
     * @return  The profile user's Firebase ID
     */
    public long getUserId()
    {
        return this.userId;
    }
    /**
     * Set the app user's Firebase ID
     *
     * This should be set with the ID passed from Firebase.
     * Default value is -1.
     *
     * @param id    The app user's Firebase ID
     * @return      The app user's updated Firebase ID
     */
    @SuppressWarnings("UnusedReturnValue")
    public long setMyId(long id)
    {
        this.myId = id;
        return this.myId;
    }
    /**
     * Set the profile user's Firebase ID
     *
     * This should be set with the ID passed from Firebase.
     * Default value is -1.
     *
     * @param id    The profile user's Firebase ID
     * @return      The profile user's updated Firebase ID
     */
    @SuppressWarnings("UnusedReturnValue")
    public long setUserId(long id)
    {
        this.userId = id;
        return this.userId;
    }

}