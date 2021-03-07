package com.cs65.homie;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cs65.homie.models.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    private FirebaseAuth mAuth;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Auth endpoints
    // Fetch account from ID
    // Fetch profile


    // Create profile
    public static void createProfile(String uid, Profile profile) {
        if (uid == null) {
            Log.d(Globals.TAG, "CreateProfile: null user.");
            return;
        }

        Log.d(Globals.TAG, "Profile hasApartment - " + profile.isHasApartment());

        Map<String, Object> p = new HashMap<>();
        p.put("uid", uid);
        // Strings
        p.put("bio", profile.getBio());
        p.put("bio", profile.getBio());
        // numbers
        p.put("hasApartment", profile.isHasApartment());
        p.put("isPetFriendly", profile.isPetFriendly());
        p.put("isSmoking", profile.isSmoking());
        p.put("privateBathroom", profile.isPrivateBathroom());
        p.put("gender", profile.getGender());
        p.put("minPrice", profile.getMinPrice());
        p.put("maxPrice", profile.getMaxPrice());
        p.put("radius", profile.getRadius());
        // geolocation
        // TODO async geodecoding?
        p.put("location", profile.getLocation());
        // TODO arrays


// Add a new document with a generated ID
        db.collection("profiles")
                .add(p)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(Globals.TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Globals.TAG, "Error adding document", e);
                    }
                });
    }

    // Update profile
    public static void updateProfile() {

    }


    // Delete profile
    public static void deleteProfile(String profileId) {

    }
}
