package com.cs65.homie;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cs65.homie.models.Message;
import com.cs65.homie.models.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private HashMap<String, Profile> matchedProfiles;
    private String server_key;

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
        matchedProfiles = new HashMap<>();
        chatListeners = new HashMap<>();


        savePushToken();


//        // TODO fetch profiles from FireStore
//        Profile profile = new Profile();
//        // test profile: logged in user
//        profile.setFirstName("Timothy");
//        profile.setAvatarImage(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.background_rounded_corners_blue);
//        profile.setId(getUid());
//        profile.setLikes(new ArrayList<>(Arrays.asList("43", "44")));
//        // test profile: Dave
//        Profile profile1 = new Profile();
//        profile1.setFirstName("Dave");
//        profile1.setAvatarImage(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart0);
//        profile1.setId("43");
//        profile1.setLikes(new ArrayList<>(Arrays.asList("oLpRxjVdRPadydoHnvBLLU8Mq3f2")));
//
//        // test profile: the Lord Bennington
//        Profile profile2 = new Profile();
//        profile2.setFirstName("The Lord Bennington of Bennington's in Bennington");
//        profile2.setId("44");
//        profile2.setLikes(new ArrayList<>(Arrays.asList("oLpRxjVdRPadydoHnvBLLU8Mq3f2")));

//        profiles.put(profile.getId(), profile);
//        profiles.put(profile1.getId(), profile1);
//        profiles.put(profile2.getId(), profile2);

        // TODO also call updateMatchedProfiles when the profiles gets updated
    }

    public String getUid() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // Auth endpoints
    // Fetch account from ID
    // Fetch profile


    // Create profile
    public void createProfile(Profile profile) {
        if (profile.getId() == null) {
            Log.d(Globals.TAG, "Error creating profile: uid empty.");
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles").document(profile.getId())
                .set(profile)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Globals.TAG, "Error setting document", e);
                    }
                });

