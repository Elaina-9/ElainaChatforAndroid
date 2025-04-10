package com.example.elainachat;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.elainachat.netty.entity.ConversationInfo;
import com.example.elainachat.netty.entity.Member;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ConversationInfoActivity extends AppCompatActivity implements ConversationInfoAdapter.OnConversationItemClickListener {
    private RecyclerView recyclerViewFriends;
    private SwipeRefreshLayout swipeRefresh;
    private ConversationInfoAdapter adapter;
    private List<ConversationInfo> conversationInfos;
    private View emptyView;

    private NettyClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        // 初始化视图
        recyclerViewFriends = findViewById(R.id.recyclerViewconversations);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        emptyView = findViewById(R.id.emptyView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化RecyclerView
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFriends.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // 初始化数据
        conversationInfos = ElainaChatApplication.getInstance().getConversationInfos();
        adapter = new ConversationInfoAdapter(conversationInfos, this);
        ElainaChatApplication.getInstance().setConversationInfoAdapter(adapter);
        recyclerViewFriends.setAdapter(adapter);

        // 配置下拉刷新
        swipeRefresh.setOnRefreshListener(this::loadConversations);

        // 添加好友按钮
        FloatingActionButton fabAddFriend = findViewById(R.id.fabAddFriend);
        fabAddFriend.setOnClickListener(v -> openAddFriendDialog());

        Button btnAddFirstFriend = findViewById(R.id.btnAddFirstFriend);
        btnAddFirstFriend.setOnClickListener(v -> openAddFriendDialog());

        // 加载对话列表
        loadConversations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 设置当前活动
        ElainaChatApplication.getInstance().setCurrentActivity(this);
    }

    private void loadConversations() {
        // 显示加载指示器
        swipeRefresh.setRefreshing(true);

        // 清空现有列表
        conversationInfos.clear();

        Member member = new Member();
        member.setUserId(ElainaChatApplication.getInstance().getCurrentUser().getId());
        System.out.println("userId" + member.getUserId());
        Content content = new Content(ContentType.CONVERSATIONINFO, member);
        client = ElainaChatApplication.getInstance().getClient();
        client.sendMessage(content);

    }
    //用于结束加载动画
    public void stopLoading() {
        // 停止加载指示器
        swipeRefresh.setRefreshing(false);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (conversationInfos.isEmpty()) {
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
                Toast.makeText(ConversationInfoActivity.this, "已发送好友请求", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onConversationItemClick(ConversationInfo conversationInfo) {
        // 跳转到聊天界面
        Intent intent = new Intent(this, ChatActivity.class);
        ElainaChatApplication.getInstance().setCurrentConversation(conversationInfo);
        startActivity(intent);
    }
}