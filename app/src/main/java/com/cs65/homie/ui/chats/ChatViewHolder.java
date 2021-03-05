package com.cs65.homie.ui.chats;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.R;
import com.cs65.homie.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


class ChatViewHolder extends RecyclerView.ViewHolder
{

    // These aren't static because of the locale call
    private final SimpleDateFormat DAY_HOUR_MINUTE_FORMATTER
        = new SimpleDateFormat("E, HH:mm", Locale.getDefault());
    private final SimpleDateFormat MONTH_HOUR_MINUTE_FORMATTER
        = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
    private final SimpleDateFormat YEAR_HOUR_MINUTE_FORMATTER
        = new SimpleDateFormat("MMM dd yyyy, HH:mm", Locale.getDefault());

    private final TextView chatMessageTextViewRight;
    private final TextView chatTimeTextViewRight;
    private final View chatTextBoxLayoutRight;
    private final TextView chatMessageTextViewLeft;
    private final TextView chatTimeTextViewLeft;
    private final View chatTextBoxLayoutLeft;
    private final View spacerLeft;
    private final View spacerRight;

    ChatViewHolder(View itemView)
    {

        super(itemView);

        this.chatMessageTextViewLeft = itemView.findViewById(
            R.id.chatItemMessageTextViewLeft
        );
        this.chatTimeTextViewLeft = itemView.findViewById(
            R.id.chatItemMessageTimeViewLeft
        );
        this.chatTextBoxLayoutLeft = itemView.findViewById(
            R.id.chatItemTextBoxLayoutLeft
        );
        this.chatMessageTextViewRight= itemView.findViewById(
            R.id.chatItemMessageTextViewRight
        );
        this.chatTimeTextViewRight= itemView.findViewById(
            R.id.chatItemMessageTimeViewRight
        );
        this.chatTextBoxLayoutRight = itemView.findViewById(
            R.id.chatItemTextBoxLayoutRight
        );
        this.spacerLeft = itemView.findViewById(R.id.chatItemSpacerLeft);
        this.spacerRight = itemView.findViewById(R.id.chatItemSpacerRight);

    }


    public TextView getChatMessageTextViewLeft()
    {
        return this.chatMessageTextViewLeft;
    }

    public TextView getChatTimeTextViewLeft()
    {
        return this.chatTimeTextViewLeft;
    }

    public View getChatTextBoxLayoutLeft()
    {
        return this.chatTextBoxLayoutLeft;
    }

    public TextView getChatMessageTextViewRight()
    {
        return this.chatMessageTextViewRight;
    }

    public TextView getChatTimeTextViewRight()
    {
        return this.chatTimeTextViewRight;
    }

    public View getChatTextBoxLayoutRight()
    {
        return this.chatTextBoxLayoutRight;
    }

    public View getSpacerLeft()
    {
        return this.spacerLeft;
    }

    public View getSpacerRight()
    {
        return this.spacerRight;
    }

    /**
     * Format a message date and insert it into the given text view
     *
     * @param then      Date of the message
     * @param timeView  Text view which will contain the time string
     */
    public void formatDate(Date then, TextView timeView)
    {

        String formatDate;
        if (then == null)
        {
            return;
        }

        Date now = Calendar.getInstance().getTime();
        long diff = now.getTime() - then.getTime();

        // If the message is less than a week old, just show the day of the
        // message
        if (diff < (Utilities.MILISECONDS_IN_DAY * 6))
        {
            formatDate = DAY_HOUR_MINUTE_FORMATTER.format(then);
        }
        // If the message is less than a year old, show the date, but without
        // specifying the year
        // Leap years...whatever
        else if (diff < (Utilities.MILISECONDS_IN_DAY * 365L))
        {
            formatDate = MONTH_HOUR_MINUTE_FORMATTER.format(then);
        }
        else
        {
            formatDate = YEAR_HOUR_MINUTE_FORMATTER.format(then);
        }

        timeView.setText(formatDate);

    }

}