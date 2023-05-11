package indi.augusttheodor.aurora.activity;

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
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.regex.Pattern;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.data.ArticleDetailObject;
import indi.augusttheodor.aurora.data.GroupArticleViewHolder;
import indi.augusttheodor.aurora.fragment.ArticleShowCommentFragment;
import indi.augusttheodor.aurora.fragment.ArticleShowTransmitFragment;
import indi.augusttheodor.aurora.tools.AuroraTextClickableSpan;
import indi.augusttheodor.aurora.tools.HeartBeat;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import indi.augusttheodor.aurora.inter.OriginalArticleActivity;
import indi.augusttheodor.aurora.inter.ViewPager2LayoutAdapter;
import okhttp3.Call;
import okhttp3.Response;

public class TransmitActivity extends OriginalArticleActivity {

    public String origin_id;
    public ArticleDetailObject article;
    public GroupArticleViewHolder.GroupArticleObject origin; //源文章的object
    public AppCompatActivity activity;
    public ViewPager2 view_pager;
    public TextView comment_inside;
    public TextView comment_outside;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.activity=this;
        Intent intent=getIntent();
        this.origin_id=intent.getStringExtra("transmit_id");
        super.article_id=origin_id;
        super.type=AuroraApplication.Constants.Transmit;
        setContentView(R.layout.activity_transmit);
        if (origin_id != null) {
            AuroraApplication.httpPackage.asyncRequest(
                    getString(R.string.backend_website)+getString(R.string.backend_article_rect).replace("0",this.origin_id),
                    "GET",
                    null,
                    "ORIGIN_DETAIL",
                    this,
                    this
            ); //获取源帖子的meta
        }
        //获取传值
        comment_inside=findViewById(R.id.set_comment_content);
        comment_outside=findViewById(R.id.set_comment_outside);
        view_pager=findViewById(R.id.viewpager2);
        this.view_pager=findViewById(R.id.viewpager2);
        this.view_pager.setAdapter(new TransmitLayoutAdapter(getSupportFragmentManager(),getLifecycle())); //初始化Adapter
        this.view_pager.setUserInputEnabled(false); //禁止滑动
        //给viewpager2适配adapter
        findViewById(R.id.set_comment_outside).setOnClickListener(this); //点击回复框
        findViewById(R.id.set_like).setOnClickListener(this);
        //findViewById(R.id.set_star).setOnClickListener(this);
        //findViewById(R.id.set_star).setOnLongClickListener(this);
        findViewById(R.id.set_comment).setOnClickListener(this);
        findViewById(R.id.notice).setOnClickListener(HeartBeat.v);
        findViewById(R.id.new_notice).setOnClickListener(HeartBeat.v);
        findViewById(R.id.option).setOnClickListener(this);
        findViewById(R.id.set_comment_content).setOnFocusChangeListener(this); //输入框的焦点监听器，无焦点则返回到普通模式
        //设置监听器
        ((RadioButton) findViewById(R.id.comment)).setChecked(true);
        super.onCreate(savedInstanceState);
    }

    public void writeMeta(){
        runOnUiThread(() -> {
            //写元数据
            //只渲染一次我就不写view holder了
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
            TransmitActivity.super.article=article;
            ((TextView) findViewById(R.id.author_name)).setText(article.author_name);
            AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+AuroraApplication.shiftString(article.author_nick),(ImageView)findViewById(R.id.author_nick),AuroraApplication.short_memory_opt);
            findViewById(R.id.user_link).setTag(article.author);
            ((TextView) findViewById(R.id.article_time)).setText(AuroraApplication.shiftTime(article.time));
            ((RadioButton)findViewById(R.id.comment)).setText(getString(R.string.comment)+article.a_comment);
            ((TextView)findViewById(R.id.like)).setText(getString(R.string.like)+article.a_prefer);
            ((TextView)findViewById(R.id.star)).setText(getString(R.string.star)+article.a_star);
            ((CheckBox)findViewById(R.id.set_like)).setChecked(article.is_prefer);
            //替换Span为图片
            Pattern pattern=Pattern.compile(getString(R.string.image_span));
            ((TextView)findViewById(R.id.article_content)).setText(article.content);
        });
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        if(response.code() == 200){
            switch(tag) {
                case "ORIGIN_DETAIL":
                    TransmitDeltaObject delta=AuroraApplication.httpPackage.gson.fromJson(response.body().string(),new TypeToken<TransmitDeltaObject>(){}.getType());
                    this.article=delta.article;
                    this.origin=delta.origin;
                    writeMeta();
                    runOnUiThread(() -> {
                        findViewById(R.id.origin_article_link).setOnClickListener(v->{
                            Intent jump=new Intent(this, ArticleActivity.class);
                            jump.putExtra("article_id",origin.article_id);
                            startActivity((jump));
                        });
                        ((TextView) findViewById(R.id.origin_article_title)).setText(origin.article_title);
                        ((TextView) findViewById(R.id.origin_author_name)).setText(origin.author_name);
                        AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+AuroraApplication.shiftString(origin.author_nick),(ImageView) findViewById(R.id.origin_author_nick),AuroraApplication.short_memory_opt);
                        findViewById(R.id.origin_user_link).setOnClickListener(v->{
                            Intent jump=new Intent(this, UserDetailActivity.class);
                            jump.putExtra("user_id",origin.author_id);
                            startActivity((jump));
                        });
                        ((TextView) findViewById(R.id.origin_time)).setText(AuroraApplication.shiftTime(origin.article_time));
                        ((TextView) findViewById(R.id.origin_amount)).setText(origin.article_amount);
                    });
                    break;
            }
        }else{
            switch (tag){
                case "ORIGIN_DETAIL":
                    //读取对象
                    this.finish();
                    break;
            }
            OkHttpPackage.showToastResponse(this,response);
        }
        super.onResponse(call,response,tag);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkId) {}

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        super.onMenuItemClick(item);
        switch (item.getItemId()){
            case R.id.share_inside:
                ClipboardManager cm=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("AURORA_LINK",getString(R.string.sign_link).replace("{0}", AuroraTextClickableSpan.transmit).replace("{1}",origin.article_title).replace("{2}",origin_id)));
                Toast.makeText(getApplicationContext(), getString(R.string.hint_clipboard_success), Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    private void setSuper(ArticleShowCommentFragment comment){ //传一下值
        super.comment_fragment=comment;
    }

    class TransmitLayoutAdapter extends ViewPager2LayoutAdapter {

        public TransmitLayoutAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
            ArticleShowCommentFragment comment=new ArticleShowCommentFragment(origin_id,false);
            super.fragment_list.add(comment); //评论
            setSuper(comment);
        }

    }

    public class TransmitDeltaObject{

        public ArticleDetailObject article;
        public GroupArticleViewHolder.GroupArticleObject origin;

    }

}
