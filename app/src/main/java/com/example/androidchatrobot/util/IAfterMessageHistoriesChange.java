package com.example.androidchatrobot.util;

import com.example.androidchatrobot.pojo.MessageHistory;

import java.util.List;

public interface IAfterMessageHistoriesChange {
    public void AfterMessageHistoriesChange(List<MessageHistory> messageHistories);
}
