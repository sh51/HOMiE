package com.cs65.homie.ui.chats;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

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

    private final ChatsViewModel vm;
    private final Map<String, Integer> userPositions
        = new HashMap<String, Integer>();

    public ChatsRecyclerAdapter(ChatsViewModel vm)
    {
        this.vm = vm;
    }

    @NotNull
    public ChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ChatsViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_chat, parent, false
            )
        );
    }

    public void onBindViewHolder(@NotNull ChatsViewHolder holder, int position)
    {

        // TODO Need to set up separate clicks for avatar (to profile)
        // and the rest (to messages)
        Message message = null;
        Profile profile = this.vm.getUser(position);
        List<Message> messages
            = this.vm.getMessages(profile.getId()).getValue();
        if (!messages.isEmpty())
        {
            message = messages.get(messages.size() - 1);
        }

        holder.getAvatarView().setImageURI(Uri.parse(profile.getAvatarImage()));
        holder.getNameView().setText(profile.getFirstName());
        if (message != null)
        {
            holder.getChatPreviewView().setText(message.getText());
            holder.setTimeSinceLastMessage(message.getTimestamp());
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
