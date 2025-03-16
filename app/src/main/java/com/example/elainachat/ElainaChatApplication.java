package com.example.elainachat;

import android.app.Application;

import com.example.elainachat.netty.NettyClient;
import com.example.elainachat.netty.entity.Content;
import com.example.elainachat.netty.entity.ContentType;
import com.example.elainachat.netty.entity.FriendsInfo;
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
    //服务端地址
    private String host = "10.0.2.2";
    private Users currentUser =new Users(2L,"123456");
    private NettyClient client;

    private List<Messages> messages;
    private MessageAdapter messageAdapter;
    //此处friendApapter需要处理页面转换，从friend界面到chat界面，所以在friendActivity中初始化
    private List<FriendsInfo> friendsList;
    private FriendsAdapter friendAdapter;

    private String currentConversationId = "2_1";
    private Long currentPage = 0L;
    private Long lastMessageId = 0L;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        client = new NettyClient(host,8888);
        messages = new ArrayList<>();
        friendsList = new ArrayList<>();

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