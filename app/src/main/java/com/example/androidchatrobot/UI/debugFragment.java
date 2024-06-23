package com.example.androidchatrobot.UI;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidchatrobot.R;
import com.linhaodev.prism4jx.Prism4jGrammarLocator;

import org.commonmark.node.FencedCodeBlock;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonPlugin;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.recycler.MarkwonAdapter;
import io.noties.markwon.recycler.SimpleEntry;
import io.noties.markwon.syntax.Prism4jSyntaxHighlight;
import io.noties.markwon.syntax.Prism4jThemeDefault;
import io.noties.markwon.syntax.SyntaxHighlight;
import io.noties.markwon.syntax.SyntaxHighlightPlugin;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.annotations.PrismBundle;
import kotlin.text.Regex;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link debugFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class debugFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public debugFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment debugFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static debugFragment newInstance(String param1, String param2) {
        debugFragment fragment = new debugFragment();
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
    View v;
    private static Markwon markwon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_debug, container, false);
        TextView tv3=v.findViewById(R.id.debugtxt3);
        TextView tv2=v.findViewById(R.id.debugtxt2);
        TextView tv1=v.findViewById(R.id.debugtxt1);
        setMarkdownText(getContext(),tv1," ### 方法1：作为Module导入 1. **创建一个新的Module**：在您的项目中创建一个新的Module，可以选择Android Library作为Module类型。这可以通过Android Studio的File > New > New Module...来完成。 2. **复制源代码**：将下载的库源代码复制到您新创建的Module的`src/main/java`目录下。同时，确保资源文件（如果有的话）被复制到正确的资源目录下。");

        setMarkdownText(getContext(),tv2," #include <iostream>\n" +
                "\n" +
                "int main() {\n" +
                "    std::cout << \"Hello, World!\" << std::endl;\n" +
                "    return 0;\n" +
                "} ");
        // 修改这里，返回初始化后的view
        setMarkdownText(getContext(),tv3,"$$\\\\text{A long division \\\\longdiv{12345}{13}$$");
        return v;
    }


    static class MyPlugin extends AbstractMarkwonPlugin{
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
            Toast.makeText(context,"success",Toast.LENGTH_SHORT);
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
        }
    }

    private static Markwon createMarkwonInstance(Context context, TextView textView) {
        // 必要时，根据需要初始化Prism4jGrammarLocator


        return Markwon.builder(context)
                .usePlugin(new MyPlugin(context))
                .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
                    @Override
                    public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                            builder.inlinesEnabled(true);
                            builder.theme().textColor(Color.BLUE);
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