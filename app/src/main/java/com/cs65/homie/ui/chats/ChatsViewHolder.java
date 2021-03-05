package com.cs65.homie.ui.chats;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.R;
import com.cs65.homie.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


class ChatsViewHolder extends RecyclerView.ViewHolder
{

    // These aren't static because of the locale call
    private final SimpleDateFormat DAY_FORMATTER =
        new SimpleDateFormat("E", Locale.getDefault());
    private final SimpleDateFormat MONTH_DAY_FORMATTER =
        new SimpleDateFormat("MMM dd", Locale.getDefault());
    private final SimpleDateFormat YEAR_FORMATTER =
        new SimpleDateFormat("yyyy", Locale.getDefault());

    private final ImageView avatarView;
    private final TextView chatPreviewView;
    private final TextView nameView;
    private final View textLayoutView;
    private final TextView timeSinceLastMessageView;

    ChatsViewHolder(View itemView)
    {

        super(itemView);

        this.avatarView = itemView.findViewById(R.id.chatsAvatarView);
        this.chatPreviewView = itemView.findViewById(R.id.chatsPreviewTextView);
        this.nameView = itemView.findViewById(R.id.chatsNameTextView);
        this.textLayoutView = itemView.findViewById(R.id.chatsTextLayout);
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

    public View getTextLayoutView()
    {
        return this.textLayoutView;
    }

    public TextView getTimeSinceLastMessageView()
    {
        return this.timeSinceLastMessageView;
    }

    /**
     * Set the time since the last message in this chat, formatted
     *
     * @param then  The time of the last message in the chat
     */
    public void setTimeSinceLastMessage(Date then)
    {

        if (this.getTimeSinceLastMessageView() == null)
        {
            return;
        }

        Date now = Calendar.getInstance().getTime();
        long duration = now.getTime() - then.getTime();

        String output;
        if (duration < Utilities.MILISECONDS_IN_HOUR)
        {
            output = String.format(
                Locale.getDefault(),
                "%dm",
                // Get duration in terms of minutes, rounded down
                (int)(duration / Utilities.MILISECONDS_IN_MINUTE)
            );
        }
        else if (duration < Utilities.MILISECONDS_IN_DAY)
        {
            output = String.format(
                Locale.getDefault(),
                "%dh",
                // Get duration in terms of hours, rounded down
                (int)(duration / Utilities.MILISECONDS_IN_MINUTE * 60)
            );
        }
        else if (duration < Utilities.MILISECONDS_IN_DAY * 6)
        {
            output = DAY_FORMATTER.format(then);
        }
        else if (duration < Utilities.MILISECONDS_IN_DAY * 365L)
        {
            output = MONTH_DAY_FORMATTER.format(then);
        }
        else
        {
            output = YEAR_FORMATTER.format(then);
        }

        this.getTimeSinceLastMessageView().setText(output);

    }

}