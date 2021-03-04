package com.cs65.homie.ui.chats;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ChatsViewHolder extends RecyclerView.ViewHolder
{

    private final ImageView avatarView;
    private final TextView chatPreviewView;
    private final TextView nameView;
    private final TextView timeSinceLastMessageView;

    public ChatsViewHolder(View itemView)
    {

        super(itemView);

        this.avatarView = itemView.findViewById(R.id.chatsAvatarView);
        this.chatPreviewView = itemView.findViewById(R.id.chatsPreviewTextView);
        this.nameView = itemView.findViewById(R.id.chatsNameTextView);
        this.timeSinceLastMessageView = itemView.findViewById(
            R.id.chatsTimeSinceLastMessageTextView
        );

    }

    public ImageView getAvatarView()
    {
        return this.avatarView;
    }

    public TextView getChatPreviewView()
    {
        return this.chatPreviewView;
    }

    public TextView getNameView()
    {
        return this.nameView;
    }

    public TextView getTimeSinceLastMessageView()
    {
        return this.timeSinceLastMessageView;
    }

    public void setTimeSinceLastMessage(Date then)
    {

        Date now = Calendar.getInstance().getTime();
        LocalDate localDate = LocalDate.ofEpochDay(
            then.getTime() / (1000 * 60 * 60 * 24)
        );
        long duration = now.getTime() - then.getTime();

        // FIXME Magic everything
        String output;
        if (duration < 1000 * 60 * 60)
        {
            output = String.format(
                Locale.getDefault(),
                "%dm",
                (int)(duration / (100 * 60))
            );
        }
        else if (duration < 1000 * 60 * 60 * 24)
        {
            output = String.format(
                Locale.getDefault(),
                "%dh",
                (int)(duration / (100 * 60 * 60))
            );
        }
        else if (duration < 1000 * 60 * 60 * 24 * 6)
        {
            // FIXME Date formatters need to be static
            output = DateTimeFormatter.ofPattern("E").format(localDate);
        }
        else if (duration < 1000L * 60L * 60L * 24L * 364L)
        {
            output = DateTimeFormatter.ofPattern("d L").format(localDate);
        }
        else
        {
            output = DateTimeFormatter.ofPattern("u").format(localDate);
        }

        this.getTimeSinceLastMessageView().setText(output);

    }

}