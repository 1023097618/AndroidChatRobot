package com.example.androidchatrobot.pojo;

import android.text.Editable;
import android.text.SpannableString;

import com.example.androidchatrobot.util.SpannableUtils;

import java.util.ArrayList;
import java.util.List;

public class Message {
    public static String SENT_BY_ME="me";
    public static String SNET_BY_ROBOT="bot";

    public static String WAIT="please wait...";

    public boolean temp=false;

    public String getRawString(){
        return SpannableUtils.convertContentListToEditable(contents).toString();
    }

    public Message(List<Content> contents, String sentBy) {
        this.contents = contents;
        this.sentBy = sentBy;
    }

    public Message(String question, String sentBy) {
        this(new ArrayList<>(),sentBy);
        this.contents.add(new Content(Content.text,question));
    }

    private List<Content> contents;
    private String sentBy;

    private transient SpannableString spannableString;



    public SpannableString getSpannableString() {
        if(spannableString==null){
            spannableString =new SpannableString(SpannableUtils.convertContentListToEditable(contents));
        }
        return spannableString;
    }


    public List<Content> getContents() {
        return contents;
    }
    public void setFirstContent(String text){
        contents.get(0).setContent(text);
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public String getSentBy() {
        return sentBy;
    }


}
