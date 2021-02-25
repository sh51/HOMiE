package com.cs65.homie.ui.profile.view;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;


@SuppressWarnings("Convert2Diamond")
public class ProfileViewActivityViewModel extends ViewModel
{

    private final MutableLiveData<Uri> avatarUri
        = new MutableLiveData<Uri>();
    private final MutableLiveData<Boolean> bathroom
        = new MutableLiveData<Boolean>(false);
    private final MutableLiveData<String> bio
        = new MutableLiveData<String>("");
    private final MutableLiveData<LatLng> loc
        = new MutableLiveData<LatLng>(new LatLng(0, 0));
    private final MutableLiveData<String> locStr
        = new MutableLiveData<String>("");
    private final MutableLiveData<String> gender
        = new MutableLiveData<String>("");
    private final MutableLiveData<LatLng> myLoc
        = new MutableLiveData<LatLng>(new LatLng(0, 0));
    private final MutableLiveData<Boolean> myLocLive
        = new MutableLiveData<Boolean>(false);
    private final MutableLiveData<String> myLocStr
        = new MutableLiveData<String>("");
    private final MutableLiveData<String> name
        = new MutableLiveData<String>("");
    private final MutableLiveData<Boolean> pets
        = new MutableLiveData<Boolean>(false);
    private final MutableLiveData<Boolean> place
        = new MutableLiveData<Boolean>(false);
    private final MutableLiveData<Boolean> smoking
        = new MutableLiveData<Boolean>(false);
    private long userId = -1;

    public MutableLiveData<Uri> getAvatarUri()
    {
        // FIXME Underlying URI can be null
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
    public MutableLiveData<LatLng> getLoc()
    {
        return this.loc;
    }
    public MutableLiveData<String> getLocStr()
    {
        return this.locStr;
    }
    public MutableLiveData<Boolean> getPets()
    {
        return this.pets;
    }
    public MutableLiveData<LatLng> getMyLoc()
    {
        return this.myLoc;
    }
    public MutableLiveData<Boolean> getMyLocLive()
    {
        return this.myLocLive;
    }
    public MutableLiveData<String> getMyLocStr()
    {
        return this.myLocStr;
    }
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
    public long getUserId()
    {
        return this.userId;
    }
    @SuppressWarnings("UnusedReturnValue")
    public long setUserId(long id)
    {
        this.userId = id;
        return this.userId;
    }

}