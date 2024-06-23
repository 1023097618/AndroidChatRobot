package com.example.androidchatrobot.util;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.LeadingMarginSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidchatrobot.Manager.DataManager;
import com.example.androidchatrobot.R;
import com.example.androidchatrobot.UI.debugFragment;
import com.example.androidchatrobot.pojo.Message;
import com.linhaodev.prism4jx.Prism4jGrammarLocator;

import org.commonmark.node.FencedCodeBlock;

import java.util.List;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.recycler.MarkwonAdapter;
import io.noties.markwon.recycler.SimpleEntry;
import io.noties.markwon.syntax.Prism4jSyntaxHighlight;
import io.noties.markwon.syntax.Prism4jThemeDefault;
import io.noties.markwon.utils.LeadingMarginUtils;
import io.noties.prism4j.Prism4j;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{
    List<Message> messageList;
    TextView textView;

    private Markwon markwon;
    public MessageAdapter(List<Message> messageList,TextView textView){
        this.messageList=messageList;
        this.textView=textView;
        markwon=createMarkwonInstance(DataManager.GetInstance().context, textView);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,null);
        MyViewHolder myViewHolder=new MyViewHolder(chatView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message=messageList.get(position);
        LinearLayout activeLayout=null;
        if(message.getSentBy().equals(Message.SENT_BY_ME)){
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightTextView.setText(message.getSpannableString());

            activeLayout=holder.rightChatView;
        }else{
            MarkwonAdapter markwonAdapter=MarkwonAdapter.builderTextViewIsRoot(R.layout.adapter_default_entry)
                            .include(FencedCodeBlock.class, SimpleEntry.create(R.layout.adapter_fenced_code_block,R.id.text))
                                    .build();
            RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(DataManager.GetInstance().context);
            holder.leftTextView.setLayoutManager(layoutManager);
            holder.leftTextView.setAdapter(markwonAdapter);
            String text=message.getRawString();
            markwonAdapter.setMarkdown(markwon,message.getRawString());
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.rightChatView.setVisibility(View.GONE);

            activeLayout=holder.leftChatView;
        }

       activeLayout.setOnLongClickListener((View v)->{
            PopupMenu popupMenu=new PopupMenu(DataManager.GetInstance().context, v);
            popupMenu.getMenuInflater().inflate(R.menu.msgmenu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener((MenuItem item)->{
                int itemid=item.getItemId();
                int nowPosition=messageList.indexOf(message);
                if(itemid==R.id.deleteMsg){
                    if(nowPosition!=-1) {
                        DataManager.GetInstance().DeleteMsg(messageList, nowPosition);
                        this.notifyItemRemoved(nowPosition);
                    }
                }
                return true;
            });
            popupMenu.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftChatView,rightChatView;
        TextView rightTextView;
        RecyclerView leftTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView=itemView.findViewById(R.id.left_chat_view);
            rightChatView=itemView.findViewById(R.id.right_chat_view);
            leftTextView=itemView.findViewById(R.id.left_chat_text_view);
            rightTextView=itemView.findViewById(R.id.right_chat_text_view);
        }
    }




    static class MyPlugin extends AbstractMarkwonPlugin {
        private Context context;

        public MyPlugin(Context context) {
            this.context = context;
        }

        Prism4j prism4j = new Prism4j(new Prism4jGrammarLocator());
        @Override
        public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
            super.configureTheme(builder);
            builder.codeBlockTextColor(Color.WHITE);
            builder.codeBlockBackgroundColor(Color.BLACK);
        }

        @Override
        public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
            super.configureConfiguration(builder);
            builder.syntaxHighlight(Prism4jSyntaxHighlight.create(prism4j, Prism4jThemeDefault.create()));
        }

        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
            super.configureSpansFactory(builder);
            // 根据实际情况配置自定义span
            builder.appendFactory(FencedCodeBlock.class, new SpanFactory() {
                @Nullable
                @Override
                public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                    return new CopyContentsSpan(context);
                }
            });
            builder.appendFactory(FencedCodeBlock.class, new SpanFactory() {
                @Nullable
                @Override
                public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                    return new CopyIconSpan(getDrawable(DataManager.GetInstance().context, R.drawable.ic_code_white_24dp));
                }
            });

        }
    }

    static class CopyIconSpan implements LeadingMarginSpan {
        Drawable icon;
        public CopyIconSpan(Drawable icon) {
            this.icon=icon;
            if (icon.getBounds().isEmpty()) {
                icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            }
        }

        @Override
        public int getLeadingMargin(boolean first) {
            return 0;
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
            // called for each line of text, we are interested only in first one
            if (!LeadingMarginUtils.selfStart(start, text, this)){
                return;
            }
            int save = c.save();
            try {
                // horizontal position for icon
                float w = (float) icon.getBounds().width();
                // minus quarter width as padding
                float left = layout.getWidth() - w - (w / 4F);
                c.translate(left, (float) top);
                icon.draw(c);
            } finally {
                c.restoreToCount(save);
            }
        }
    }

    static class CopyContentsSpan extends ClickableSpan {
        Context context;

        public CopyContentsSpan(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View widget) {
            // 尝试转换widget为TextView并获取其文本
            Spanned spanned = widget instanceof TextView ? ((TextView) widget).getText() instanceof Spanned ? (Spanned) ((TextView) widget).getText() : null : null;
            // 如果转换失败，则直接返回
            if (spanned == null) return;

            // 获取span的开始和结束位置
            int start = spanned.getSpanStart(this);
            int end = spanned.getSpanEnd(this);

            // 默认情况下，代码块在内容之前和之后都有新行
            String contents = spanned.subSequence(start, end).toString().trim();

            // 获取系统的剪贴板服务
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建剪贴数据并设置到剪贴板
            clipboard.setPrimaryClip(ClipData.newPlainText(null, contents));
            // 显示复制成功的Toast消息
            Toast.makeText(context,"code copied",Toast.LENGTH_LONG).show();
        }



        @Override
        public void updateDrawState(@NonNull TextPaint ds) {

        }
    }

    private static Markwon createMarkwonInstance(Context context, TextView textView) {
        return Markwon.builder(context)
                .usePlugin(new MyPlugin(context))
                .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
                    @Override
                    public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                        builder.inlinesEnabled(true);
                        builder.theme().textColor(ContextCompat.getColor(DataManager.GetInstance().context, R.color.royal_blue));
                    }
                }))
                .build();
    }

    public static void setMarkdownText(Context context, TextView textView, String markdown) {
        String processedMarkdown = markdown.replaceAll("(?<!\\$)\\$(?!\\$)", "$$$$");
        Markwon markwon = createMarkwonInstance(context, textView);
        markwon.setMarkdown(textView, processedMarkdown);
    }
}
