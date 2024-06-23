package com.example.androidchatrobot.pojo;

import java.util.List;

public class MessageHistory {
    private List<Message> messages;
    private final long StartTimestamp;

    private String Title;

    private boolean isSaved;

    public void setTitle(String title) {
        Title = title;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public long getStartTimestamp() {
        return StartTimestamp;
    }

    public MessageHistory(List<Message> messages, long timestamp) {
        this.messages = messages;
        StartTimestamp = timestamp;
    }

    public String getTitle() {
        return Title;
    }
}
