package com.example.elainachat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //用于区分不同类型的消息
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private List<Message> messages;

    private OnLoadMoreListener loadMoreListener;

    // 添加加载更多的监听接口
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
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
        Message message = messages.get(position);
        if (message.getSender().equals("User")) {
            return VIEW_TYPE_SENT;     // 返回 1，表示发送的消息
        } else {
            return VIEW_TYPE_RECEIVED; // 返回 2，表示接收的消息
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 当滑动到顶部时触发加载更多
        if (position == 0 && loadMoreListener != null) {
            loadMoreListener.onLoadMore();
        }
        Message message = messages.get(position);
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).messageText.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    // 在开头添加消息列表
    public void addMessagesAtStart(List<Message> newMessages) {
        messages.addAll(0, newMessages);
        notifyItemRangeInserted(0, newMessages.size());
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }


    // 设置加载更多监听器
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.loadMoreListener = listener;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }
}