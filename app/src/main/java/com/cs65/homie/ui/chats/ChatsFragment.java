package com.cs65.homie.ui.chats;

import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.models.Message;
import com.cs65.homie.models.Profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@SuppressWarnings("Convert2Diamond")
public class ChatsFragment extends Fragment implements View.OnClickListener
{

    private ChatsRecyclerAdapter adapter = null;
    private RecyclerView recyclerView = null;
    private ChatsViewModel vm = null;

    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        this.vm = new ViewModelProvider(this.getActivity()).get(ChatsViewModel.class);

        if (this.vm == null)
        {
            // TODO Handle
            // If it can even happen
            Log.d(
                MainActivity.TAG,
                this.getClass().getCanonicalName()
                    + ".onCreate(), ViewModel is null"
            );
            return;
        }

        this.loadFakeData();

    }

    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(
            R.layout.fragment_chats, container, false
        );
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        this.recyclerView = view.findViewById(R.id.chatsRecyclerView);
        if (this.recyclerView != null)
        {

            LinearLayoutManager layoutManager
                = new LinearLayoutManager(this.getContext());
            this.recyclerView.addItemDecoration(new DividerItemDecoration(
                this.getContext(), layoutManager.getOrientation()
            ));
            this.recyclerView.setLayoutManager(layoutManager);
            this.adapter = new ChatsRecyclerAdapter(this, this.vm);
            this.recyclerView.setAdapter(this.adapter);
            this.vm.getUsersMessages().observe(
                this.getViewLifecycleOwner(), this::invalidateRecycler
            );

        }

    }

    public void invalidateRecycler(
        Map<String, MutableLiveData<List<Message>>> usersMessages
    )
    {

        if (this.adapter != null)
        {
            this.adapter.notifyDataSetChanged();
            for (MutableLiveData<List<Message>> messages : usersMessages.values())
            {
                // FIXME Horrible, rotten, terrible use of lambda
                messages.observe(this.getViewLifecycleOwner(), (messagesList) ->
                {
                    if (!messagesList.isEmpty())
                    {
                        String receiverId = messagesList.get(0).getReceiverId();
                        int position = this.adapter.getUserPosition(receiverId);
                        if (position >= 0)
                        {
                            this.adapter.notifyItemChanged(position);
                        }
                    }
                });
            }
        }

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
        message1.setTimestamp(Calendar.getInstance().getTime());
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
        message2.setTimestamp(Calendar.getInstance().getTime());
        message2.setReceiverId("43");
        message2.setSenderId("42");
        Message message3 = new Message();
        message3.setText("Abba");
        message3.setTimestamp(Calendar.getInstance().getTime());
        message3.setReceiverId("43");
        message3.setSenderId("42");

        List<Message> messages = new ArrayList<Message>();
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        TreeMap<String, MutableLiveData<List<Message>>> usersMessages = this.vm.getUsersMessages().getValue();
        TreeMap<String, Profile> users = this.vm.getUsers().getValue();

        usersMessages.put(profile1.getId(), new MutableLiveData<List<Message>>(messages));
        users.put(profile1.getId(), profile1);
        usersMessages.put(profile2.getId(), new MutableLiveData<List<Message>>());
        users.put(profile2.getId(), profile2);

        this.vm.getUsersMessages().setValue(usersMessages);
        this.vm.getUsers().setValue(users);

    }

    public void onClick(View view)
    {
        ((MainActivity)this.getActivity()).spawnChatFragment("43");
    }

}