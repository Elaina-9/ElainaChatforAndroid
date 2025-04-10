package com.example.elainachat;

import android.app.Activity;
import android.app.Application;

import com.example.elainachat.netty.NettyClient;

import com.example.elainachat.netty.entity.AppDatabase;
import com.example.elainachat.netty.entity.ConversationInfo;
import com.example.elainachat.netty.entity.Messages;
import com.example.elainachat.netty.entity.Users;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElainaChatApplication extends Application {
    private static ElainaChatApplication instance;
    private AppDatabase database;
    private Activity currentActivity;
    private ChatViewModel chatViewModel;
    //服务端地址
    private String host = "10.0.2.2";
    private Users currentUser =new Users(2L,"");
    private ConversationInfo currentConversation;
    private NettyClient client;

    private List<Messages> messages;
    private MessageAdapter messageAdapter;

    private List<ConversationInfo> conversationInfos;
    private ConversationInfoAdapter conversationInfoAdapter;
    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getDatabase(this);
        instance = this;
        client = new NettyClient(host,8888);
        messages = new ArrayList<>();
        conversationInfos = new ArrayList<>();

//        messageAdapter.setOnLoadMoreListener(
//                new MessageAdapter.OnLoadMoreListener() {
//                    @Override
//                    public void onLoadMore() {
//                        try {
//                            String[] result = ElainaChatApplication.getInstance().getCurrentConversationId().split("_");
//                            Long receiverId = Long.parseLong(result[1]);
//
//                            Content content = new Content(ContentType.CHATHISTORY,new Messages(
//                                    ElainaChatApplication.getInstance().getCurrentUser().getId(),
//                                    receiverId,
//                                    ElainaChatApplication.getInstance().getLastMessageId().toString()+"_"+ElainaChatApplication.getInstance().getCurrentPage().toString()));
//                            client.sendMessage(content);
//                        }
//                        catch (Exception e) {
//                            System.out.println(e.getMessage());
//                        }
//                    }
//                }
//        );
//        new Thread(() ->{
//        // 模拟加载历史消息
//        try {
//            client.start();
//            Content content = new Content(ContentType.CONNECT,"ac");
//            client.sendMessage(content);
//            content = new Content(ContentType.CHATHISTORY,new Messages(1L,2L,"100_1"));
//            client.sendMessage(content);
//        } catch (Exception e) {
//            //打印错误信息
//            System.out.println(e.getMessage());
//        }
//    }).start();
}
    public static ElainaChatApplication getInstance() {
        return instance;
    }

}