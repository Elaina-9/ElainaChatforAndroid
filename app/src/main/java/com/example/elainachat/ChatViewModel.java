package com.example.elainachat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.example.elainachat.netty.entity.Messages;
import com.example.elainachat.netty.entity.MessagesRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private final MessagesRepository repository;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        repository = new MessagesRepository(application);
    }
    public List<Messages> getLatestMessages(String conversationId) {
        return repository.getLatestMessages(conversationId);
    }

    public List<Messages> getPreviousMessages(String conversationId, long messageId) {
        return repository.getPreviousMessages(conversationId, messageId);
    }
    public void insert(Messages message) {
        repository.insert(message);
    }

    public void insertAll(List<Messages> messages) {
        repository.insertAll(messages);
    }

    public void update(Messages message) {
        repository.update(message);
    }

    public void delete(Messages message) {
        repository.delete(message);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}