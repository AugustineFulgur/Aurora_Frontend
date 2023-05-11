package indi.augusttheodor.aurora.activity;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.CommentAdapter;
import indi.augusttheodor.aurora.data.ArticleDetailObject;
import indi.augusttheodor.aurora.fragment.ArticleShowCommentFragment;
import indi.augusttheodor.aurora.fragment.ArticleShowTransmitFragment;
import indi.augusttheodor.aurora.tools.AuroraTextClickableSpan;
import indi.augusttheodor.aurora.tools.HeartBeat;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import indi.augusttheodor.aurora.inter.OriginalArticleActivity;
import indi.augusttheodor.aurora.inter.ViewPager2LayoutAdapter;
import indi.augusttheodor.aurora.tools.WaterMarkUtils;

public class TrendActivity extends OriginalArticleActivity {

    private ViewPager2 view_pager;
    public String article_id;
    public ArticleDetailObject article;
    public AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_trend);
        this.activity=this;
        //获取传值
        Intent intent=getIntent();
        this.article_id=intent.getStringExtra("trend_id");
        super.article_id=article_id;
        super.type=AuroraApplication.Constants.Trend;
        if (article_id != null) {
            AuroraApplication.httpPackage.asyncRequest(
                    getString(R.string.backend_website) + getString(R.string.backend_article) + this.article_id,
                    "GET",
                    null,
                    "ARTICLE_MAIN",
                    this,
                    this
            ); //获取文章主楼内容
        }
        //获取传值
        CommentAdapter.comment_inside=findViewById(R.id.set_comment_content);
        CommentAdapter.comment_outside=findViewById(R.id.set_comment_outside);
        findViewById(R.id.set_comment_outside).setOnClickListener(this); //点击回复框
        findViewById(R.id.set_comment_outside).setOnLongClickListener(this);
        findViewById(R.id.set_comment_content).setOnLongClickListener(this); //清除回复对象
        findViewById(R.id.set_like).setOnClickListener(this);
        //findViewById(R.id.set_star).setOnClickListener(this);
        //findViewById(R.id.set_star).setOnLongClickListener(this);
        //findViewById(R.id.set_transmit).setOnClickListener(this);
        findViewById(R.id.set_comment).setOnClickListener(this);
        findViewById(R.id.notice).setOnClickListener(HeartBeat.v);
        findViewById(R.id.new_notice).setOnClickListener(HeartBeat.v);
        findViewById(R.id.option).setOnClickListener(this);
        findViewById(R.id.set_comment_content).setOnFocusChangeListener(this); //输入框的焦点监听器，无焦点则返回到普通模式
        ((RadioGroup)findViewById(R.id.bar)).setOnCheckedChangeListener(this); //上方bar的监听器
        //设置监听器
        //设置监听器
        this.view_pager=findViewById(R.id.viewpager2);
        this.view_pager.setAdapter(new TrendLayoutAdapter(getSupportFragmentManager(),getLifecycle())); //初始化Adapter
        this.view_pager.setUserInputEnabled(false); //禁止滑动
        //给viewpager2适配adapter
        super.view_pager=this.view_pager;
        //赋值到父类
        super.onCreate(savedInstanceState);
        ((RadioButton) findViewById(R.id.comment)).setChecked(true);
        //设置评论被选中
    }

    public void writeMeta(){
        article=super.article;
        runOnUiThread(() -> {
            //写元数据
            //只渲染一次我就不写view holder了
            super.article=article;
            findViewById(R.id.author_name).setOnClickListener(v->{
                Intent jump=new Intent(this, UserDetailActivity.class);
                jump.putExtra("user_id",article.author);
                this.startActivity((jump));
            });
            findViewById(R.id.author_nick).setOnClickListener(v->{
                Intent jump=new Intent(this, UserDetailActivity.class);
                jump.putExtra("user_id",article.author);
                this.startActivity((jump));
            });
            ((TextView) findViewById(R.id.author_name)).setText(article.author_name);
            AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+AuroraApplication.shiftString(article.author_nick),(ImageView)findViewById(R.id.author_nick),AuroraApplication.short_memory_opt);
            ((TextView) findViewById(R.id.article_title)).setVisibility(GONE);
            ((TextView) findViewById(R.id.article_time)).setText(AuroraApplication.shiftTime(article.time));
            ((RadioButton)findViewById(R.id.comment)).setText(getString(R.string.comment)+article.a_comment);
            ((TextView)findViewById(R.id.like)).setText(getString(R.string.like)+article.a_prefer);
            ((TextView)findViewById(R.id.star)).setText(getString(R.string.star)+article.a_star);
            ((CheckBox)findViewById(R.id.set_like)).setChecked(article.is_prefer);
            //替换Span为图片
            AuroraApplication.setImgSpan(article.image,(TextView) findViewById(R.id.article_content),article.content,activity,activity);
            switch (article.safe_type){ //安全处理
                case AuroraApplication.Constants.Type_Public:
                    ((TextView)findViewById(R.id.type)).setText(getString(R.string.article_public));
                    ((TextView)findViewById(R.id.article_content)).setTextIsSelectable(true); //设置可选中
                    break;
                case AuroraApplication.Constants.Type_Protected:
                    ((TextView)findViewById(R.id.type)).setText(getString(R.string.article_protected));
                    //版权，要添加水印
                    findViewById(R.id.safe_watermark).setForeground(WaterMarkUtils.createWaterMark(article.author_name,this));
                    break;
                case AuroraApplication.Constants.Type_Private:
                    ((TextView)findViewById(R.id.type)).setText(getString(R.string.article_private));
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                    break;
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item){
        super.onMenuItemClick(item);
        switch (item.getItemId()){
            case R.id.share_inside:
                ClipboardManager cm=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("AURORA_LINK",getString(R.string.sign_link).replace("{0}", AuroraTextClickableSpan.trend).replace("{1}",article.content.substring(0,20)+"...").replace("{2}",article_id)));
                Toast.makeText(getApplicationContext(), getString(R.string.hint_clipboard_success), Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    private void setSuper(ArticleShowCommentFragment comment,ArticleShowTransmitFragment transmit){ //传一下值
        super.comment_fragment=comment;
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {}

    class TrendLayoutAdapter extends ViewPager2LayoutAdapter {

        public TrendLayoutAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
            ArticleShowCommentFragment comment=new ArticleShowCommentFragment(article_id,true);
            ArticleShowTransmitFragment transmit=new ArticleShowTransmitFragment(article_id);
            super.fragment_list.add(comment); //评论
            super.fragment_list.add(transmit); //转发
            setSuper(comment,transmit);
        }

    }
    
}