package com.example.androidchatrobot.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Base64;

import com.example.androidchatrobot.pojo.Content;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SpannableUtils {
    public static Editable convertContentListToEditable(List<Content> contents) {
        SpannableStringBuilder editable = new SpannableStringBuilder();

        for (Content content : contents) {
            if (content.getType().equals(Content.text)) {
                // 直接追加文本
                editable.append(content.getContent());
            } else if (content.getType().equals(Content.image)) {
                // 将Base64字符串转换回Bitmap
                Bitmap bitmap = convertBase64ToBitmap(content.getContent());
                // 创建一个ImageSpan
                Drawable drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                ImageSpan imageSpan = new ImageSpan(drawable);
                // 插入图片到SpannableStringBuilder
                // 需要先添加一个占位符，然后设置ImageSpan
                int start = editable.length();
                editable.append("\uFFFC"); // 使用对象占位符
                editable.setSpan(imageSpan, start, editable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            // 如果有更多内容类型，可以在这里添加更多的else if语句
        }

        return editable;
    }

    private static Bitmap convertBase64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    public static List<Content> extractEditTextContent(Editable content) {
        List<Content> contentList = new ArrayList<>();
        ImageSpan[] imageSpans = content.getSpans(0, content.length(), ImageSpan.class);
        int lastIndex = 0;
        for (ImageSpan span : imageSpans) {
            int start = content.getSpanStart(span);
            int end = content.getSpanEnd(span);
            // 添加ImageSpan之前的文字
            if (start > lastIndex) {
                contentList.add(new Content(Content.text,content.subSequence(lastIndex, start).toString()));
            }
            // 添加ImageSpan
            Drawable drawable = span.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            String base64=convertBitmapToBase64(bitmap);
            contentList.add(new Content(Content.image,base64));
            lastIndex = end;
        }

        // 添加最后的文字
        if (lastIndex < content.length()) {
            contentList.add(new Content(Content.text,content.subSequence(lastIndex, content.length()).toString()));
        }

        return contentList;
    }
    private static String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
