package com.cs65.homie.ui.chats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChatFragment extends Fragment implements View.OnClickListener
{

    public static final String ARG_KEY_USER_ID  = "CHAT_FRAG_ARG_KEY_USER_ID";
    public static final String BUNDLE_KEY_KEYBOARD_UP
        = "CHAT_FRAG_BUNDLE_KEY_KEYBOARD_IS_UP";

    private ChatRecyclerAdapter adapter = null;
    private EditText inputView = null;
    //private boolean isKeyboardUp = false;
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

        if (this.getArguments() != null)
        {
            this.userId = this.getArguments().getString(ARG_KEY_USER_ID, "");
        }
        else if (savedInstanceState != null)
        {
            this.userId = savedInstanceState.getString(ARG_KEY_USER_ID, "");
        }

        this.vm = new ViewModelProvider(this.requireActivity()).get(ChatsViewModel.class);

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

        // FIXME if the input view has no text, take away it's focus

        ((MainActivity)this.requireActivity()).hideNavView();
        this.recyclerView = view.findViewById(R.id.chatRecyclerView);
        if (this.recyclerView != null)
        {

            LinearLayoutManager layoutManager
                = new LinearLayoutManager(this.getContext());
            layoutManager.setStackFromEnd(true);
            this.recyclerView.setLayoutManager(layoutManager);
            this.adapter = new ChatRecyclerAdapter(this.vm, this.userId);
            this.recyclerView.setAdapter(this.adapter);
            // FIXME Bad lambda use
            // FIXME Bad variable name
            this.vm.getMessages(this.userId).observe(
                this.getViewLifecycleOwner(), (x) -> {
                    this.adapter.notifyDataSetChanged();
                    this.recyclerView.scrollToPosition(x.size() - 1);
                }
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

        this.inputView = view.findViewById(R.id.chatTextInputView);


    }

    // FIXME On rotation to horizontal, the keyboard is hidden if it is shown
    // before rotation
    // REPIII tried to fix this, but failed.
    // Other things have higher priority, but should be fixed if somebody
    // can figure it out
    // TODO Sort
    //public void onResume()
    //{
    //    super.onResume();
    //    if (this.inputView != null)
    //    {
    //        InputMethodManager inputManager = ((InputMethodManager) this.requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
    //        if (this.isKeyboardUp)
    //        {
    //            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    //        }
    //        else
    //        {
    //            inputManager.hideSoftInputFromWindow(this.inputView.getWindowToken(), 0);
    //        }
    //    }
    //}

    public void onSaveInstanceState(@NotNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_KEY_USER_ID, this.userId);
        //outState.putBoolean(BUNDLE_KEY_KEYBOARD_UP, this.isKeyboardUp);
    }

    // TODO Sort
    //public void onPause()
    //{
    //   super.onPause();
    //   InputMethodManager inputManager = ((InputMethodManager) this.requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
    //   this.isKeyboardUp = inputManager.isAcceptingText();
    //}

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
        if (messages == null)
        {
            messages = new ArrayList<Message>();
        }
        //noinspection ConstantConditions
        messages.add(message);
        this.vm.getMessages(this.userId).setValue(messages);

        // FIXME Don't ignore all these potential nulls
        InputMethodManager inputManager
            = (InputMethodManager)this.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE
            );
        inputManager.hideSoftInputFromWindow(
            this.getActivity().getCurrentFocus().getWindowToken(), 0
        );

        inputView.setText(null);
        Utilities.showErrorToast(
            R.string.chat_message_sent_toast, this.getActivity()
        );

    }

}