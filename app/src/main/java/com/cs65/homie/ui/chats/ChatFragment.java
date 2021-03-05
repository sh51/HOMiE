package com.cs65.homie.ui.chats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.Utilities;
import com.cs65.homie.models.Message;
import com.cs65.homie.models.Profile;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;


public class ChatFragment extends Fragment implements View.OnClickListener
{

    public static final String ARG_KEY_USER_ID  = "CHAT_FRAG_ARG_KEY_USER_ID";

    private ChatRecyclerAdapter adapter = null;
    private EditText inputView = null;
    private RecyclerView recyclerView = null;
    private String userId = null;
    private ChatsViewModel vm = null;


    @SuppressLint("NonConstantResourceId")
    public void onClick(View view)
    {
        switch (view.getId())
        {

            case R.id.chatButtonBackView:
                // Finish the fragment
                this.getParentFragmentManager().popBackStack();
                break;
            case R.id.chatAvatarImageView:
            case R.id.chatNameTextView:
                break;
            case R.id.chatButtonSendView:
                this.sendMessage(this.inputView);
                break;
            default:
                // pass

        }
    }

    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        ((MainActivity)this.requireActivity()).hideNavView();
        if (this.getArguments() != null)
        {
            this.userId = this.getArguments().getString(ARG_KEY_USER_ID, "");
        }
        else if (savedInstanceState != null)
        {
            this.userId = savedInstanceState.getString(ARG_KEY_USER_ID, "");
        }

        this.vm = new ViewModelProvider(this.getActivity()).get(ChatsViewModel.class);

    }

    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    public void onDestroy()
    {
        ((MainActivity)this.requireActivity()).showNavView();
        super.onDestroy();
    }

    public void onViewCreated(@NotNull View view, Bundle savedInstanceState)
    {

        this.recyclerView = view.findViewById(R.id.chatRecyclerView);
        if (this.recyclerView != null)
        {

            LinearLayoutManager layoutManager
                = new LinearLayoutManager(this.getContext());
            //this.recyclerView.addItemDecoration(new DividerItemDecoration(
            //    this.getContext(), layoutManager.getOrientation()
            //));
            this.recyclerView.setLayoutManager(layoutManager);
            this.adapter = new ChatRecyclerAdapter(this.vm, this.userId);
            this.recyclerView.setAdapter(this.adapter);
            // FIXME Bad lambda use
            this.vm.getMessages(this.userId).observe(
                this.getViewLifecycleOwner(), (x) -> { this.adapter.notifyDataSetChanged(); }
            );

        }

        View backButtonView = view.findViewById(R.id.chatButtonBackView);
        if (backButtonView != null)
        {
            backButtonView.setOnClickListener(this);
        }
        View sendButtonView = view.findViewById(R.id.chatButtonSendView);
        if (sendButtonView != null)
        {
            sendButtonView.setOnClickListener(this);
        }
        this.inputView = view.findViewById(R.id.chatTextInputView);

        Profile user = this.vm.getUser(this.userId);
        if (user != null)
        {

            TextView chatNameView = view.findViewById(R.id.chatNameTextView);
            if (chatNameView != null)
            {
                chatNameView.setOnClickListener(this);
                chatNameView.setText(user.getFirstName());

            }

            ImageView avatarView = view.findViewById(R.id.chatAvatarImageView);
            if (avatarView != null)
            {
                avatarView.setOnClickListener(this);
                if (user.getAvatarImage() != null)
                {
                    avatarView.setImageURI(Uri.parse(user.getAvatarImage()));
                }
            }
        }

    }

    public void onSaveInstanceState(@NotNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_KEY_USER_ID, this.userId);
    }

    private void sendMessage(EditText inputView)
    {

        if (inputView == null)
        {
            return;
        }

        String messageText = inputView.getEditableText().toString();
        if (messageText.equals(""))
        {
            return;
        }

        Message message = new Message();
        message.setTimestamp(Calendar.getInstance().getTime());
        message.setText(messageText);
        message.setReceiverId(this.userId);
        message.setSenderId(
            ((MainActivity)this.requireActivity()).getFakeMyId()
        );

        List<Message> messages = this.vm.getMessages(this.userId).getValue();
        //noinspection ConstantConditions
        messages.add(message);
        this.vm.getMessages(this.userId).setValue(messages);

        // TODO Don't ignore all these nulls
        InputMethodManager inputManager
            = (InputMethodManager)this.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE
            );
        inputManager.hideSoftInputFromWindow(
            this.getActivity().getCurrentFocus().getWindowToken(), 0
        );

        inputView.setText("");
        Utilities.showErrorToast(
            R.string.chat_message_sent_toast, this.getActivity()
        );

    }

}