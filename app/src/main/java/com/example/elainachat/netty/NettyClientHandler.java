package com.example.elainachat.netty;

import android.os.Handler;
import android.os.Looper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.elainachat.ElainaChatApplication;
import com.example.elainachat.netty.entity.Content;
import com.example.elainachat.netty.entity.ContentType;
import com.example.elainachat.netty.entity.CustomGson;
import com.example.elainachat.netty.entity.FriendsInfo;
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
                System.out.println("Received message: " + message.getMessageContent());
                break;
            case SERVERRESPONSE:
                System.out.println("Server response: " + content.getData().toString());
                break;
            case CHATHISTORY:
                Type pageType = new TypeToken<IPage<Messages>>(){}.getType();
                IPage<Messages> messages = gson.fromJson(gson.toJson(content.getData()), pageType);
                //如果lastMessageId为0，说明是第一次加载聊天记录
                if(ElainaChatApplication.getInstance().getLastMessageId() == 0) {
                    ElainaChatApplication.getInstance().setLastMessageId(messages.getRecords().get(0).getId());
                }
                ElainaChatApplication.getInstance().setCurrentPage(messages.getCurrent() + 1);
                mainHandler.post(() -> {
                    for (Messages m : messages.getRecords()) {
                        ElainaChatApplication.getInstance().getMessageAdapter().addMessageAtStart(m);
                        System.out.println(m.getSenderId() + "  send to " + m.getReceiverId() + " : " + m.getMessageContent());
                    }
                });
                break;
            case FRIENDQUERY:
                Type friendListType = new TypeToken<List<FriendsInfo>>(){}.getType();
                List<FriendsInfo> friends = gson.fromJson(gson.toJson(content.getData()), friendListType);
                for(FriendsInfo friend : friends) {
                    System.out.println("Friend: " + friend.toString());
                    mainHandler.post(() -> ElainaChatApplication.getInstance().getFriendAdapter().addFriend(friend));
                }
                break;
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