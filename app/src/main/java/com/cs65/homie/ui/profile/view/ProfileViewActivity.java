package com.cs65.homie.ui.profile.view;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cs65.homie.MainActivity;
import com.cs65.homie.R;

import java.util.Locale;


public class ProfileViewActivity extends AppCompatActivity
{

    public static final String BUNDLE_KEY_USER_ID
        = "BUNDLE_KEY_PROFILE_VIEW_USER_ID";

    private ImageView avatarView = null;
    private TextView bathroomView = null;
    private TextView bioView = null;
    private TextView genderView = null;
    private TextView locView = null;
    private TextView nameView = null;
    private TextView petsView = null;
    private TextView placeView = null;
    private TextView smokingView = null;
    private ProfileViewActivityViewModel vm = null;

    public void updateAvatarUri(Uri avatar)
    {
        this.avatarView.setImageURI(null);
        this.avatarView.setImageURI(avatar);
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
            this.updateBathroomText(bathroom, this.vm.getPlace().getValue());
        }
    }
    public void updateBathroomTextPlace(boolean place)
    {
        if (this.vm != null)
        {
            this.updateBathroomText(this.vm.getBathroom().getValue(), place);
        }
    }

    public void updateBathroomText(boolean apartment, boolean bathroom)
    {
        if (this.bathroomView == null)
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
        this.bathroomView.setText(String.format(
            "%s Private Bathroom?  %s", ownership, which
        ));

    }

    public void updateBioView(String bio)
    {
        if (this.bioView != null)
        {
            this.bioView.setText(bio);
        }
    }

    public void updateGenderView(String gender)
    {
        if (this.genderView != null)
        {
            this.genderView.setText(gender);
        }
    }

    public void updateNameView(String name)
    {
        if (this.nameView != null)
        {
            this.nameView.setText(name);
        }
    }

    public void updatePetsView(boolean pets)
    {

        if (this.petsView == null)
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
        this.bathroomView.setText(String.format(
            Locale.getDefault(),
            "Pet Friendly?  %s", which
        ));

    }

    public void updateHasPlaceView(boolean place)
    {

        if (this.placeView == null)
        {
            return;
        }

        // FIXME Magic strings
        if (place)
        {
            this.placeView.setText("Has a place");
        }
        else
        {
            this.placeView.setText("Is looking for a place");
        }

    }

    public void updateSmokingView(boolean smoking)
    {

        if (this.smokingView == null)
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
        this.smokingView.setText(String.format(
            Locale.getDefault(),
            "Smoking?  %s", which
        ));

    }

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

        // Load the layout
        this.setContentView(R.layout.activity_profile_view);

        // Set up the observers for all the relevant views
        this.avatarView = this.findViewById(R.id.profileViewAvatarImageView);
        if (this.avatarView != null)
        {
            this.vm.getAvatarUri().observe(this, this::updateAvatarUri);
            Uri avatarUri = this.vm.getAvatarUri().getValue();
            if (avatarUri != null)
            {
                this.updateAvatarUri(avatarUri);
            }
        }
        this.bathroomView = this.findViewById(R.id.profileViewBathroomTextView);
        if (this.bathroomView != null)
        {
            this.vm.getBathroom().observe(this, this::updateBathroomTextBathroom);
            this.vm.getPlace().observe(this, this::updateBathroomTextPlace);
            this.updateBathroomText(
                this.vm.getBathroom().getValue(),
                this.vm.getPlace().getValue()
            );
        }
        this.bioView = this.findViewById(R.id.profileViewBioTextView);
        if (this.bioView == null)
        {
            this.vm.getBio().observe(this, this::updateBioView);
            this.updateBioView(this.vm.getBio().getValue());
        }
        this.genderView = this.findViewById(R.id.profileViewGenderTextView);
        if (this.genderView != null)
        {
            this.vm.getGender().observe(this, this::updateGenderView);
            this.updateGenderView(this.vm.getGender().getValue());
        }
        // TODO Locs view
        this.nameView = this.findViewById(R.id.profileViewNameTextView);
        if (this.nameView != null)
        {
            this.vm.getProfileName().observe(this, this::updateNameView);
            this.updateNameView(this.vm.getProfileName().getValue());
        }
        this.petsView = this.findViewById(R.id.profileViewPetsTextView);
        if (this.placeView != null)
        {
            this.vm.getPets().observe(this, this::updatePetsView);
            this.updatePetsView(this.vm.getPets().getValue());
        }
        this.placeView = this.findViewById(R.id.profileViewHasPlaceTextView);
        if (this.placeView != null)
        {
            this.vm.getPlace().observe(this, this::updateHasPlaceView);
            this.updateHasPlaceView(this.vm.getPlace().getValue());
        }
        this.smokingView = this.findViewById(R.id.profileViewSmokingTextView);
        if (this.smokingView != null)
        {
            this.vm.getSmoking().observe(this, this::updateSmokingView);
            this.updateSmokingView(this.vm.getSmoking().getValue());
        }

    }

    private void pingFirebase() {}

}