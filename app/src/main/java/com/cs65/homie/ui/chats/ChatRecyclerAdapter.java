package com.cs65.homie.ui.chats;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

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

        Message message = this.vm.getMessages(this.userId)
            .getValue().get(position);
        if (holder.getChatTextBoxLayout() != null)
        {
            holder.getChatMessageTextView().setText(message.getText());
        }
        if (holder.getChatTimeTextView() != null)
        {
            holder.getChatTimeTextView().setText(this.formatDate(message.getTimestamp()));
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