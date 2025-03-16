package com.example.elainachat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.elainachat.netty.entity.FriendsInfo;

import java.util.List;

import lombok.NonNull;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private List<FriendsInfo> friendsList;
    private OnFriendItemClickListener listener;

    public interface OnFriendItemClickListener {
        void onFriendItemClick(FriendsInfo friend);
    }

    public FriendsAdapter(List<FriendsInfo> friendsList, OnFriendItemClickListener listener) {
        this.friendsList = friendsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        FriendsInfo friend = friendsList.get(position);
        holder.bind(friend, listener);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public void updateFriendsList(List<FriendsInfo> newList) {
        this.friendsList = newList;
        notifyDataSetChanged();
    }

    public void addFriend(FriendsInfo friend) {
        friendsList.add(friend);
        notifyItemInserted(friendsList.size() - 1);
        System.out.println("adapter" + friendsList);
    }
    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvName;
        TextView tvLastMessage;
        TextView tvTime;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(final FriendsInfo friend, final OnFriendItemClickListener listener) {
            tvName.setText(friend.getUserName());
            tvLastMessage.setText(friend.getLastMessage());
            tvTime.setText(friend.getLastMessageTime().toString());

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
                    listener.onFriendItemClick(friend);
                }
            });
        }
    }
}