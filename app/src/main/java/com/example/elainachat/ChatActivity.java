package com.example.elainachat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.view.Menu;
import android.view.MenuItem;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 初始化视图
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        // 设置RecyclerView
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        messageAdapter.setOnLoadMoreListener(
                new MessageAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        try {
                            List<Message> newmessages = new ArrayList<>();
                            // 模拟加载更多消息
                            for (int i = 0; i < 20; i++) {
                                newmessages.add(new Message("这是新的第 " + i + " 条消息", "bot"));
                            }
                            // 使用Handler.post延迟到主线程的下一个消息循环
                            new Handler(Looper.getMainLooper()).post(() -> {
                                messageAdapter.addMessagesAtStart(newmessages);
                        });
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            //打印错误信息
                            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            System.out.println(e.getMessage());
                        }
                    }
                }
        );
        chatRecyclerView.setAdapter(messageAdapter);

        // 添加欢迎消息
        addMessage("你好！我是助手，有什么可以帮你的吗？","bot");

        // 设置发送按钮点击事件
        sendButton.setOnClickListener(v -> sendMessage());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("聊天界面");
        toolbar.setNavigationOnClickListener(v -> finish());
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
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // 添加用户消息
            addMessage(messageText,"User");
            // 清空输入框
            messageEditText.setText("");
            // 模拟机器人回复
            replayMessage(messageText);
        }
    }

    private void addMessage(String message,String sender) {
        messageAdapter.addMessage(new Message(message,sender));
        scrollToBottom();
    }

    private void scrollToBottom() {
        chatRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }

    private void replayMessage(String userMessage) {
        // 现在只是简单地模拟一个回复
        String botResponse = "我收到了你的消息：" + userMessage;
        addMessage(botResponse,"bot");
    }
}