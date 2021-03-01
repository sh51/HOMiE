package com.cs65.homie.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.soundcloud.android.crop.Crop;

import com.cs65.homie.R;
import com.cs65.homie.Utilities;

import java.io.File;

public class ProfileSettingsActivity extends AppCompatActivity {

    private ImageView photoView;
    private String tempImgFileName = "temp.png";
    private EditText editedName, editedEmail, changedPassword;
    private RadioButton radioFemale, radioMale, radioNoPref;
    private Spinner housingSearchOptions;

    private Uri photoUri;
    private String photoPath, name, email, password,
            genderPref, housingSearch,
            petFriendly, noneSmoking, privateBathroom;

    public static final int CAMERA_REQUEST_CODE = 1;

    private static final String DEBUGGING_TAG = "ajb";

//    private SharedPreferences savedProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        SharedPreferences savedProfile = getSharedPreferences(getString(R.string.saved_preferences), MODE_PRIVATE);

        this.editedName = (EditText) findViewById(R.id.editText_Name);
        this.editedEmail = (EditText) findViewById(R.id.editText_Email);
        this.changedPassword = (EditText) findViewById(R.id.editText_Password);
        this.radioFemale = (RadioButton) findViewById(R.id.radioButton_female);
        this.radioMale = (RadioButton) findViewById(R.id.radioButton_male);
        this.radioNoPref = (RadioButton) findViewById(R.id.radioButton_nopref);
        this.photoView = (ImageView) findViewById(R.id.photoView);
        this.housingSearchOptions = (Spinner) findViewById(R.id.spinnerNeedHousing);

        Utilities.checkPermission(this);

        File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName);
        this.photoUri = FileProvider.getUriForFile(
                this, "com.cs65.homie.ui", tempImgFile);

        if (savedInstanceState != null) {
            this.photoPath = savedInstanceState.getString(getString(R.string.key_filename));
            this.name = savedProfile.getString(getString(R.string.key_name), null);
            this.email = savedInstanceState.getString(getString(R.string.key_email));
            this.petFriendly = savedProfile.getString(getString(R.string.key_petFriendly), null);
            this.noneSmoking = savedProfile.getString(getString(R.string.key_noneSmoking), null);
            this.privateBathroom = savedProfile.getString(getString(R.string.key_privateBathroom), null);
            this.genderPref = savedProfile.getString(getString(R.string.key_genderpref), null);
            this.housingSearch = savedProfile.getString(getString(R.string.key_lookingForHousing), null);
        }
        else {
            this.photoPath = savedProfile.getString(getString(R.string.key_filename), null);
            this.name = savedProfile.getString(getString(R.string.key_name), null);
            this.email = savedProfile.getString(getString(R.string.key_email), null);
            this.petFriendly = savedProfile.getString(getString(R.string.key_petFriendly), null);
            this.noneSmoking = savedProfile.getString(getString(R.string.key_noneSmoking), null);
            this.privateBathroom = savedProfile.getString(getString(R.string.key_privateBathroom), null);
            this.genderPref = savedProfile.getString(getString(R.string.key_genderpref), null);
            this.housingSearch = savedProfile.getString(getString(R.string.key_lookingForHousing), null);

        }

//        this.photoView.setImageURI(this.photoUri);

        this.loadProfile();

    }

    /**
     * Save location of temporary profile picture
     * Reloaded on onCreate
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.key_filename), this.photoPath);

    }

    public void onChangePhotoClicked(View view) {
        Log.d(DEBUGGING_TAG, "onChangedPhotoClicked");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.photoUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d(DEBUGGING_TAG, Integer.toString(resultCode));
        if(resultCode != RESULT_OK) return;
        if(requestCode == CAMERA_REQUEST_CODE) {
            Log.d(DEBUGGING_TAG, "requested camera");
            Crop.of(photoUri, photoUri).asSquare().start(this);
        }
        else if (requestCode == Crop.REQUEST_CROP) {
            Log.d(DEBUGGING_TAG, "requested crop");
            Uri tempUri = Crop.getOutput(intent);
            photoView.setImageURI(null);
            photoView.setImageURI(tempUri);
            photoPath = photoUri.getPath();
        }
    }
    public void onSaveClicked(View view) {
        this.saveProfile();
        finish();
    }

    public void onCancelClicked(View view) {
        finish();
    }

    public void onGenderRadioToggled(View view) {
        if (view.getId() == R.id.radioButton_female)
            this.genderPref = getString(R.string.radio_Female_text);
        else if (view.getId() == R.id.radioButton_male)
            this.genderPref = getString(R.string.radio_Male_text);
        else if (view.getId() == R.id.radioButton_nopref)
            this.genderPref = getString(R.string.radio_NoPref_text);
    }

    /**
     * load user data that has already been saved
     */
    private void loadProfile(){
        Log.d(DEBUGGING_TAG, "in load profile");
//        this.savedProfile = getSharedPreferences(getString(R.string.saved_preferences), MODE_PRIVATE);
        if (this.name != null) editedName.setText(this.name);
        if (this.email != null) editedEmail.setText(this.email);
        if (this.genderPref != null) {
            if (this.genderPref == getString(R.string.radio_Male_text)) radioMale.setChecked(true);
            else if (this.genderPref == getString(R.string.radio_Female_text)) radioFemale.setChecked(true);
            else radioNoPref.setChecked(true);
        }
        if (this.photoPath != null) photoView.setImageURI(Uri.fromFile(new File(this.photoPath)));
    }

    /**
     * save user data using a SharedPreference object
     * after calling saveProfile(), let user know that data is saved
     */
    private void saveProfile(){
        Log.d(DEBUGGING_TAG, "in saveProfile");
        this.name = editedName.getText().toString();
        this.email = editedEmail.getText().toString();

        SharedPreferences savedProfile = getSharedPreferences(getString(R.string.saved_preferences), MODE_PRIVATE);

        SharedPreferences.Editor editedProfile = savedProfile.edit();
        editedProfile.putString(getString(R.string.key_name), this.name);
        editedProfile.putString(getString(R.string.key_email), this.email);
        editedProfile.putString(getString(R.string.key_password), this.password);
        editedProfile.putString(getString(R.string.key_genderpref), this.genderPref);
        editedProfile.putString(getString(R.string.key_lookingForHousing), this.housingSearch);
        editedProfile.putString(getString(R.string.key_petFriendly), this.petFriendly);
        editedProfile.putString(getString(R.string.key_noneSmoking), this.noneSmoking);
        editedProfile.putString(getString(R.string.key_privateBathroom), this.privateBathroom);
        editedProfile.putString(getString(R.string.key_filename), this.photoPath);
        editedProfile.apply();
        Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
        Log.d(DEBUGGING_TAG, "toasted");

    }
}