package com.example.androidchatrobot.pojo;

import java.util.List;

public class Content {
    public Content(String type, String content) {
        Type = type;
        this.content = content;
    }
    public static String image="image_url";
    public static String text="text";

    private String Type;
    private String content;

    public static String getImage() {
        return image;
    }

    public static void setImage(String image) {
        Content.image = image;
    }

    public static String getText() {
        return text;
    }

    public static void setText(String text) {
        Content.text = text;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
