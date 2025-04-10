package com.example.elainachat.netty.entity;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.elainachat.netty.entity.MessagesDao;
import com.example.elainachat.netty.entity.Messages;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.elainachat.netty.entity.AppDatabase;

public class MessagesRepository {

    private final MessagesDao messagesDao;
    private final ExecutorService executorService;

    public MessagesRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        messagesDao = database.messagesDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    public List<Messages> getLatestMessages(String conversationId) {
        return messagesDao.getLatestMessages(conversationId);
    }
    public List<Messages> getPreviousMessages(String conversationId, long messageId) {
        return messagesDao.getPreviousMessages(conversationId, messageId);
    }

    public void insert(final Messages message) {
        executorService.execute(() -> messagesDao.insert(message));
    }

    public void insertAll(final List<Messages> messages) {
        executorService.execute(() -> messagesDao.insertAll(messages));
    }

    public void update(final Messages message) {
        executorService.execute(() -> messagesDao.update(message));
    }

    public void delete(final Messages message) {
        executorService.execute(() -> messagesDao.delete(message));
    }

    public void deleteAll() {
        executorService.execute(messagesDao::deleteAll);
    }
}