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
import com.cs65.homie.Utilities;
import com.cs65.homie.models.Message;
import com.cs65.homie.models.Profile;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("Convert2Diamond")
class ChatsRecyclerAdapter extends RecyclerView.Adapter<ChatsViewHolder>
{

    private final ChatsFragment frag;
    private final ChatsViewModel vm;
    private final Map<String, Integer> userPositions
        = new HashMap<String, Integer>();

    ChatsRecyclerAdapter(ChatsFragment frag, ChatsViewModel vm)
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

            // Clicking on the avatar should spawn that user's profile
            avatarView.setOnClickListener(
                (v) -> this.frag.spawnProfileView(profile.getId())
            );

            if (
                profile.getAvatarImage() == null
                || profile.getAvatarImage().equals("")
            )
            {
                if (
                    profile.getFirstName() != null
                    && !profile.getFirstName().equals("")
                )
                avatarView.setImageBitmap(
                    Utilities.nameToDrawable(profile.getFirstName())
                );
            }
            else
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
            holder.setTimeSinceLastMessage(message._getDatetime());
        }
        if (textLayoutView != null)
        {
            // If the user clicks on a chat, that chat is spawned in a new
            // fragment
            // Since spawning requires manipulating the navigator,
            // MainActivty must handle this task
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
        //noinspection ConstantConditions
        return this.vm.getUsers().getValue().size();
    }

    /**
     * Given a User Id, get the recycler view position their chats are
     * previewed in
     *
     * @param id    User Id
     * @return      The recycler view position the user's chats are previewed
     *              in. If the user is not in the recycler, returns -1
     */
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