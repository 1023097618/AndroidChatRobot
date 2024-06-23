package com.example.androidchatrobot.UI;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.androidchatrobot.Manager.DataManager;
import com.example.androidchatrobot.pojo.Content;
import com.example.androidchatrobot.util.MessageAdapter;
import com.example.androidchatrobot.R;
import com.example.androidchatrobot.pojo.Message;
import com.example.androidchatrobot.pojo.MessageHistory;
import com.example.androidchatrobot.pojo.Setting;
import com.example.androidchatrobot.util.Common;
import com.example.androidchatrobot.util.SpannableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final MediaType JSON=MediaType.get("application/json; charset=utf-8");
    OkHttpClient okHttpClient=new OkHttpClient();
    private MessageHistory messageHistory;

    public ChatFragment(MessageHistory messageHistory) {
        this.messageHistory = messageHistory;
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;

    Setting setting;
    View view;
    Activity activity;
    ActivityResultLauncher<String> mGetContent;
    public static final int CHOOSE_PHOTO = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getActivity()==null){
            return null;
        }

        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_chat, container, false);
        setting= DataManager.GetInstance().getSetting(getActivity());

        //RecycleView
        recyclerView=view.findViewById(R.id.recycle_chat);
        messageAdapter=new MessageAdapter(messageHistory.getMessages(),view.findViewById(R.id.tv_tem));
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        activity=getActivity();

        //send button
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.sendbtn);
        EditText editText=(EditText)view.findViewById(R.id.message_edit_text);
        imageButton.setOnClickListener((View v) -> {
                    if(setting.getApiToken()==null||setting.getApiToken().trim().equals("")){
                        Toast.makeText(DataManager.GetInstance().context,"请在设置中填写api token",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String txt=editText.getText().toString();
                    if(!txt.trim().equals("")){
                        addChat(editText.getText(),Message.SENT_BY_ME);
                        callAPI(messageHistory.getMessages());
                    }
                    editText.setText("");
                }
        );


        //upload button
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        try {
                            if(uri!=null) {
                                insertImageIntoEditText(uri);
                            }
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        view.findViewById(R.id.uploadBtn).setOnClickListener((View v)->{
            if(setting.getApiToken()==null||setting.getApiToken().trim().equals("")){
                Toast.makeText(DataManager.GetInstance().context,"请在设置中填写api token",Toast.LENGTH_SHORT).show();
                return;
            }
            if(DataManager.GetInstance().getVisionModels().contains(setting.getModelName())) {
                mGetContent.launch("image/*");
            }else{
                Toast.makeText(DataManager.GetInstance().context, "这个模型不能上传图片",Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }
    private void insertImageIntoEditText(Uri imageUri) throws FileNotFoundException {
        EditText editText = view.findViewById(R.id.message_edit_text);

        // 获取Drawable并设置边界大小
        Drawable drawable = Drawable.createFromStream(activity.getContentResolver().openInputStream(imageUri), null);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        // 创建ImageSpan
        ImageSpan imageSpan = new ImageSpan(drawable);

        // 创建SpannableString
        SpannableString spannableString = new SpannableString(" ");
        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        // 获取当前EditText的文本
        int start = Math.max(editText.getSelectionStart(), 0);
        int end = Math.max(editText.getSelectionEnd(), 0);
        Editable editableText = editText.getEditableText();

        // 如果是在文本中间插入，可能需要处理插入点的文本替换
        if (start < end) {
            editableText.replace(start, end, spannableString);
        } else {
            editableText.insert(start, spannableString);
        }
    }

    private void addChat(Editable editable,String sentBy){
        if(getActivity()!=null){
            List<Message> messages = messageHistory.getMessages();
            int lastindex=messages.size()-1;
            if(lastindex>-1&& messages.get(lastindex).temp){
                messages.remove(lastindex);
            }
            List<Content> contents= SpannableUtils.extractEditTextContent(editable);
            Message newMessage=new Message(contents,sentBy);
            messages.add(newMessage);
            if(!messageHistory.isSaved()){
                messageHistory.setSaved(true);
                String title=editable.toString();
                if(title.length()> Common.MaxQuestionChar){
                    title=title.substring(0,Common.MaxQuestionChar)+"...";
                }
                messageHistory.setTitle(title);
                DataManager.GetInstance().AddMessageHistory(getActivity(),messageHistory);
            }
            DataManager.GetInstance().SaveMessageHistory(getActivity());
            notifyItem(newMessage,ITEMCHANGE,true,true);
        }
    }
    private void addChat(String question,String sentby){
        List<Content> contents=new ArrayList<>();
        contents.add(new Content(Content.text,question));
        addChat(Editable.Factory.getInstance().newEditable(question), sentby);
    }


    private void addTempChat(String answer,boolean scroll){
        if(getActivity()!=null){
            List<Message> messages = messageHistory.getMessages();
            int lastindex=messages.size()-1;
            if(lastindex>-1&& messages.get(lastindex).temp){
                messages.get(lastindex).setFirstContent(answer);
                notifyItem(messages.get(lastindex),ITEMCHANGE,false,scroll);
            }else{
                Message message=new Message(answer,Message.SNET_BY_ROBOT);
                message.temp=true;
                messages.add(message);
                notifyItem(message,ITEMINSERT,false,scroll);
            }

        }
    }
    private final int ITEMINSERT=0;
    private final int ITEMCHANGE=1;

    private long lastTime = 0;
    // 限制间隔，单位毫秒，这里设置为3秒
    private final long limit = 200;

    private void notifyItem(Message message,int e,boolean must,boolean scroll){
        List<Message> messages=messageHistory.getMessages();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime > limit || must) {
            lastTime = currentTime;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int pos=messages.indexOf(message);
                    if(pos==-1){
                        return;
                    }
                    if(e==ITEMINSERT) {
                        messageAdapter.notifyItemInserted(pos);
                    }else if(e==ITEMCHANGE){
                        messageAdapter.notifyItemChanged(pos);
                    }
                    if(scroll){
                        recyclerView.smoothScrollToPosition(pos);
                    }
                }
            });
        }
    }

    private void callAPI(List<Message> messages){
        JSONObject requestObject=new JSONObject();
        int startindex=messages.size()-setting.getMaxContextNumber();
        messages=new ArrayList<>(messages.subList(Math.max(startindex,0),messages.size()));
        addTempChat(Message.WAIT,true);
        try {
            requestObject.put("model",setting.getModelName());
            JSONArray messagesArray=new JSONArray();
            for(Message msg:messages){
                JSONArray contentArray=new JSONArray();
                JSONObject messageObject=new JSONObject();
                messageObject.put("role", msg.getSentBy().equals(Message.SENT_BY_ME) ? "user" : "assistant");
                for(Content content:msg.getContents()){
                    JSONObject contentObject = new JSONObject();
                    contentObject.put("type",content.getType());
                    contentObject.put(content.getType(), content.getType().equals(Content.text)?content.getContent():new JSONObject().put("url","data:image/jpeg;base64,"+content.getContent()).put("detail",setting.getDetailName()));
                    contentArray.put(contentObject);
                }
                messageObject.put("content",contentArray);
                messagesArray.put(messageObject);
            }
            requestObject.put("max_tokens",4000);
            requestObject.put("temperature",setting.getTemperature());
            requestObject.put("messages",messagesArray);
            requestObject.put("stream",true);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody requestBody=RequestBody.create(requestObject.toString(),JSON);
        Request request=new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer "+setting.getApiToken())
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        addTempChat("Failed To GetResponse Due to"+e.toString(),true);
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()){
                            try {
                                ResponseBody responseBody=response.body();
                                BufferedReader reader = new BufferedReader(responseBody.charStream());
                                String line;
                                StringBuilder answer=new StringBuilder();
                                while ((line = reader.readLine()) != null) {
                                    if(!line.trim().equals("")){
                                        line=line.substring(6);
                                        if(line.equals("[DONE]")){
                                            break;
                                        }
                                        JSONObject responseObject1 = new JSONObject(line);
                                        JSONArray jsonArray=responseObject1.getJSONArray("choices");
                                        String deltaAnswer="";
                                        JSONObject delta=jsonArray.getJSONObject(0).getJSONObject("delta");
                                        if(delta.length()!=0){
                                            deltaAnswer=delta.getString("content");
                                        }
                                        String stopreason= jsonArray.getJSONObject(0).getString("finish_reason");
                                        addTempChat(answer.toString(),false);
                                        answer.append(deltaAnswer);
                                    }
                                }
                                addChat(answer.toString(),Message.SNET_BY_ROBOT);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            addTempChat("Failed To GetResponse Due to"+response.body().string(),true);
                        }
                    }
                }
        );
    }
}