//        if (uid == null) {
//            Log.d(Globals.TAG, "CreateProfile: null user.");
//            return;
//        }
//
//        Log.d(Globals.TAG, "Profile hasApartment - " + profile.isHasApartment());
//
//        Map<String, Object> p = new HashMap<>();
//        p.put("uid", uid);
//        // Strings
//        p.put("bio", profile.getBio());
//        p.put("address", profile.getAddress());
//        p.put("email", profile.getEmail());
//        p.put("password", profile.getPassword());
//        p.put("firstName", profile.getFirstName());
//        p.put("avatarImage", profile.getAvatarImage());
//        // numbers
//        p.put("hasApartment", profile.isHasApartment());
//        p.put("isPetFriendly", profile.isPetFriendly());
//        p.put("isSmoking", profile.isSmoking());
//        p.put("privateBathroom", profile.isPrivateBathroom());
//        p.put("gender", profile.getGender());
//        p.put("minPrice", profile.getMinPrice());
//        p.put("maxPrice", profile.getMaxPrice());
//        p.put("radius", profile.getRadius());
//        // geolocation
//        // TODO async geodecoding?
//        p.put("location", profile.getLocation());
//        // TODO array fields
    }

    // Update profile
    public void updateProfile(String userId, Map<String, Object> data) {
        db.collection("profiles")
                .document(userId)
                .update(data);
    }


    // Delete profile
    public void deleteProfile(String profileId) {

    }

    public void fetchProfile(String uid, Utilities.onProfileFetchedCallbackInterface callback) {
        if (uid == null) {
            Log.d(Globals.TAG, "FirebaseHelper: fetchProfile called with empty uid.");
            return;
        }

        Query q = db.collection("profiles").whereEqualTo("id", uid);
        q.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        Profile p = document.toObject(Profile.class);
                        profiles.put(uid, p);
                        callback.run(p);
                    } catch (Exception e) {
                        Log.d(Globals.TAG, "fetchProfile: cast to profile failed.\n+" + e.toString());
                    }
                    break;
                }
            } else {
                Log.d(Globals.TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    public void fetchProfiles(Utilities.onProfilesFetchedCallbackInterface callback) {
        Query q = db.collection("profiles");
        q.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        Profile p = document.toObject(Profile.class);
                        String uid = p.getId();
                        if (uid != null) profiles.put(uid, p);
//                        if (document.toObject(Profile.class).getId() != null) Log.d(Globals.TAG, document.toObject(Profile.class).toString());
                    } catch (Exception e) {
                        Log.d(Globals.TAG, "fetchProfiles: cast to profile failed.\n" + e);
                    }
                }
                updateMatchedProfiles();
                callback.run(getProfiles());
            } else {
                Log.d(Globals.TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    // Get certain profile
    public Profile getProfile(String uid) {
        return profiles.get(uid);
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
        return new ArrayList<Profile>(matchedProfiles.values());
    }

    // refresh matched profiles from the list of profiles
    private void updateMatchedProfiles() {
        String uid = getUid();
        Profile userProfile = profiles.get(uid);

        // likes is an array of userIds, iterate through likes to populate matched profiles
        userProfile.getLikes().forEach((id) -> {
            // since profiles is supposed to be a complete set of profiles, so this is guaranteed to be non-null
            Profile p = profiles.get(id);
            if (profiles.get(id).getLikes() != null && profiles.get(id).getLikes().contains(uid)) matchedProfiles.put(id, p);
        });
    }
    //
    // like a profile, the callback is only called when there is a new match
    public void like(String uid, Utilities.onLikeCallbackInterface callback) {
        String currId = getUid();
        Profile userProfile = profiles.get(currId), likedProfile = profiles.get(uid);;

        // first update firestore profile
        final Map<String, Object> update = new HashMap<>();
        update.put("likes", FieldValue.arrayUnion(uid));  // arrayUnion would not generate duplicates
        updateProfile(currId, update);
        // upon success update local profile
        List<String> likes = userProfile.getLikes();
        if (!likes.contains(uid)) likes.add(uid);
        // lastly check if matched profiles needs an update
        // * likes might not be initialized when they haven't liked anyone yet
        if (likedProfile.getLikes() != null && likedProfile.getLikes().contains(currId)) {
            Log.d(Globals.TAG, "Matched with: " + uid);
            // update matched profiles if there is a match
            matchedProfiles.put(uid, likedProfile);
            // execute callback
            callback.run(true);
        } else { callback.run(false); }
    }

    // unlike a profile
    public static void unlike(String uid, Utilities.onUnlikeCallbackInterface callback) {

    }

    // Chat & notification related functions: !must be logged in

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
                Log.d(Globals.TAG, "Message sent.");
                // message sent, execute callback
                callback.run(msg);
                // send out push notif
                DatabaseReference pts = rdb.getReference("pts/" + receiverId);
                pts.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String messageTitle = "You've got a new message.";
                        if (profiles.get(msg.getSenderId()) != null && profiles.get(msg.getSenderId()).getFirstName() != null)
                            messageTitle = profiles.get(msg.getSenderId()).getFirstName();

                        // TODO replace the userId with username
                        if (dataSnapshot.getValue() != null)
                            sendPushNotification(dataSnapshot.getValue(String.class), msg.getSenderId(), messageTitle, msg.getText());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                    }
                });

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
        ChildEventListener chatListener = chatListeners.get(uid);
        DatabaseReference ref = rdb.getReference("chats/" + getPairId(currId, uid));

        chatListener = new ChildEventListener() {
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
//        Log.d(Globals.TAG, "childEventListner added");
    }

    // unload messages of certain user - this should happen when an unmatch event occurs
    public void unloadMessages(String uid, Utilities.onMessagesUnloadedCallbackInterface callback) {
        ChildEventListener chatListener = chatListeners.get(uid);
        chatListeners.put(uid, null);
        if (chatListener != null) {
            String currId = getUid();
            DatabaseReference ref = rdb.getReference("chats/" + getPairId(currId, uid));
            ref.removeEventListener(chatListener);
            callback.run();
        }
    }


    // load the server key with context
    public void loadServerKey(Context ctxt) {
        String string = "";
        StringBuilder stringBuilder = new StringBuilder();

        try {
            InputStream is = ctxt.getResources().openRawResource(R.raw.env);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                if ((string = reader.readLine()) == null) break;
                stringBuilder.append(string).append("\n");
            }

            is.close();
            server_key = stringBuilder.toString();
            Log.d(Globals.TAG, "Server key loaded.");
        } catch (Exception e) {
            Log.d(Globals.TAG, "Load server key failed.");
        }

    }

    // send a push notification to a certain user
    public void sendPushNotification(String token, String uid, String title, String text) {
        if (server_key == null) return;
        Log.d(Globals.TAG, "Sending push notification to " + token);

        HandlerThread thread = new HandlerThread("pushNotification");
        thread.start();
        final Handler handler = new Handler(thread.getLooper());
        handler.postDelayed(() -> {
            try {
                String request = Globals.FCM_API;
                URL url = new URL(request);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "key=" + server_key);
                conn.setUseCaches(false);
                conn.connect();
                JSONObject body = new JSONObject();
                body.put("to", token);
                JSONObject notification = new JSONObject();
                notification.put("title", title + " sent you a message:");
                notification.put("body", text);
                JSONObject data = new JSONObject();
                data.put("senderId", uid);
//                data.put("title", title);
//                data.put("text", text);
                body.put("notification", notification);
                body.put("data", data);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(body.toString().getBytes());
                // Read the response
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                Log.d(Globals.TAG, "Push notification sent.");
//                Log.d(Globals.TAG, sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }


            thread.quit();
        }, 1000);
    }

    // save the push tokens to realtime database
    private void savePushToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(Globals.TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    // save the token
                    DatabaseReference ref = rdb.getReference("pts/" + getUid());
                    ref.setValue(token, (err, prevRef) -> {
                        Log.d(Globals.TAG, "Push notification saved.");
                    });

                });
    }

    // util function, map two userId to a chat id
    public static String getPairId(String id1, String id2) {
        return (id1 != null && id2 != null) ? id1.compareTo(id2) < 0 ? id1 + id2 : id2 + id1 : null;
    }

}
