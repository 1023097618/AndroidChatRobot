package com.example.androidchatrobot.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.SpannableString;

import com.example.androidchatrobot.R;
import com.example.androidchatrobot.util.IAfterMessageHistoriesChange;
import com.example.androidchatrobot.pojo.Message;
import com.example.androidchatrobot.pojo.MessageHistory;
import com.example.androidchatrobot.pojo.Setting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DataManager {
    private static DataManager Instance;
    public Context context;
    public static DataManager GetInstance(){
        if(Instance==null){
            Instance=new DataManager();
        }
        return Instance;
    }
    private DataManager(){}

    private List<MessageHistory> messageHistories;
    public List<IAfterMessageHistoriesChange> IAfterMessageHistoriesChanges;
    public Setting setting;

    public MessageHistory GetNewMessageHistory(){
        return new MessageHistory(new LinkedList<Message>(),System.currentTimeMillis());
    }

    public void AddAfterMessageHistoriesChangeEvent(IAfterMessageHistoriesChange iAfterMessageHistoriesChange){
        if(IAfterMessageHistoriesChanges==null){
            IAfterMessageHistoriesChanges=new LinkedList<>();
        }
        IAfterMessageHistoriesChanges.add(iAfterMessageHistoriesChange);
    }
    private void DoAfterMessageHistoriesChangeEvent(){
        if (IAfterMessageHistoriesChanges!=null){
            for(IAfterMessageHistoriesChange iAfterMessageHistoriesChange:IAfterMessageHistoriesChanges){
                iAfterMessageHistoriesChange.AfterMessageHistoriesChange(messageHistories);
            }
        }
    }

    public List<MessageHistory> getMessageHistories(Context context) {
        if(messageHistories==null){
            SharedPreferences prefs = context.getSharedPreferences("chatMessage", Context.MODE_PRIVATE);
            Gson gson=new Gson();
            String json = prefs.getString("key", null);
            Type type = new TypeToken<LinkedList<MessageHistory>>() {}.getType();
            messageHistories = gson.fromJson(json, type);
            if(messageHistories==null){
                messageHistories=new LinkedList<MessageHistory>();
            }
        }
        return messageHistories;
    }

    //call when message change
    public void SaveMessageHistory(Context context){
        SharedPreferences prefs=context.getSharedPreferences("chatMessage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson=new Gson();
        String json = gson.toJson(messageHistories);
        editor.putString("key", json);
        editor.apply();
        DoAfterMessageHistoriesChangeEvent();
    }

    //call when add messageHistory
    public void AddMessageHistory(Context context,MessageHistory messageHistory){
        messageHistories.add(0,messageHistory);
        SaveMessageHistory(context);
    }
    public void DeleteMessageHistory(Context context,int position){
        messageHistories.remove(position);
        SaveMessageHistory(context);
    }

    public Setting getSetting(Context context){
        if(setting==null){
            SharedPreferences prefs = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = prefs.getString("key", null);
            Type type = new TypeToken<Setting>() {}.getType();
            setting = gson.fromJson(json, type);
            if(setting==null){
                setting=new Setting(1d,10,"",0,1);
                saveSetting(context);
            }
        }
        return setting;
    }

    public void saveSetting(Context context){
        SharedPreferences prefs=context.getSharedPreferences("setting",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(setting);
        editor.putString("key", json);
        editor.apply();
    }

    public List<String> getVisionModels(){
        return Arrays.asList(context.getResources().getStringArray(R.array.vision_model_array));
    }

    public void DeleteMsg(List<Message> messages,int position){
        messages.remove(position);
        SaveMessageHistory(context);
    }
}

