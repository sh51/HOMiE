package com.cs65.homie.ui.chats;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.FirebaseHelper;
import com.cs65.homie.Globals;
import com.cs65.homie.R;
import com.cs65.homie.models.Message;
import com.cs65.homie.models.Profile;
import com.cs65.homie.ui.profile.view.ProfileViewActivity;
import com.cs65.homie.ui.profile.view.ProfileViewFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * A fragment for chats, a view containing all outstanding chats with
 * other users
 *
 * Users can open a chart with other users from this fragment
 */
@SuppressWarnings("Convert2Diamond")
public class ChatsFragment extends Fragment
{
    private static final int PROFILE_VIEW_ACTIVITY_RESPONSE_CODE = 22564;

    private FirebaseHelper mHelper;
    private ChatsRecyclerAdapter adapter = null;
    private boolean inProfile = false;
    // Defensive coding
    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView viewRecycler = null;
    private TextView viewTextWarning = null;
    private ChatsViewModel vm = null;


    public void onActivityResult (
        int requestCode, int resultCode, Intent data
    )
    {
        if (requestCode == PROFILE_VIEW_ACTIVITY_RESPONSE_CODE)
        {
            this.inProfile = false;
        }
    }

    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        // Returned VM cannot be null
        this.vm = new ViewModelProvider(
            this.requireActivity()).get(ChatsViewModel.class
        );

