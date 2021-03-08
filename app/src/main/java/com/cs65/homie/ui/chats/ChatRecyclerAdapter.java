package com.cs65.homie.ui.chats;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.MainActivity;
import com.cs65.homie.R;
import com.cs65.homie.models.Message;

import org.jetbrains.annotations.NotNull;

import java.util.List;


class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder>
{

    private final ChatsViewModel vm;
    private final String userId;

    public ChatRecyclerAdapter(ChatsViewModel vm, String userId)
    {
        this.vm = vm;
        this.userId  = userId;
    }

    @NotNull
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ChatViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_chat, parent, false
            )
        );
    }

    public void onBindViewHolder(@NotNull ChatViewHolder holder, int position)
    {

        Log.d(MainActivity.TAG, String.format(
            "%s.onBindViewHolder(), position: %d",
            this.getClass().getCanonicalName(), position
        ));
        List<Message> messages = this.vm.getMessages(this.userId).getValue();
        if (messages == null || messages.isEmpty())
        {
            return;
        }
        Message message = messages.get(position);

        // We have to do this because the backgrounds are different
        // And we can't load in a different background through code and
        // maintain padding/margins
        // So we just have two layouts for each message, right aligned and
        // left aligned
        // Both are GONE to begin with, and then we show one though code
        // Views are recycled, so that must be taken into account.
        // (They might not be gone at this invocation
        //
        // App owner's messages are on the right
        if (message.getSenderId().equals(this.userId))
        {
            // Right chat needs to be hidden
            if (holder.getChatTextBoxLayoutRight() != null)
            {
                holder.getChatTextBoxLayoutRight().setVisibility(View.GONE);
            }
            if (holder.getChatTextBoxLayoutLeft() != null)
            {
                holder.getChatMessageTextViewLeft().setText(message.getText());
            }
            if (holder.getChatTimeTextViewLeft() != null)
            {
                holder.formatDate(
                    message._getDatetime(), holder.getChatTimeTextViewLeft()
                );
            }
            // A different spacer is used to right and left align the chat boxes
            // This spacer is controlled by its layout weight
            // We need to ensure each weight is correct since views are recycled
            if (holder.getSpacerLeft() != null)
            {
                LinearLayout.LayoutParams params
                    = (LinearLayout.LayoutParams)holder.getSpacerLeft()
                    .getLayoutParams();
                params.weight = 0;
                holder.getSpacerLeft().setLayoutParams(params);
            }
            if (holder.getSpacerRight() != null)
            {
                LinearLayout.LayoutParams params
                    = (LinearLayout.LayoutParams)holder.getSpacerRight()
                        .getLayoutParams();
                params.weight = 1;
                holder.getSpacerRight().setLayoutParams(params);
            }
            // Finally, show the view
            if (holder.getChatTextBoxLayoutLeft() != null)
            {
                holder.getChatTextBoxLayoutLeft().setVisibility(View.VISIBLE);
            }
        }
        else
        {
            // See the notes above
            // Everything is just repeated for the right chat box here
            if (holder.getChatTextBoxLayoutLeft() != null)
            {
                holder.getChatTextBoxLayoutLeft().setVisibility(View.GONE);
            }
            if (holder.getChatTextBoxLayoutRight() != null)
            {
                holder.getChatMessageTextViewRight().setText(message.getText());
            }
            if (holder.getChatTimeTextViewRight() != null)
            {
                holder.formatDate(
                    message._getDatetime(), holder.getChatTimeTextViewRight()
                );
            }
            if (holder.getSpacerLeft() != null)
            {
                LinearLayout.LayoutParams params
                    = (LinearLayout.LayoutParams)holder.getSpacerLeft()
                        .getLayoutParams();
                params.weight = 1;
                holder.getSpacerLeft().setLayoutParams(params);
            }
            if (holder.getSpacerRight() != null)
            {
                LinearLayout.LayoutParams params
                    = (LinearLayout.LayoutParams)holder.getSpacerRight()
                    .getLayoutParams();
                params.weight = 0;
                holder.getSpacerRight().setLayoutParams(params);
            }
            if (holder.getChatTextBoxLayoutRight() != null)
            {
                holder.getChatTextBoxLayoutRight().setVisibility(View.VISIBLE);
            }
        }

    }

    public int getItemCount()
    {
        List<Message> messages = this.vm.getMessages(this.userId).getValue();
        if (messages != null)
        {
            return messages.size();
        }
        return 0;
    }

}