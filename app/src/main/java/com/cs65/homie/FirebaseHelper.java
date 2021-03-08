package com.cs65.homie;

import android.content.ContentResolver;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cs65.homie.models.Message;
import com.cs65.homie.models.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FirebaseHelper {
    private FirebaseAuth mAuth;


    private HashMap<String, ChildEventListener> chatListeners;
    private HashMap<String, Profile> profiles;
    private List<Profile> matchedProfiles;

    private FirebaseFirestore db;
    // realtime database object
    private FirebaseDatabase rdb;

    private static FirebaseHelper inst = null;

    public static FirebaseHelper getInstance() {
        if (inst == null)
            inst = new FirebaseHelper();

        return inst;
    }
    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
        rdb = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        profiles = new HashMap<>();
        chatListeners = new HashMap<>();
        matchedProfiles = new ArrayList<>();

        // TODO fetch profiles from FireStore
        Profile profile = new Profile();
        // test profile: logged in user
        profile.setFirstName("Timothy");
        profile.setAvatarImage(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.background_rounded_corners_blue);
        profile.setId(getUid());
        profile.setLikes(new ArrayList<>(Arrays.asList("43", "44")));
        // test profile: Dave
        Profile profile1 = new Profile();
        profile1.setFirstName("Dave");
        profile1.setAvatarImage(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart0);
        profile1.setId("43");
        profile1.setLikes(new ArrayList<>(Arrays.asList("oLpRxjVdRPadydoHnvBLLU8Mq3f2")));

        // test profile: the Lord Bennington
        Profile profile2 = new Profile();
        profile2.setFirstName("The Lord Bennington of Bennington's in Bennington");
        profile2.setId("44");
        profile2.setLikes(new ArrayList<>(Arrays.asList("oLpRxjVdRPadydoHnvBLLU8Mq3f2")));

        profiles.put(profile.getId(), profile);
        profiles.put(profile1.getId(), profile1);
        profiles.put(profile2.getId(), profile2);

        // TODO also call updateMatchedProfiles when the profiles gets updated
        updateMatchedProfiles();
    }

    public String getUid() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // Auth endpoints
    // Fetch account from ID
    // Fetch profile


    // Create profile
    public void createProfile(String uid, Profile profile) {
        if (uid == null) {
            Log.d(Globals.TAG, "CreateProfile: null user.");
            return;
        }

        Log.d(Globals.TAG, "Profile hasApartment - " + profile.isHasApartment());

        Map<String, Object> p = new HashMap<>();
        p.put("uid", uid);
        // Strings
        p.put("bio", profile.getBio());
        p.put("address", profile.getAddress());
        p.put("email", profile.getEmail());
        p.put("password", profile.getPassword());
        p.put("firstName", profile.getFirstName());
        p.put("avatarImage", profile.getAvatarImage());
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
        // TODO array fields


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

    // Get all the profiles
    public List<Profile> getProfiles() {
        return new ArrayList<Profile>(profiles.values());
    }

    // Get profiles suggestions - these will be displayed on the match screen
    public List<Profile> getSuggestedProfiles() {
        return new ArrayList<Profile>(profiles.values());
    }
    // Get matched profiles for chat initialization - return the matched profiles, need to be called after a MatchedProfile update
    public List<Profile> getMatchedProfiles() {
        return matchedProfiles;
    }

    // refresh matched profiles from the list of profiles
    private void updateMatchedProfiles() {
        String uid = getUid();
        Profile userProfile = profiles.get(uid);

        // likes is an array of userIds, iterate through likes to populate matched profiles
        userProfile.getLikes().forEach((id) -> {
            // since profiles is supposed to be a complete set of profiles, so this is guaranteed to be non-null
            Profile p = profiles.get(id);
            if (profiles.get(id).getLikes().contains(uid)) matchedProfiles.add(p);
        });
    }


        // Chat related functions: !must be logged in

    // call sendMessage with receiverId, text and a callback for ui change
    public void sendMessage(String receiverId, String text, Utilities.onMessageSentCallbackInterface callback) {
        String uid = getUid();
        if (uid == null) {
            Log.d(Globals.TAG, "SendMessage: user not logged in.");
            return;
        }
        if (receiverId == null) {
            Log.d(Globals.TAG, "SendMessage: receiverId is null.");
            return;
        }

        Message msg = new Message();
        msg.setSenderId(uid);
        msg.setReceiverId(receiverId);
        msg.setText(text);
        msg.setTimestamp(System.currentTimeMillis());


        Log.d(Globals.TAG, msg.getSenderId() + " sending \"" + msg.getText() + "\" to " + msg.getReceiverId() + ".");

        // determine chat id from sender/receiver id
        String cid = msg._getPairId();

        DatabaseReference ref = rdb.getReference("chats/" + cid);

        // push message to the messages list
        ref.push().setValue(msg, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                callback.run(msg);
                Log.d(Globals.TAG, "Message sent.");
            }
        });


    }

    // remove a message
    public void removeMessage(Message msg) {
        if (msg.getMid() == null || !msg._isValid()) return;
        rdb.getReference("chats/" + msg._getPairId() + "/" + msg.getMid()).setValue(null, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Log.d(Globals.TAG, "Message removed.");
            }
        });
    }

    // load messages of certain user
    public void loadMessages(String uid, Utilities.onMessagesLoadedCallbackInterface callback) {
        String currId = getUid();
        DatabaseReference ref = rdb.getReference("chats/" + getPairId(currId, uid));

        ChildEventListener chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message msg = snapshot.getValue(Message.class);
                msg.setMid(snapshot.getKey());
//                Log.d(Globals.TAG, "Got message with id: " + snapshot.getKey());
                callback.run(msg);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Message msg = snapshot.getValue(Message.class);
                msg.setMid(snapshot.getKey());
                // set timestamp to -1 to mark this as a deleted message
                msg.setTimestamp(-1L);
                Log.d(Globals.TAG, "Message with id: " + snapshot.getKey() + " removed.");
                callback.run(msg);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        chatListeners.put(uid, chatListener);
        ref.orderByChild("timestamp").addChildEventListener(chatListener);
        Log.d(Globals.TAG, "childEventListner added");
    }

    // unload messages of certain user - this should happen when an unmatch event occurs
    public void unloadMessages(String uid, Utilities.onMessagesUnloadedCallbackInterface callback) {
        ChildEventListener chatListener = chatListeners.get(uid);
        if (chatListener != null) {
            String currId = getUid();
            DatabaseReference ref = rdb.getReference("chats/" + getPairId(currId, uid));
            ref.removeEventListener(chatListener);
            callback.run();
        }
    }

    // register to chat updates: for simplicity all updates under /chats would be received and filtered
    // only pertinent chat messages would be presented
    public void addChatEventListener(Utilities.onChatUpdatedCallbackInterface callback) {
        String uid = getUid();
        DatabaseReference ref = rdb.getReference("chats");


    }
    // remove ChatEventListener
    public void removeChatEventListener() {
//        if (chatListener != null) rdb.getReference("chats").removeEventListener(chatListener);
    }

    // like a user
    public static void like(String uid, Utilities.onLikeCallbackInterface callback) {

    }

    // unlike a user
    public static void unlike(String uid, Utilities.onUnlikeCallbackInterface callback) {

    }

    // util function, map two userId to a chat id
    public static String getPairId(String id1, String id2) {
        return (id1 != null && id2 != null) ? id1.compareTo(id2) < 0 ? id1 + id2 : id2 + id1 : null;
    }

}
