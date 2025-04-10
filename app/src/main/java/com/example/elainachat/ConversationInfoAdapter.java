package com.example.elainachat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.elainachat.netty.entity.ConversationInfo;

import java.util.List;

import lombok.NonNull;

public class ConversationInfoAdapter extends RecyclerView.Adapter<ConversationInfoAdapter.ConversationInfoViewHolder> {

    private List<ConversationInfo> conversationInfos;
    private OnConversationItemClickListener listener;

    public interface OnConversationItemClickListener {
        void onConversationItemClick(ConversationInfo conversationInfo);
    }

    public ConversationInfoAdapter(List<ConversationInfo> conversationInfo , OnConversationItemClickListener listener) {
        this.conversationInfos = conversationInfo;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversationInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationInfoViewHolder holder, int position) {
        ConversationInfo conversationInfo = conversationInfos.get(position);
        holder.bind(conversationInfo, listener);
    }

    @Override
    public int getItemCount() {
        return conversationInfos.size();
    }

    public void updateConversationList(List<ConversationInfo> newList) {
        // 清空现有列表并添加新数据，保持引用不变
        this.conversationInfos.clear();
        this.conversationInfos.addAll(newList);
        notifyDataSetChanged();
    }

    public void addConversation(ConversationInfo conversationInfo) {
        conversationInfos.add(conversationInfo);
        notifyItemInserted(conversationInfos.size() - 1);
        System.out.println("adapter" + conversationInfos);
    }
    static class ConversationInfoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvName;
        TextView tvLastMessage;
        TextView tvTime;

        public ConversationInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(final ConversationInfo conversationInfo, final OnConversationItemClickListener listener) {
            tvName.setText(conversationInfo.getName());
            tvLastMessage.setText(conversationInfo.getLastMessage());
            if (conversationInfo.getLastMessageTime() == null) {
                tvTime.setText("刚刚");
            } else {
                tvTime.setText(conversationInfo.getLastMessageTime().toString());
            }

            // 如果有头像URL，使用Glide或Picasso加载
//            if (friend.getAvatarUrl() != null && !friend.getAvatarUrl().isEmpty()) {
                // Glide示例
                // Glide.with(itemView.getContext())
                //     .load(friend.getAvatarUrl())
                //     .placeholder(R.drawable.default_avatar)
                //     .error(R.drawable.default_avatar)
                //     .into(imgAvatar);
//            }

            // 设置整个项的点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConversationItemClick(conversationInfo);
                }
            });
        }
    }
}