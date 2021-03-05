package com.cs65.homie.ui.chats;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.R;


public class ChatViewHolder extends RecyclerView.ViewHolder
{

    private final TextView chatMessageTextView;
    private final TextView chatTimeTextView;
    private final View chatTextBoxLayout;
    private final View spacerLeft;
    private final View spacerRight;


    public ChatViewHolder(View itemView)
    {

        super(itemView);

        this.chatMessageTextView = itemView.findViewById(R.id.chatItemMessageTextView);
        this.chatTimeTextView = itemView.findViewById(R.id.chatItemMessageTimeView);
        this.chatTextBoxLayout = itemView.findViewById(R.id.chatItemTextBoxLayout);
        this.spacerLeft = itemView.findViewById(R.id.chatItemSpacerLeft);
        this.spacerRight = itemView.findViewById(R.id.chatItemSpacerRight);

    }

    TextView getChatMessageTextView()
    {
        return this.chatMessageTextView;
    }

    TextView getChatTimeTextView()
    {
        return this.chatTimeTextView;
    }

    View getChatTextBoxLayout()
    {
        return this.chatTextBoxLayout;
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