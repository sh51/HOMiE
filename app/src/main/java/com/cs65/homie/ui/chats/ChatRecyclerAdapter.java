package com.cs65.homie.ui.chats;

import android.graphics.Color;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder>
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

    public void onBindViewHolder(ChatViewHolder holder, int position)
    {

        Log.d(MainActivity.TAG, String.format(
            "%s.onBindViewHolder(), position: %d",
            this.getClass().getCanonicalName(), position
        ));
        // TODO Handle get failure
        Message message = this.vm.getMessages(this.userId).getValue().get(position);

        // We have to do this because the backgrounds are different
        // And we can't load in a different background through code and
        // maintain padding/margins
        if (message.getSenderId().equals(this.userId))
        {
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
                holder.getChatTimeTextViewLeft().setText(this.formatDate(message.getTimestamp()));
            }
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
            if (holder.getChatTextBoxLayoutLeft() != null)
            {
                holder.getChatTextBoxLayoutLeft().setVisibility(View.VISIBLE);
            }
        }
        else
        {
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
                holder.getChatTimeTextViewRight().setText(
                    this.formatDate(message.getTimestamp())
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
        return this.vm.getMessages(this.userId).getValue().size();
    }

    // FIXME Move to the ViewHolder
    private String formatDate(Date then)
    {

        String formatDate = "";
        if (then == null)
        {
            return formatDate;
        }

        Date now = Calendar.getInstance().getTime();
        long diff = now.getTime() - then.getTime();

        if (diff < (1000 * 60 * 60 * 24 * 6))
        {
            SimpleDateFormat formatter = new SimpleDateFormat("E, HH:mm", Locale.getDefault());
            formatDate = formatter.format(then);
        }
        else if (diff < (1000L * 60L * 60L * 24L * 355L))
        {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            formatDate = formatter.format(then);
        }
        else
        {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy, HH:mm", Locale.getDefault());
            formatDate = formatter.format(then);
        }

        return formatDate;

    }

}