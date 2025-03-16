package com.example.elainachat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.view.Menu;
import android.view.MenuItem;

import com.example.elainachat.netty.NettyClient;
import com.example.elainachat.netty.entity.Content;
import com.example.elainachat.netty.entity.ContentType;
import com.example.elainachat.netty.entity.Messages;
import com.example.elainachat.netty.entity.Users;


public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private NettyClient client;
    private List<Messages> messages;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 初始化视图
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        client = ElainaChatApplication.getInstance().getClient();
        messages = ElainaChatApplication.getInstance().getMessages();
        initMessageAdapter();
        messageAdapter.clear();

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

        new Thread(() ->{
        // 模拟加载历史消息
        try {
            Users currentUser = ElainaChatApplication.getInstance().getCurrentUser();
            Content content = new Content(ContentType.CHATHISTORY,new Messages(currentUser.getId(),1L,"100_1"));
            client.sendMessage(content);
        } catch (Exception e) {
            //打印错误信息
            System.out.println(e.getMessage());
        }
    }).start();
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
            Messages messages = new Messages(ElainaChatApplication.getInstance().getCurrentUser().getId(), 2L, messageContent);
            ElainaChatApplication.getInstance().getMessageAdapter().addMessage(messages);
            messageEditText.setText("");
            client = ElainaChatApplication.getInstance().getClient();
            client.sendMessage(new Content(ContentType.MESSAGE, messages));
        }
        catch (Exception e) {
            e.printStackTrace();
            //打印错误信息
            System.out.println(e.getMessage());
        }
    }

    private void initMessageAdapter() {
        messageAdapter = new MessageAdapter(messages);
        messageAdapter.setOnLoadMoreListener(
                new MessageAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        try {
                            String[] result = ElainaChatApplication.getInstance().getCurrentConversationId().split("_");
                            Long receiverId = Long.parseLong(result[1]);

                            Content content = new Content(ContentType.CHATHISTORY,new Messages(
                                    ElainaChatApplication.getInstance().getCurrentUser().getId(),
                                    receiverId,
                                    ElainaChatApplication.getInstance().getLastMessageId().toString()+"_"+ElainaChatApplication.getInstance().getCurrentPage().toString()));
                            client.sendMessage(content);
                        }
                        catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
        );
        ElainaChatApplication.getInstance().setMessageAdapter(messageAdapter);
    }

}