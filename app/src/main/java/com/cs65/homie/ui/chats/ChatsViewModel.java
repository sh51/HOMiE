package com.cs65.homie.ui.chats;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cs65.homie.models.Message;
import com.cs65.homie.models.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@SuppressWarnings("Convert2Diamond")
public class ChatsViewModel extends ViewModel
{

    private final MutableLiveData<TreeMap<String, Profile>> users;
    private final MutableLiveData<
        TreeMap<String, MutableLiveData<List<Message>>>
    >  usersMessages;
    private List<String> usersList = new ArrayList<String>();

    public ChatsViewModel()
    {

        this.users = new MutableLiveData<TreeMap<String, Profile>>(
            new TreeMap<String, Profile>()
        );
        this.usersMessages = new MutableLiveData<
            TreeMap<String, MutableLiveData<List<Message>>>
        >(
            new TreeMap<String, MutableLiveData<List<Message>>>()
        );

        // Since we don't have a destroy event, we can't release
        // this observation
        // FIXME Due to the circular connection I doubt GC cleans it up either...
        this.users.observeForever(this::updateUsersList);

    }

    public MutableLiveData<List<Message>> getMessages(String id)
    {
        return this.usersMessages.getValue().get(id);
    }

    public Profile getUser(int i)
    {

        if (i < this.usersList.size())
        {
            return this.users.getValue().get(this.usersList.get(i));
        }
        return null;

    }

    public Profile getUser(String id)
    {
        return this.users.getValue().get(id);
    }

    public MutableLiveData<TreeMap<String, Profile>> getUsers()
    {
        return this.users;
    }

    public MutableLiveData<
        TreeMap<String, MutableLiveData<List<Message>>>
    > getUsersMessages()
    {
        return this.usersMessages;
    }

    private void updateUsersList(TreeMap<String, Profile> map)
    {
        this.usersList.clear();
        this.usersList.addAll(map.keySet());
    }

}