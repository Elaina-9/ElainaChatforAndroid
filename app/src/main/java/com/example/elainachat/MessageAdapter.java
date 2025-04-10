package com.example.elainachat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elainachat.netty.entity.Messages;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //用于区分不同类型的消息
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private List<Messages> messages;
    private Long currentUserId;
    private boolean isLoading = false;

    public MessageAdapter(List<Messages> messages) {
        this.messages = messages;
        currentUserId = ElainaChatApplication.getInstance().getCurrentUser().getId();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Messages message = messages.get(position);
        if (message.getSenderId() == currentUserId) {
            return VIEW_TYPE_SENT;     // 返回 1，表示发送的消息
        } else {
            return VIEW_TYPE_RECEIVED; // 返回 2，表示接收的消息
        }
    }

    //滑入屏幕时调用
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages message = messages.get(position);
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).messageText.setText(message.getMessageContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    // 在开头添加消息列表
    public void addMessageAtStart(Messages message) {
        messages.add(0, message);
        notifyItemInserted(0);
    }

    public void addMessage(Messages message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
        isLoading = false;
    }
    public void setMessages(List<Messages> newMessages) {
        this.messages.clear();
        if (newMessages != null) {
            this.messages.addAll(newMessages);
        }
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }
}