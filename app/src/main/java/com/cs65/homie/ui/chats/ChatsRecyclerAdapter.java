package com.cs65.homie.ui.chats;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.models.Message;
import com.cs65.homie.models.Profile;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("Convert2Diamond")
public class ChatsRecyclerAdapter extends RecyclerView.Adapter<ChatsViewHolder>
{

    private final ChatsFragment frag;
    private final ChatsViewModel vm;
    private final Map<String, Integer> userPositions
        = new HashMap<String, Integer>();

    public ChatsRecyclerAdapter(ChatsFragment frag, ChatsViewModel vm)
    {
        this.frag = frag;
        this.vm = vm;
    }

    @NotNull
    public ChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ChatsViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_chats, parent, false
            )
        );
    }

    public void onBindViewHolder(@NotNull ChatsViewHolder holder, int position)
    {

        ImageView avatarView = holder.getAvatarView();
        TextView chatPreviewView = holder.getChatPreviewView();
        TextView nameView = holder.getNameView();
        View textLayoutView = holder.getTextLayoutView();

        Message message = null;
        Profile profile = this.vm.getUser(position);
        List<Message> messages
            = this.vm.getMessages(profile.getId()).getValue();
        if (messages != null && !messages.isEmpty())
        {
            message = messages.get(messages.size() - 1);
        }

        if (avatarView != null)
        {
            // TODO Need to set up separate clicks for avatar (to profile)
            //avatarView.setOnClickListener(this.frag);
            if (profile.getAvatarImage() != null)
            {
                avatarView.setImageURI(Uri.parse(profile.getAvatarImage()));
            }
        }
        if (nameView != null)
        {
            nameView.setText(profile.getFirstName());
        }
        if (message != null)
        {
            if (chatPreviewView != null)
            {
                chatPreviewView.setText(message.getText());
            }
            holder.setTimeSinceLastMessage(message.getTimestamp());
        }
        if (textLayoutView != null)
        {
            textLayoutView.setOnClickListener(
                (v) -> ((MainActivity)this.frag.requireActivity()).spawnChatFragment(
                    profile.getId()
                )
            );
        }

        this.userPositions.put(profile.getId(), position);

    }

    public int getItemCount()
    {
        return this.vm.getUsers().getValue().size();
    }

    public int getUserPosition(String id)
    {

        Integer position = this.userPositions.get(id);
        if (position == null)
        {
            return -1;
        }
        return position;

    }

}