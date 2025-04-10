package com.example.elainachat.netty.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.elainachat.netty.entity.LocalDateTimeConverter;

import java.time.LocalDateTime;

@Entity(
        tableName = "messages",
        indices = {
                @Index("conversationId"),
                @Index(value = {"id", "conversationId"}, unique = true)
        }
)
@TypeConverters(LocalDateTimeConverter.class)
public class Messages {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String conversationId;

    private long senderId;

    private long receiverId;

    @NonNull
    private String messageContent;

    private LocalDateTime createdAt;

    public Messages(String conversationId,Long senderId, long receiverId, @NonNull String messageContent) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(@NonNull String conversationId) {
        this.conversationId = conversationId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    @NonNull
    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(@NonNull String messageContent) {
        this.messageContent = messageContent;
    }

    @NonNull
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}