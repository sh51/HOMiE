package com.cs65.homie.ui;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.cs65.homie.FirebaseHelper;
import com.cs65.homie.Globals;
import com.cs65.homie.models.GenderEnum;
import com.cs65.homie.ui.login.ui.login.LoginActivity;
import com.cs65.homie.ui.login.ui.login.RegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.soundcloud.android.crop.Crop;

import com.cs65.homie.R;
import com.cs65.homie.Utilities;
import com.cs65.homie.models.Profile;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileSettingsActivity extends AppCompatActivity {

    // TODO: Make better pattern, just testing functionality
    public static String userID = null;

    private FirebaseHelper mHelper;
    private ImageView photoView;
    private String tempImgFileName = "temp.png";
    private EditText editedName, editedEmail, changedPassword, mEditBudgetMax, mEditBudgetMin, mEditAddress, mEditRadius;
    private TextInputEditText bio;
    private String tempImgHomeName = "temp.png";
    private RadioButton radioFemale, radioMale, radioNoPref;
    private Spinner housingSearchOptions;
    private Switch isPetFriendly, isSmoking, isPrivateBathroom;

    private Uri photoUri, houseUri;
    private int gender;
    private boolean hasApartment;
    private String photoPath, name, email, password,
            genderPref, housingSearch, housePhotoPath,
            address, radius, minBudget, maxBudget,
            petFriendly, noneSmoking, privateBathroom;

    private static final int RC_LOGIN = 0;

    public static final int CAMERA_REQUEST_CODE = 1;
    private static final int SELECT_MULTIPLE_IMG = 2;
    private List<String> housing_images;
    private ImageView housingImgView;

    private static final String TAG = "ajb";

    private SharedPreferences savedProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        mHelper = FirebaseHelper.getInstance();
        Profile myProfile = mHelper.getMyProfile();
        SharedPreferences savedProfile = getSharedPreferences(getString(R.string.saved_preferences), MODE_PRIVATE);


        this.editedName = (EditText) findViewById(R.id.editText_Name);
        this.editedEmail = (EditText) findViewById(R.id.editText_Email);
        this.changedPassword = (EditText) findViewById(R.id.editText_Password);
        this.radioFemale = (RadioButton) findViewById(R.id.radioButton_female);
        this.radioMale = (RadioButton) findViewById(R.id.radioButton_male);
        this.radioNoPref = (RadioButton) findViewById(R.id.radioButton_nopref);
        this.photoView = (ImageView) findViewById(R.id.photoView);
        this.housingSearchOptions = (Spinner) findViewById(R.id.spinnerNeedHousing);
        mEditBudgetMax = findViewById(R.id.editBudgetMax);
        mEditBudgetMin = findViewById(R.id.editBudgetMin);
        mEditAddress = findViewById(R.id.editTextPostalAddress);
        mEditRadius = findViewById(R.id.editTextRadius);
        bio = (TextInputEditText) findViewById(R.id.bio);

        isPetFriendly = (Switch) findViewById(R.id.switchPetFriendly);
        isSmoking = (Switch) findViewById(R.id.switchNoneSmoking);
        isPrivateBathroom = (Switch) findViewById(R.id.switchPrivateBathroom);

        Button changeHousing = (Button) findViewById(R.id.button_ChangeHousingPhoto);
        TextView changeHousingImgPrompt = (TextView) findViewById(R.id.textView_HousingImageView);
        this.housingImgView = (ImageView) findViewById(R.id.imageViewHousing);

        Utilities.checkPermission(this);


        File tempImgFile = new File(getExternalFilesDir(null), tempImgFileName);
        File tempHomeImgFile = new File(getExternalFilesDir(null), tempImgHomeName);
        this.photoUri = FileProvider.getUriForFile(
                this, "com.cs65.homie.ui", tempImgFile);
        this.houseUri = FileProvider.getUriForFile(
                this, "com.cs65.homie.ui", tempHomeImgFile);
        changeHousing.setVisibility(View.GONE);
        changeHousingImgPrompt.setVisibility(View.GONE);
        housingImgView.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> housingAdapter = ArrayAdapter.createFromResource(
                this, R.array.spinner_HousingSearchOptions, android.R.layout.simple_spinner_item);
        housingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        housingSearchOptions.setAdapter(housingAdapter);
        housingSearchOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Have Housing. Just looking for a Roommate.")) {
                    changeHousing.setVisibility(View.VISIBLE);
                    changeHousingImgPrompt.setVisibility(View.VISIBLE);
                    housingImgView.setVisibility(View.VISIBLE);
                    hasApartment = true;
                } else {
                    changeHousing.setVisibility(View.GONE);
                    changeHousingImgPrompt.setVisibility(View.GONE);
                    housingImgView.setVisibility(View.GONE);
                    hasApartment = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // load data from fetched profile
        this.photoPath = savedProfile.getString(getString(R.string.key_filename), null);
        if (photoPath != null) photoView.setImageURI(Uri.fromFile(new File(this.photoPath)));
        editedName.setText(myProfile.getFirstName());
        editedEmail.setText(myProfile.getEmail());
        isPetFriendly.setChecked(myProfile.isPetFriendly());
        isSmoking.setChecked(!myProfile.isSmoking());
        isPrivateBathroom.setChecked(myProfile.isPrivateBathroom());
        if (myProfile.getGender() == 0) {
            radioFemale.setChecked(true);
            gender = 0;
        } else if (myProfile.getGender() == 1) {
            radioMale.setChecked(true);
            gender = 1;
        } else { radioNoPref.setChecked(true); gender = 2; }
        housingSearchOptions.setSelection(myProfile.isHasApartment() ? 1 : 0);
        this.housePhotoPath = savedProfile.getString(getString(R.string.key_housingImg), null);
        mEditAddress.setText(myProfile.getAddress());
        mEditRadius.setText(String.valueOf(myProfile.getRadius()));
        mEditBudgetMin.setText(String.valueOf(myProfile.getMinPrice()));
        mEditBudgetMax.setText(String.valueOf(myProfile.getMaxPrice()));
        bio.setText(myProfile.getBio());


//        this.photoView.setImageURI(this.photoUri);
        this.loadProfile();

    }

    /**
     * Save location of temporary profile picture
     * Reloaded on onCreate
     *
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.key_filename), this.photoPath);

    }

    public void onChangePhotoClicked(View view) {
        Log.d(TAG, "onChangedPhotoClicked");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.photoUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    // TODO ADD FUNCTIONALITY FOR ADDING MULTIPLE IMAGES
    public void onChangeHousingPhotoClicked(View view) {
        Log.d(TAG, "onChangeHousingPhotoClicked");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_MULTIPLE_IMG);

//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.houseUri);
//        startActivityForResult(intent, SELECT_MULTIPLE_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d(TAG, Integer.toString(resultCode));
        if (resultCode != RESULT_OK) return;
        if (requestCode == CAMERA_REQUEST_CODE) {
            Log.d(TAG, "requested camera");
            Crop.of(photoUri, photoUri).asSquare().start(this);
        } else if (requestCode == Crop.REQUEST_CROP) {
            Log.d(TAG, "requested crop");
            Uri tempUri = Crop.getOutput(intent);
            photoView.setImageURI(null);
            photoView.setImageURI(tempUri);
            photoPath = photoUri.getPath();
        }
//        else if (requestCode == SELECT_MULTIPLE_IMG) {
//            this.housingImgView.setImageURI(houseUri);
//            housePhotoPath = houseUri.getPath();
//        }
        else if (requestCode == SELECT_MULTIPLE_IMG && intent != null) {
            String[] filePathCol = {MediaStore.Images.Media.DATA};
            housing_images = new ArrayList<String>();
            if (intent.getData() != null) {
                Uri uri = intent.getData();
                Cursor cursor = getContentResolver().query(houseUri, filePathCol, null, null, null);
                cursor.moveToFirst();

                int ind = cursor.getColumnIndex(filePathCol[0]);
                this.housePhotoPath = cursor.getString(ind);
                this.housing_images.add(this.housePhotoPath);
                cursor.close();

            } else {
                if (intent.getClipData() != null) {
                    ClipData clipData = intent.getClipData();
                    ArrayList<Uri> uriArrayList = new ArrayList<Uri>();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        uriArrayList.add(uri);
                        Cursor cursor = getContentResolver().query(uri, filePathCol, null, null, null);
                        cursor.moveToFirst();
                        int colInd = cursor.getColumnIndex(filePathCol[0]);
                        this.housePhotoPath = cursor.getString(colInd);
                        this.housing_images.add(this.housePhotoPath);
                        cursor.close();

                    }
                    Log.d(Globals.TAG, "select images");
                }
            }
        }
    }


    public void onSaveClicked(View view) {
        this.hideKeyboard(view);
        this.saveProfile();
        finish();
    }

    public void onCancelClicked(View view) {
        this.hideKeyboard(view);
        finish();
    }

    public void onLogoutClicked(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, RC_LOGIN);
//        finish();
    }

    public void onGenderRadioToggled(View view) {
        gender = radioFemale.isChecked() ? 0 : (radioMale.isChecked() ? 1 : 2);

        if (view.getId() == R.id.radioButton_female)
            this.genderPref = getString(R.string.radio_Female_text);
        else if (view.getId() == R.id.radioButton_male)
            this.genderPref = getString(R.string.radio_Male_text);
        else if (view.getId() == R.id.radioButton_nopref)
            this.genderPref = getString(R.string.radio_NoPref_text);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputManager
                = (InputMethodManager) this.getSystemService(
                Context.INPUT_METHOD_SERVICE
        );
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * load user data that has already been saved
     */
    private void loadProfile() {
        this.savedProfile = getSharedPreferences(getString(R.string.saved_preferences), MODE_PRIVATE);
        if (this.name != null) editedName.setText(this.name);
        if (this.email != null) editedEmail.setText(this.email);
        if (this.genderPref != null) {
            if (this.genderPref == getString(R.string.radio_Male_text)) radioMale.setChecked(true);
            else if (this.genderPref == getString(R.string.radio_Female_text))
                radioFemale.setChecked(true);
            else radioNoPref.setChecked(true);
        }
        if (this.photoPath != null) photoView.setImageURI(Uri.fromFile(new File(this.photoPath)));
    }

    /**
     * save user data using a SharedPreference object
     * after calling saveProfile(), let user know that data is saved
     */
    private void saveProfile() {
        Log.d(TAG, "in saveProfile");
        this.name = editedName.getText().toString();
        this.email = editedEmail.getText().toString();

        SharedPreferences savedProfile = getSharedPreferences(getString(R.string.saved_preferences), MODE_PRIVATE);

        // Not sure if we want to cache this so I'll leave this here, but we should also create a profile at this point
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
        Log.d(TAG, "toasted");

        // Firebase update
        final Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", name);
        updates.put("email", email);
        // TODO change auth too
        updates.put("password", password);
        updates.put("bio", bio.getText().toString());
        Log.d(Globals.TAG,  "updating gender to: " + gender);
        updates.put("gender", gender);
        updates.put("hasApartment", hasApartment);
        updates.put("petFriendly", isPetFriendly.isChecked());
        updates.put("smoking", !isSmoking.isChecked());
        updates.put("privateBathroom", isPrivateBathroom.isChecked());
        updates.put("id", mHelper.getUid());

        double minPrice, maxPrice, radius;
        // TODO call geocoder to translate the address
        String address = mEditAddress.getText().toString();

        try {
            minPrice = Double.valueOf(mEditBudgetMin.getText().toString());
            maxPrice = Double.valueOf(mEditBudgetMax.getText().toString());
            radius = Double.valueOf(mEditRadius.getText().toString());
        } catch (Exception e) {
            Log.d(Globals.TAG, "Cast price failed.");
            minPrice = 600;
            maxPrice = 1000;
            radius = 10;
        }

        updates.put("minPrice", minPrice);
        updates.put("maxPrice", maxPrice);
        updates.put("radius", radius);
        updates.put("address", address);
//        mHelper.createProfile(newProfile);
        mHelper.updateProfile(mHelper.getUid(), updates);

    }
}
