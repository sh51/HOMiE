package com.cs65.homie.ui.chats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.cs65.homie.FirebaseHelper;
import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.Utilities;
import com.cs65.homie.models.Message;
import com.cs65.homie.models.Profile;
import com.cs65.homie.ui.profile.view.ProfileViewActivity;
import com.cs65.homie.ui.profile.view.ProfileViewFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// FIXME On rotation to horizontal, the keyboard is hidden if it is shown
// before rotation
// REPIII tried to fix this, but failed.
// Other things have higher priority, but should be fixed if somebody
// can figure it out

/**
 * Fragment for a "chat" (conversation, person-to-person messages)
 */
@SuppressWarnings("Convert2Diamond")
public class ChatFragment extends Fragment implements View.OnClickListener
{

    private FirebaseHelper mHelper;

    public static final String ARG_KEY_USER_ID  = "CHAT_FRAG_ARG_KEY_USER_ID";
    public static final String BUNDLE_KEY_KEYBOARD_UP
        = "CHAT_FRAG_BUNDLE_KEY_KEYBOARD_IS_UP";

    private static final int PROFILE_VIEW_ACTIVITY_RESPONSE_CODE = 22565;

    private ChatRecyclerAdapter adapter = null;
    private boolean inProfile = false;
    private EditText inputView = null;
    //private boolean isKeyboardUp = false;
    private RecyclerView recyclerView = null;
    private String userId = null;
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

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view)
    {
        // This probably shouldn't be a switch anymore
        switch (view.getId())
        {

            case R.id.chatAvatarImageView:
            case R.id.chatNameTextView:
                this.spawnProfileView();
                break;
            default:
                // pass

        }
    }

    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        mHelper = FirebaseHelper.getInstance();

        if (this.getArguments() != null)
        {
            this.userId = this.getArguments().getString(ARG_KEY_USER_ID, "");
        }
        else if (savedInstanceState != null)
        {
            this.userId = savedInstanceState.getString(ARG_KEY_USER_ID, "");
        }

        this.vm = new ViewModelProvider(
            this.requireActivity()
        ).get(ChatsViewModel.class);

    }

    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    public void onDestroy()
    {

        // When destroyed, bring up the navigation view that was hidden upon
        // creation
        ((MainActivity)this.requireActivity()).showNavView();
        super.onDestroy();

    }

    public void onNewMessage(List<Message> messages)
    {
        this.adapter.notifyDataSetChanged();
        this.recyclerView.scrollToPosition(messages.size() - 1);
    }

    // This is being kept around if the keyboard issues is ever solved
    //
    //public void onPause()
    //{
    //   super.onPause();
    //   InputMethodManager inputManager
    //      = ((InputMethodManager)this.requireActivity().getSystemService(
    //          Context.INPUT_METHOD_SERVICE)
    //      );
    //   this.isKeyboardUp = inputManager.isAcceptingText();
    //}
    //public void onResume()
    //{
    //    super.onResume();
    //    if (this.inputView != null)
    //    {
    //        InputMethodManager inputManager
    //            = ((InputMethodManager)this.requireActivity().getSystemService(
    //                Context.INPUT_METHOD_SERVICE)
    //            );
    //        if (this.isKeyboardUp)
    //        {
    //            inputManager.toggleSoftInput(
    //                InputMethodManager.SHOW_FORCED, 0
    //            );
    //        }
    //        else
    //        {
    //            inputManager.hideSoftInputFromWindow(
    //                this.inputView.getWindowToken(), 0
    //             );
    //        }
    //    }
    //}

    public void onViewCreated(@NotNull View view, Bundle savedInstanceState)
    {

        // Prevent navigation from the chat fragment by hiding the
        // navigation view
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
            this.vm.getMessages(this.userId).observe(
                this.getViewLifecycleOwner(),
                this::onNewMessage
            );

        }

        View backButtonView = view.findViewById(R.id.chatButtonBackView);
        if (backButtonView != null)
        {
            backButtonView.setOnClickListener(
                (v) -> this.getParentFragmentManager().popBackStack()
            );
        }
        View sendButtonView = view.findViewById(R.id.chatButtonSendView);
        if (sendButtonView != null)
        {
            sendButtonView.setOnClickListener(
                (v) -> this.sendMessage(this.inputView)
            );
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
                if (
                    user.getAvatarImage() == null
                    || user.getAvatarImage().equals("")
                )
                {
                    if (
                        user.getFirstName() != null
                        && !user.getFirstName().equals("")
                    )
                    {
                        avatarView.setImageBitmap(
                            Utilities.nameToDrawable(user.getFirstName())
                        );
                    }
                }
                else
                {
                    avatarView.setImageURI(Uri.parse(user.getAvatarImage()));
                }
            }
        }

        this.inputView = view.findViewById(R.id.chatTextInputView);

    }


    public void onSaveInstanceState(@NotNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_KEY_USER_ID, this.userId);
        //outState.putBoolean(BUNDLE_KEY_KEYBOARD_UP, this.isKeyboardUp);
    }

    public void spawnProfileView()
    {

        if (this.inProfile)
        {
            return;
        }

        this.inProfile = true;

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String myId = sharedPref.getString("userId", "");

        Intent intent = new Intent(
            this.requireContext(), ProfileViewActivity.class
        );
        intent.putExtra(ProfileViewFragment.BUNDLE_KEY_USER_ID, this.userId);
        intent.putExtra(ProfileViewFragment.BUNDLE_KEY_MY_ID, myId);
        this.startActivityForResult(
            intent, PROFILE_VIEW_ACTIVITY_RESPONSE_CODE
        );

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


        // go to definition for description
        mHelper.sendMessage(this.userId, messageText, (msg) -> {
            // scroll to bottom when message sent
            recyclerView.scrollToPosition(this.vm.getMessages(this.userId).getValue().size() -  1);
        });

        // Put the keyboard back
        InputMethodManager inputManager
            = (InputMethodManager)this.requireContext().getSystemService(
                Context.INPUT_METHOD_SERVICE
            );
        inputManager.hideSoftInputFromWindow(
            this.requireActivity().getCurrentFocus().getWindowToken(), 0
        );

        // Clear the text in the input
        inputView.setText(null);

        // In practice the message will be sent asynchronously to Firebase
        // For now, toast
        Utilities.showErrorToast(
            R.string.chat_message_sent_toast, this.getActivity()
        );
    }

}