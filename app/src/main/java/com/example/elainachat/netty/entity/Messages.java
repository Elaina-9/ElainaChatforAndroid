package com.example.elainachat.netty.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
/**
 * <p>
 * 
 * </p>
 *
 * @author elaina
 * @since 2025-02-28
 */
@Getter
@Setter
public class Messages implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String conversationId;

    private Long senderId;

    private Long receiverId;

    private String messageContent;

    private LocalDateTime createdAt;
    public Messages(Long senderId,Long receiverId,String messageContent){
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
        this.createdAt = LocalDateTime.now();
    }
}
