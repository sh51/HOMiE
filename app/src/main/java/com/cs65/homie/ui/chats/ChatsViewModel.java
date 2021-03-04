package com.cs65.homie.ui.chats;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cs65.homie.models.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("Convert2Diamond")
public class ChatsViewModel extends ViewModel
{

    private final MutableLiveData<
        Map<String, MutableLiveData<List<Message>>>
    > userMessages;

    public ChatsViewModel()
    {
        this.userMessages = new MutableLiveData<
            Map<String, MutableLiveData<List<Message>>>
        >(
            new HashMap<String, MutableLiveData<List<Message>>>()
        );
    }

    public MutableLiveData<Map<String, MutableLiveData<List<Message>>>> getUsersMessages()
    {
        return this.userMessages;
    }

    public MutableLiveData<List<Message>> getMessages(String user)
    {
        return this.userMessages.getValue().get(user);
    }

}