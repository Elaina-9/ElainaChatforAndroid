package com.example.elainachat;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import android.view.Menu;
import android.view.MenuItem;

import com.example.elainachat.netty.NettyClient;
import com.example.elainachat.netty.entity.Content;
import com.example.elainachat.netty.entity.ContentType;
import com.example.elainachat.netty.entity.Conversation;
import com.example.elainachat.netty.entity.ConversationInfo;
import com.example.elainachat.netty.entity.Messages;
import com.example.elainachat.netty.entity.MessagesRepository;
import com.example.elainachat.netty.entity.Users;


public class ChatActivity extends AppCompatActivity {
    private ChatViewModel chatViewModel;
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NettyClient client;
    private List<Messages> messages;
    private MessageAdapter messageAdapter;
    private ConversationInfo currentConversation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        loadInitialMessages();

    }

    //用于查询历史消息，先查本地数据库，如果没有则向服务器请求
    private void loadMessages() {
        // 显示加载指示器
        swipeRefreshLayout.setRefreshing(true);
        //获得目前最新消息的ID
        long lastMessageId = messages.get(0).getId();

        //后台线程查询数据库
        new Thread(()-> {
            List<Messages> latestMessages = chatViewModel.getPreviousMessages(currentConversation.getConversationId(), lastMessageId);
            if (latestMessages != null && !latestMessages.isEmpty()) {
                System.out.println("本地有消息，直接加载");
                for(Messages message : latestMessages) {
                    messageAdapter.addMessageAtStart(message);
                }
                runOnUiThread(() -> {
                    stopLoading();
                });
            } else {
                //本地没有消息，向服务器请求
                System.out.println("本地没有消息，向服务器请求");
                Content content = new Content(ContentType.OLDCHATRECORD, messages.get(0));
                client.sendMessage(content);
            }
        }).start();
    }
    public void stopLoading() {
        // 停止加载指示器
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 设置当前活动
        ElainaChatApplication.getInstance().setCurrentActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ElainaChatApplication.getInstance().setMessageAdapter(null);
//        ElainaChatApplication.getInstance().setCurrentPage(1L);
//        ElainaChatApplication.getInstance().setLastMessageId(0L);
    }

    // 创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载菜单资源
        getMenuInflater().inflate(R.menu.chat_toolbar_menu, menu);
        return true;
    }

    // 处理菜单项点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.action_search) {
            Toast.makeText(this, "点击了搜索", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_share) {
            Toast.makeText(this, "点击了分享", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_settings) {
            Toast.makeText(this, "点击了设置", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_about) {
            Toast.makeText(this, "点击了关于", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage() {
        try {
            String messageContent = messageEditText.getText().toString();
            if (messageContent.isEmpty()) {
                return;
            }
            System.out.println(messageContent);
            Messages messages = new Messages(currentConversation.getConversationId(),ElainaChatApplication.getInstance().getCurrentUser().getId(),
                    currentConversation.getUserId(), messageContent);
            messageAdapter.addMessage(messages);
            messageEditText.setText("");
            chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            System.out.println("发送消息: " + messages.getConversationId() + " " + messages.getSenderId() + " " + messages.getReceiverId() + " " + messages.getMessageContent());
            client.sendMessage(new Content(ContentType.MESSAGE, messages));
        }
        catch (Exception e) {
            System.out.println("sendMessage Error:"+e.getMessage());
        }
    }

    //用于进入ChatActivity时初始化
    private void loadInitialMessages() {
        // 显示加载指示器
        swipeRefreshLayout.setRefreshing(true);

        new Thread(() -> {
            try {
                // 1. 先尝试从本地加载消息
                List<Messages> localMessages = chatViewModel.getLatestMessages(currentConversation.getConversationId());

                runOnUiThread(() -> {
                    if (localMessages != null && !localMessages.isEmpty()) {
                        // 有本地消息，直接显示
                        for (Messages message : localMessages) {
                            messageAdapter.addMessageAtStart(message);
                        }
                        chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);

                        // 2. 本地消息加载完成后，再检查是否有新消息
                        checkForNewMessages();
                    } else {
                        // 无本地消息，向服务器请求初始消息
                        Conversation conversation = new Conversation(currentConversation.getConversationId(), LocalDateTime.now());
                        Content content = new Content(ContentType.INITCONVERSATION, conversation);
                        client.sendMessage(content);
                        // 服务器响应会通过NettyClientHandler处理
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e("ChatActivity", "加载消息失败", e);
                    Toast.makeText(ChatActivity.this, "加载消息失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    stopLoading();
                });
            }
        }).start();
    }

    //用于加载新消息
    private void checkForNewMessages() {
        if (messages == null || messages.isEmpty()) {
            stopLoading();
            return;
        }

        try {
            Messages latestMessage = messages.get(messages.size() - 1);
            Content content = new Content(ContentType.NEWCHATRECORD, latestMessage);
            client.sendMessage(content);
            // 服务器响应会通过NettyClientHandler处理，并自动停止加载
        } catch (Exception e) {
            Log.e("ChatActivity", "检查新消息失败", e);
            stopLoading();
        }
    }


    private void init() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        ElainaChatApplication.getInstance().setChatViewModel(chatViewModel);

        // 初始化视图
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                loadMessages();
            }
            catch (Exception e) {
                Log.e("RefreshDatabaseError", "Error loading messages", e);
                // 展示错误信息
                Toast.makeText(this, "加载消息错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        client = ElainaChatApplication.getInstance().getClient();
        messages = ElainaChatApplication.getInstance().getMessages();
        messageAdapter = new MessageAdapter(messages);
        ElainaChatApplication.getInstance().setMessageAdapter(messageAdapter);
        messageAdapter.clear();

        currentConversation = ElainaChatApplication.getInstance().getCurrentConversation();

        // 设置RecyclerView
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(ElainaChatApplication.getInstance().getMessageAdapter());

        // 设置发送按钮点击事件
        sendButton.setOnClickListener(v -> sendMessage());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("聊天界面");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

}