        mHelper = FirebaseHelper.getInstance();
        this.loadChats(mHelper.getMatchedProfiles());
        //this.loadFakeData();

    }

    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(
            R.layout.fragment_chats, container, false
        );
    }

    public void onMessagesUpdate(List<Message> messages)
    {

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String myId = sharedPref.getString("userId", "");

        // if the messages list is not empty, get the other user
        // (might be sender or receiver) or the first message, and invalidate
        // that chat recycler view, to update the time and message preview
        if (!messages.isEmpty())
        {
            String receiverId = messages.get(0).getReceiverId();
            if (receiverId.equals(myId))
            {
                receiverId = messages.get(0).getSenderId();
            }
            int position = this.adapter.getUserPosition(receiverId);
            if (position >= 0)
            {
                this.adapter.notifyItemChanged(position);
            }
        }

    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        this.viewTextWarning = view.findViewById(R.id.chatsEmptyTextView);
        if (
            this.viewTextWarning != null
            && !this.vm.getUsers().getValue().isEmpty()
        )
        {
            this.viewTextWarning.setVisibility(View.GONE);
        }

        this.viewRecycler = view.findViewById(R.id.chatsRecyclerView);
        if (this.viewRecycler != null)
        {

            LinearLayoutManager layoutManager
                = new LinearLayoutManager(this.getContext());
            this.viewRecycler.addItemDecoration(new DividerItemDecoration(
                this.requireContext(), layoutManager.getOrientation()
            ));
            this.viewRecycler.setLayoutManager(layoutManager);
            this.adapter = new ChatsRecyclerAdapter(this, this.vm);
            this.viewRecycler.setAdapter(this.adapter);
            this.vm.getUsersMessages().observe(
                this.getViewLifecycleOwner(), this::invalidateRecycler
            );
            // Observe all the messages live data
            //noinspection ConstantConditions
            for (
                MutableLiveData<List<Message>> messages
                : this.vm.getUsersMessages().getValue().values()
            )
            {
                messages.observe(
                    this.getViewLifecycleOwner(), this::onMessagesUpdate
                );
            }

        }

    }

    public void invalidateRecycler(
        Map<String, MutableLiveData<List<Message>>> usersMessages
    )
    {

        if (this.viewTextWarning != null && !usersMessages.isEmpty())
        {
            this.viewTextWarning.setVisibility(View.GONE);
        }

        if (this.adapter != null)
        {
            this.adapter.notifyDataSetChanged();
            // Any one of the messages may have changed,
            // so we must observe them all
            for (MutableLiveData<List<Message>> messages : usersMessages.values())
            {
                messages.observe(
                    this.getViewLifecycleOwner(), this::onMessagesUpdate
                );
            }
        }

    }

    public void spawnProfileView(String userId)
    {

        if (this.inProfile)
        {
            return;
        }

        this.inProfile = true;

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String myId = sharedPref.getString("userId", "");

        Intent intent = new Intent(this.getContext(), ProfileViewActivity.class);
        intent.putExtra(ProfileViewFragment.BUNDLE_KEY_USER_ID, userId);
        intent.putExtra(ProfileViewFragment.BUNDLE_KEY_MY_ID, myId);
        this.startActivity(intent);

    }

    private void loadFakeData()
    {

        Profile profile1 = new Profile();
        profile1.setFirstName("Dave");
        profile1.setAvatarImage(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.cs65.homie/" + R.drawable.dart0);
        profile1.setId("43");
        Profile profile2 = new Profile();
        profile2.setFirstName("The Lord Bennington of Bennington's in Bennington");
        profile2.setId("44");

        Message message1 = new Message();
        message1.setText("This is a test message. Oh good day it's a test message. Indeed!");
        message1.setDatetime(Calendar.getInstance().getTime());
        message1.setReceiverId("42");
        message1.setSenderId("43");
        Message message2 = new Message();
        message2.setText(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do "
            + "eiusmod tempor incididunt ut labore et dolore magna aliqua. "
            + "Ut enim ad minim veniam, quis nostrud exercitation ullamco "
            + "laboris nisi ut aliquip ex ea commodo consequat. Duis aute "
            + "irure dolor in reprehenderit in voluptate velit esse cillum "
            + "dolore eu fugiat nulla pariatur. Excepteur sint occaecat "
            + "cupidatat non proident, sunt in culpa qui officia deserunt "
            + "mollit anim id est laborum."
        );
        message2.setDatetime(Calendar.getInstance().getTime());
        message2.setReceiverId("43");
        message2.setSenderId("42");
        Message message3 = new Message();
        message3.setText("Abba");
        message3.setDatetime(Calendar.getInstance().getTime());
        message3.setReceiverId("43");
        message3.setSenderId("42");

        List<Message> messages = new ArrayList<Message>();
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        TreeMap<String, MutableLiveData<List<Message>>> usersMessages = this.vm.getUsersMessages().getValue();
        TreeMap<String, Profile> users = this.vm.getUsers().getValue();

        //noinspection ConstantConditions
        usersMessages.put(profile1.getId(), new MutableLiveData<List<Message>>(messages));
        //noinspection ConstantConditions
        users.put(profile1.getId(), profile1);
        usersMessages.put(profile2.getId(), new MutableLiveData<List<Message>>());
        users.put(profile2.getId(), profile2);

        this.vm.getUsersMessages().setValue(usersMessages);
        this.vm.getUsers().setValue(users);

    }

    private void loadChats(List<Profile> profiles) {

        if (profiles == null) profiles = new ArrayList<>();

        TreeMap<String, Profile> users = this.vm.getUsers().getValue();
        TreeMap<String, MutableLiveData<List<Message>>> usersMessages = this.vm.getUsersMessages().getValue();

        // user profiles and chats initialized
        profiles.forEach((profile) -> {
            users.put(profile.getId(), profile);
            usersMessages.put(profile.getId(), new MutableLiveData<List<Message>>());
        });

        this.vm.getUsersMessages().setValue(usersMessages);
        this.vm.getUsers().setValue(users);

        // load chats
        profiles.forEach((profile) -> {
            // initialize the message list for each profile
            usersMessages.get(profile.getId()).setValue(new ArrayList<>());
            List<Message> msgs = usersMessages.get(profile.getId()).getValue();

            mHelper.loadMessages(profile.getId(), (changedMsg -> {
                if (changedMsg.getTimestamp() == -1) {
                    // this message is removed
                    Log.d(Globals.TAG, "Removing message...");
                    for (int i = 0; i < msgs.size(); i++)
                        if (msgs.get(i).getMid().equals(changedMsg.getMid())) {
                            msgs.remove(i);
                            break;
                        }

                } else {
                    // a new message received
                    msgs.add(changedMsg);
                }
//                usersMessages.put(profile.getId(), new MutableLiveData<List<Message>>(msgs));
                // force a ui update
                this.vm.getUsersMessages().setValue(usersMessages);
            }));
        });
    }

}