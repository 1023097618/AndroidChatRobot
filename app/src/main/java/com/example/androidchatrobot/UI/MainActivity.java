package com.example.androidchatrobot.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.androidchatrobot.Manager.DataManager;
import com.example.androidchatrobot.R;
import com.example.androidchatrobot.pojo.MessageHistory;
import com.example.androidchatrobot.util.IAfterMessageHistoriesChange;
import com.example.androidchatrobot.util.UtilHelpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataManager.GetInstance().context=this;
        DataManager.GetInstance().getSetting(this);
        setTheme(R.style.Default_TextSize_Middle);
        UIBuild();
        AddButtonEvent();
    }



    private void AddButtonEvent() {
        //menu button
        ImageButton button1 = (ImageButton) findViewById(R.id.menubtn);
        button1.setOnClickListener((View v) -> {
                    // open the drawer
                    DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawerLayout.openDrawer(GravityCompat.START);
                }
        );

        //setting button
        Button settingButton = (Button) findViewById(R.id.settingbtn);
        settingButton.setOnClickListener((View v) -> {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    SettingFragment sf = new SettingFragment();
                    ft.replace(R.id.flContent, sf);
                    ft.commit();
                    closeDrawer();
                }
        );

        //add button
        Button addButton=findViewById(R.id.addchatbtn);
        addButton.setOnClickListener((View v)->{
            closeDrawer();
            ShowChatFragment();
        });
    }

    private void closeDrawer(){
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }
    HistoryChatAdapter historyChatAdapter;
    private void UIBuild() {
        ShowChatFragment();

        //recycleView
        RecyclerView recyclerView=findViewById(R.id.chat_history_lists);
        historyChatAdapter=new HistoryChatAdapter(DataManager.GetInstance().getMessageHistories(this));
        recyclerView.setAdapter(historyChatAdapter);
        LinearLayoutManager llm=new LinearLayoutManager(this);
        llm.setStackFromEnd(false);
        recyclerView.setLayoutManager(llm);
        DataManager.GetInstance().AddAfterMessageHistoriesChangeEvent((List<MessageHistory> messageHistories)->{
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            historyChatAdapter.notifyDataSetChanged();
                        }
                    }
            );
        });

        //drawer width
        // 获取屏幕宽度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // 找到您的 RelativeLayout
        RelativeLayout relativeLayout = findViewById(R.id.drawer);
        // 设置宽度为屏幕宽度的一半
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
        params.width = screenWidth / 2;
        relativeLayout.setLayoutParams(params);
    }

    private void ShowChatFragment(){
        MessageHistory messageHistory=DataManager.GetInstance().GetNewMessageHistory();
        ShowChatFragment(messageHistory);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                UtilHelpers.hideKeyboard(ev, view, MainActivity.this);//调用方法判断是否需要隐藏键盘
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void ShowChatFragment(MessageHistory messageHistory){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ChatFragment cf = new ChatFragment(messageHistory);
        ft.replace(R.id.flContent, cf);
        ft.commit();
//
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        debugFragment cf = new debugFragment();
//        ft.replace(R.id.flContent, cf);
//        ft.commit();

    }

    public class HistoryChatAdapter extends RecyclerView.Adapter<HistoryChatAdapter.ChatHistoryHolder> {

        private List<MessageHistory> messageHistories;
        public HistoryChatAdapter(List<MessageHistory> messageHistories){
            this.messageHistories=messageHistories;
        }

        @NonNull
        @Override
        public ChatHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_history_item,null);
            ChatHistoryHolder chatHistoryHolder=new ChatHistoryHolder(view);
            return chatHistoryHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ChatHistoryHolder holder, int position) {
            MessageHistory messageHistory=messageHistories.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date(messageHistory.getStartTimestamp()));
            holder.chat_history_title.setText(messageHistory.getTitle());
            holder.chat_history_time.setText(date);

            //select history item
            holder.chat_history_layout.setOnClickListener((View v)->{
                ShowChatFragment(messageHistory);
            });
            holder.chat_history_layout.setOnLongClickListener((View v)->{
                PopupMenu popupMenu=new PopupMenu(getApplicationContext(),v);
                popupMenu.getMenuInflater().inflate(R.menu.chathistorymenu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener((MenuItem item)->{
                    int itemid=item.getItemId();
                    if(itemid==R.id.deleteFile){
                        DataManager.GetInstance().DeleteMessageHistory(getApplicationContext(),position);
                        ShowChatFragment();
                    }
                    return true;
                });
                popupMenu.show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return messageHistories.size();
        }

        class ChatHistoryHolder extends RecyclerView.ViewHolder{
            RelativeLayout chat_history_layout;
            TextView chat_history_title;
            TextView chat_history_time;
            public ChatHistoryHolder(@NonNull View itemView) {
                super(itemView);
                chat_history_layout=itemView.findViewById(R.id.chat_history_layout);
                chat_history_title=itemView.findViewById(R.id.chat_history_title);
                chat_history_time=itemView.findViewById(R.id.chat_history_time);
            }
        }
    }


}