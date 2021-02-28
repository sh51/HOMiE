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
import android.widget.Toast;

import java.io.File;

public class ProfileSettingsActivity extends AppCompatActivity {

    private ImageView photoView;
    private String tempImgFileName = "temp.png";
    private EditText editedName, editedEmail, editedPhone, editedClass, editedMajor;
    private RadioButton radioFemale, radioMale;

    private Uri photoUri;
    private String photoPath, name, email, phone, gender, classYr, major;

    public static final int CAMERA_REQUEST_CODE = 1;

    private static final String DEBUGGING_TAG = "ajb";

//    private SharedPreferences savedProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences savedProfile = getSharedPreferences(getString(R.string.saved_preferences), MODE_PRIVATE);

        this.editedName = (EditText) findViewById(R.id.editText_Name);
        this.editedEmail = (EditText) findViewById(R.id.editText_Email);
        this.editedPhone = (EditText) findViewById(R.id.editText_Phone);
        this.radioFemale = (RadioButton) findViewById(R.id.radioButton_female);
        this.radioMale = (RadioButton) findViewById(R.id.radioButton_male);
        this.editedClass = (EditText) findViewById(R.id.editText_Class);
        this.editedMajor = (EditText) findViewById(R.id.editText_Major);
        this.photoView = (ImageView) findViewById(R.id.photoView);

        Util.checkPermission(this);

        File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName);
        this.photoUri = FileProvider.getUriForFile(
                this, "com.example.abigail_bartolome_myruns1", tempImgFile);

        if (savedInstanceState != null) {
            this.photoPath = savedInstanceState.getString(getString(R.string.key_filename));
            this.name = savedInstanceState.getString(getString(R.string.key_name));
            this.email = savedInstanceState.getString(getString(R.string.key_email));
            this.phone = savedInstanceState.getString(getString(R.string.key_phone));
            this.gender = savedInstanceState.getString(getString(R.string.key_gender));
            this.classYr = savedInstanceState.getString(getString(R.string.key_class));
            this.major = savedInstanceState.getString(getString(R.string.key_major));
        }
        else {
            this.photoPath = savedProfile.getString(getString(R.string.key_filename), null);
            this.name = savedProfile.getString(getString(R.string.key_name), null);
            this.email = savedProfile.getString(getString(R.string.key_email), null);
            this.phone = savedProfile.getString(getString(R.string.key_phone), null);
            this.classYr = savedProfile.getString(getString(R.string.key_class), null);
            this.major = savedProfile.getString(getString(R.string.key_major), null);
            this.gender = savedProfile.getString(getString(R.string.key_gender), null);
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
            this.gender = getString(R.string.radio_Female_text);
        else if (view.getId() == R.id.radioButton_male)
            this.gender = getString(R.string.radio_Male_text);
    }

    /**
     * load user data that has already been saved
     */
    private void loadProfile(){
        Log.d(DEBUGGING_TAG, "in load profile");
//        this.savedProfile = getSharedPreferences(getString(R.string.saved_preferences), MODE_PRIVATE);
        if (this.name != null) editedName.setText(this.name);
        if (this.email != null) editedEmail.setText(this.email);
        if (this.phone != null) editedPhone.setText(this.phone);
        if (this.classYr != null) editedClass.setText(this.classYr);
        if (this.major != null) editedMajor.setText(this.major);
        if (this.gender != null) {
            if (this.gender == getString(R.string.radio_Female_text)) radioFemale.setChecked(true);
            else radioMale.setChecked(true);
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
        this.phone = editedPhone.getText().toString();
        this.classYr = editedClass.getText().toString();
        this.major = editedMajor.getText().toString();

        SharedPreferences savedProfile = getSharedPreferences(getString(R.string.saved_preferences), MODE_PRIVATE);

        SharedPreferences.Editor editedProfile = savedProfile.edit();
        editedProfile.putString(getString(R.string.key_name), this.name);
        editedProfile.putString(getString(R.string.key_email), this.email);
        editedProfile.putString(getString(R.string.key_phone), this.phone);
        editedProfile.putString(getString(R.string.key_class), this.classYr);
        editedProfile.putString(getString(R.string.key_major), this.major);
        editedProfile.putString(getString(R.string.key_gender), this.gender);
        editedProfile.putString(getString(R.string.key_filename), this.photoPath);
        editedProfile.apply();
        Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
        Log.d(DEBUGGING_TAG, "toasted");

    }
}