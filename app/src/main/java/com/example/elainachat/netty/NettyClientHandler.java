package com.example.elainachat.netty;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.elainachat.ChatActivity;
import com.example.elainachat.ChatViewModel;
import com.example.elainachat.ConversationInfoActivity;
import com.example.elainachat.ConversationInfoAdapter;
import com.example.elainachat.ElainaChatApplication;
import com.example.elainachat.MessageAdapter;
import com.example.elainachat.netty.entity.Content;
import com.example.elainachat.netty.entity.ContentType;
import com.example.elainachat.netty.entity.CustomGson;
import com.example.elainachat.netty.entity.ConversationInfo;
import com.example.elainachat.netty.entity.Messages;
import com.example.elainachat.netty.entity.Users;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {
    private Channel channel;
    private Gson gson = CustomGson.getCustomGson();
    private NettyClient client;
    private Handler mainHandler;
    public NettyClientHandler(NettyClient client) {
        this.client = client;
        // 创建主线程Handler
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        ChatViewModel chatViewModel = ElainaChatApplication.getInstance().getChatViewModel();
        ChatActivity chatActivity = null;
        if(ElainaChatApplication.getInstance().getCurrentActivity() instanceof ChatActivity) 
            chatActivity = (ChatActivity) ElainaChatApplication.getInstance().getCurrentActivity();
        
        MessageAdapter messageAdapter = ElainaChatApplication.getInstance().getMessageAdapter();

        Content content = gson.fromJson(msg, Content.class);
        ContentType type = content.getType();
        switch(type) {
            case REGISTER:
                Users newUser = gson.fromJson(gson.toJson(content.getData()), Users.class);
                System.out.println("Registered user: " + newUser.toString());
                break;
            case LOGIN:
                Users user = gson.fromJson(gson.toJson(content.getData()), Users.class);
                System.out.println(user.toString());
                break;
            case MESSAGE:
                Messages message = gson.fromJson(gson.toJson(content.getData()), Messages.class);
                Long senderId = message.getSenderId();
                Long currentUserId = ElainaChatApplication.getInstance().getCurrentUser().getId();
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        System.out.println("Received message: " + message.getMessageContent());
                        //如果是自己发送的消息被服务器确认后返回的
                        if(senderId == currentUserId) {
                            //TODO
                            //UI的消息变为发送成功
                        }
                        else{
                            messageAdapter.addMessage(message);
                        }
                    }
                    catch (Exception e) {
                        System.out.println("Error adding message: " + e.getMessage());
                    }
                });

                try {
                    chatViewModel.insert(message);
                }
                catch (Exception e) {
                    System.out.println("Error inserting message: " + e.getMessage());
                }
                break;
            case SERVERRESPONSE:
                System.out.println("Server response: " + content.getData().toString());
                break;
            case CONVERSATIONINFO:
                System.out.println("Received conversation info");
                    List<ConversationInfo> conversationInfos = gson.fromJson(gson.toJson(content.getData()), new TypeToken<List<ConversationInfo>>() {
                    }.getType());
                    ConversationInfoAdapter adapter = ElainaChatApplication.getInstance().getConversationInfoAdapter();
                if(adapter != null) {
                    mainHandler.post(() -> {
                        adapter.updateConversationList(conversationInfos);
                    });
                } else {
                    System.out.println("Adapter is null");
                }
                //获取当前活动页面并结束加载
                if(ElainaChatApplication.getInstance().getCurrentActivity() != null) {
                        ConversationInfoActivity activity = (ConversationInfoActivity) ElainaChatApplication.getInstance().getCurrentActivity();
                        activity.runOnUiThread(() -> {
                            activity.stopLoading();
                        });
                }
                break;
            case OLDCHATRECORD:
                IPage<Messages> messages = gson.fromJson(gson.toJson(content.getData()), new TypeToken<IPage<Messages>>() {}.getType());
                //获取当前活动页面
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        for (Messages message1 : messages.getRecords()) {
                            System.out.println("Received old message: " + message1.getMessageContent());
                            messageAdapter.addMessageAtStart(message1);
                            chatViewModel.insert(message1);
                        }
                    }
                    catch (Exception e) {
                        System.out.println("Error adding message: " + e.getMessage());
                    }
                });
                //结束加载
                if(chatActivity != null) {
                    final ChatActivity chatActivity1 = chatActivity;
                    chatActivity1.runOnUiThread(() -> {
                        chatActivity1.stopLoading();
                    });
                }
                break;
            case NEWCHATRECORD:
                List<Messages> newMessages = gson.fromJson(gson.toJson(content.getData()), new TypeToken<List<Messages>>() {}.getType());
                final ChatActivity finalChatActivity = chatActivity;
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        for (Messages message1 : newMessages) {
                            System.out.println("Received new message: " + message1.getMessageContent());
                            messageAdapter.addMessage(message1);
                            chatViewModel.insert(message1);
                        }
                        finalChatActivity.stopLoading();

                    }
                    catch (Exception e) {
                        System.out.println("Error adding message: " + e.getMessage());
                    }
                });
                break;
            case INITCONVERSATION:
                Type IpageMessageType = new TypeToken<IPage<Messages>>() {}.getType();
                IPage<Messages> initMessages = gson.fromJson(gson.toJson(content.getData()), IpageMessageType);
                System.out.println("Received init messages: " + initMessages.getRecords().toString());

                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        for (Messages message1 : initMessages.getRecords()) {
                            System.out.println("Received init message: " + message1.getMessageContent());
                            messageAdapter.addMessageAtStart(message1);
                            chatViewModel.insert(message1);
                        }
                    }
                    catch (Exception e) {
                        System.out.println("Error adding message: " + e.getMessage());
                    }
                });
        }
        //System.out.println("Server response: " + message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Connected to server");
        this.channel = ctx.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Disconnected from server");
        if(this.channel != null && this.channel.isActive()) {
            this.channel.close();
        }
        //关闭EventLoopGroup
        ctx.channel().eventLoop().shutdownGracefully();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}