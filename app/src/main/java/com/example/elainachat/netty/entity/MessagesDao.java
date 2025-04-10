package com.example.elainachat.netty.entity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.elainachat.netty.entity.Messages;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface MessagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Messages message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Messages> messages);

    @Update
    void update(Messages message);

    @Delete
    void delete(Messages message);

    @Query("DELETE FROM messages")
    void deleteAll();

    //获得最新的5条消息
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY id DESC LIMIT 5")
    List<Messages> getLatestMessages(String conversationId);

    //获得messageid之前的5条消息
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND id < :messageId ORDER BY createdAt DESC LIMIT 5")
    List<Messages> getPreviousMessages(String conversationId, long messageId);

}