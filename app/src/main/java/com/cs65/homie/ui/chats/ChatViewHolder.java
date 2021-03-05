package com.cs65.homie.ui.chats;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.R;


public class ChatViewHolder extends RecyclerView.ViewHolder
{

    private final TextView chatMessageTextViewRight;
    private final TextView chatTimeTextViewRight;
    private final View chatTextBoxLayoutRight;
    private final TextView chatMessageTextViewLeft;
    private final TextView chatTimeTextViewLeft;
    private final View chatTextBoxLayoutLeft;
    private final View spacerLeft;
    private final View spacerRight;

    public ChatViewHolder(View itemView)
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


    TextView getChatMessageTextViewLeft()
    {
        return this.chatMessageTextViewLeft;
    }

    TextView getChatTimeTextViewLeft()
    {
        return this.chatTimeTextViewLeft;
    }

    View getChatTextBoxLayoutLeft()
    {
        return this.chatTextBoxLayoutLeft;
    }

    TextView getChatMessageTextViewRight()
    {
        return this.chatMessageTextViewRight;
    }

    TextView getChatTimeTextViewRight()
    {
        return this.chatTimeTextViewRight;
    }

    View getChatTextBoxLayoutRight()
    {
        return this.chatTextBoxLayoutRight;
    }

    View getSpacerLeft()
    {
        return this.spacerLeft;
    }

    View getSpacerRight()
    {
        return this.spacerRight;
    }

}