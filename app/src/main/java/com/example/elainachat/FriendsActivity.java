package com.example.elainachat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.elainachat.netty.NettyClient;
import com.example.elainachat.netty.entity.Content;
import com.example.elainachat.netty.entity.ContentType;
import com.example.elainachat.netty.entity.Friends;
import com.example.elainachat.netty.entity.FriendsInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.List;

public class FriendsActivity extends AppCompatActivity implements FriendsAdapter.OnFriendItemClickListener {

    private RecyclerView recyclerViewFriends;
    private SwipeRefreshLayout swipeRefresh;
    private FriendsAdapter adapter;
    private List<FriendsInfo> friendsList;
    private View emptyView;

    private NettyClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // 初始化视图
        recyclerViewFriends = findViewById(R.id.recyclerViewFriends);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        emptyView = findViewById(R.id.emptyView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化RecyclerView
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFriends.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // 初始化数据
        friendsList = ElainaChatApplication.getInstance().getFriendsList();
        adapter = new FriendsAdapter(friendsList, this);
        ElainaChatApplication.getInstance().setFriendAdapter(adapter);
        recyclerViewFriends.setAdapter(adapter);

        // 配置下拉刷新
        swipeRefresh.setOnRefreshListener(this::loadFriends);

        // 添加好友按钮
        FloatingActionButton fabAddFriend = findViewById(R.id.fabAddFriend);
        fabAddFriend.setOnClickListener(v -> openAddFriendDialog());

        Button btnAddFirstFriend = findViewById(R.id.btnAddFirstFriend);
        btnAddFirstFriend.setOnClickListener(v -> openAddFriendDialog());

        // 加载好友列表
        loadFriends();
    }

    private void loadFriends() {
        // 显示加载指示器
        swipeRefresh.setRefreshing(true);

        // 清空现有列表
        friendsList.clear();
        Content content = new Content(ContentType.FRIENDQUERY, new Friends(ElainaChatApplication.getInstance().getCurrentUser().getId(),null));
        client = ElainaChatApplication.getInstance().getClient();
        client.sendMessage(content);

        // 模拟网络请求，实际应用中这里应该调用API
        new Handler().postDelayed(() -> {

//            FriendsInfo friend = new FriendsInfo();
//            friend.setUserId(1L);
//            friend.setLastMessage("123456");
//            friend.setLastMessage("Hello");
//            friend.setLastMessageTime(LocalDateTime.now());
//            adapter.addFriend(friend);


            System.out.println("friendsList: " + friendsList);

            // 隐藏加载指示器
            swipeRefresh.setRefreshing(false);

            // 显示/隐藏空视图
            updateEmptyView();
        }, 1000);
    }

    private void updateEmptyView() {
        if (friendsList.isEmpty()) {
            recyclerViewFriends.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerViewFriends.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void openAddFriendDialog() {
        // 弹出添加好友对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加好友");

        final EditText input = new EditText(this);
        input.setHint("请输入好友ID或用户名");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("添加", (dialog, which) -> {
            String friendId = input.getText().toString();
            if (!TextUtils.isEmpty(friendId)) {
                Toast.makeText(FriendsActivity.this, "已发送好友请求", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onFriendItemClick(FriendsInfo friend) {
        // 跳转到聊天界面
        Intent intent = new Intent(this, ChatActivity.class);

        startActivity(intent);
    }
}