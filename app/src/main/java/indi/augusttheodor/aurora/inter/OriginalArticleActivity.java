package indi.augusttheodor.aurora.inter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import org.dom4j.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.CreateTransmitActivity;
import indi.augusttheodor.aurora.activity.NoticeActivity;
import indi.augusttheodor.aurora.activity.ReportDialogActivity;
import indi.augusttheodor.aurora.activity.SummaryDialogActivity;
import indi.augusttheodor.aurora.adapter.CommentAdapter;
import indi.augusttheodor.aurora.data.ArticleDetailObject;
import indi.augusttheodor.aurora.data.CommentMetaViewHolder;
import indi.augusttheodor.aurora.fragment.ArticleShowCommentFragment;
import indi.augusttheodor.aurora.tools.AuroraImageLoader;
import indi.augusttheodor.aurora.tools.HeartBeat;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public abstract class OriginalArticleActivity extends AppCompatActivity implements View.OnClickListener, HttpInterface, RadioGroup.OnCheckedChangeListener, View.OnFocusChangeListener , View.OnLongClickListener , PopupMenu.OnMenuItemClickListener { //是所有Article衍生类的父类（Article、Transmit、Trend）

    public String article_id;
    public ArticleDetailObject article;
    public ViewPager2 view_pager;
    public String type; //文章类型
    public ArticleShowCommentFragment comment_fragment;
    public String article_reply_img=""; //储存回复的图片名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //初始化对应关系
        super.onCreate(savedInstanceState);
        //广播部分
        findViewById(R.id.new_notice).setVisibility(HeartBeat.sum==0? View.GONE:View.VISIBLE);
        ((Button)findViewById(R.id.new_notice)).setText(String.valueOf(HeartBeat.sum));
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                findViewById(R.id.new_notice).setVisibility(HeartBeat.sum==0? View.INVISIBLE:View.VISIBLE);
                ((Button)findViewById(R.id.new_notice)).setText(String.valueOf(HeartBeat.sum));
            }
        },new IntentFilter("HEARTBEAT"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_comment_outside: //点击切换到回复模式
                if(!article.is_in_group){
                    //不在组里，弹框说明并不更改模式
                    Toast.makeText(this, getString(R.string.hint_not_in_group), Toast.LENGTH_SHORT).show();
                    break;
                }
                findViewById(R.id.keyboard_reply).setVisibility(View.VISIBLE);
                findViewById(R.id.bottom_1).setVisibility(View.GONE); //切换到回复的模式
                findViewById(R.id.set_comment_content).requestFocus(); //获得焦点
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != im) {
                    im.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                } //弹出键盘
                break;
            case R.id.set_like: //点赞
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_article)+this.article_id+"/"+getString(R.string.backend_set)+getString(R.string.backend_like),
                        "GET",
                        null,
                        "SET_LIKE",
                        this,
                        this
                );
                break;
            case R.id.set_comment: //发送回复的信息
                String comment= ((TextView)findViewById(R.id.set_comment_content)).getText().toString();
                if(comment == null ||comment.isEmpty()){
                    return; //为空不发表
                }
                HashMap dict=new HashMap<String,String>();
                dict.put("comment",comment);
                dict.put("type",type); //来自子类赋值
                if(CommentAdapter.comment_id!=""){
                    //有回复对象
                    dict.put("reply",CommentAdapter.comment_id);
                    CommentAdapter.comment_id=""; //重置
                    CommentAdapter.comment_outside.setText("");
                    CommentAdapter.comment_inside.setText("");
                }
                if(this.article_reply_img!=""){
                    //有回复图片
                    dict.put("img",this.article_reply_img);
                    this.article_reply_img="";
                    findViewById(R.id.image_btn).setSelected(false);
                }
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_article)+this.article_id+"/"+getString(R.string.backend_set)+getString(R.string.backend_comment),
                        "POST",
                        dict,
                        "SET_COMMENT",
                        this,
                        this
                ); //获取文章主楼内容
                break;
            case R.id.set_transmit: //转发
                Intent jump=new Intent(view.getContext().getApplicationContext(), CreateTransmitActivity.class);
                jump.putExtra("article_id",article_id);
                view.getContext().startActivity((jump));//跳转到转发页面
                break;
            case R.id.set_star:
                SummaryDialogActivity.fast_mode=false;
                Intent star_intent=new Intent(this,SummaryDialogActivity.class);
                star_intent.putExtra("article_id",article_id);
                view.getContext().startActivity(star_intent);
                break;
            case R.id.option:
                PopupMenu menu=new PopupMenu(view.getContext(),findViewById(R.id.option));
                menu.getMenuInflater().inflate(R.menu.article_option_menu,menu.getMenu());
                refreshMenu(menu);
                menu.setOnMenuItemClickListener(this);
                menu.show();
                break;
            case R.id.image_btn:
                //带图评论
                ImagePicker picker=ImagePicker.getInstance();
                picker.setImageLoader(new AuroraImageLoader());
                picker.setCrop(false);
                picker.setMultiMode(false);
                startActivityForResult(new Intent(this, ImageGridActivity.class), 1001);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //选择回复图片的回调
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) { //上传回调，插入图片和上传（不整这些虚的了，就直接来吧^ ^）
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            for(ImageItem i:images){
                AuroraApplication.httpPackage.asyncImageRequest(
                        getString(R.string.backend_website) + getString(R.string.backend_upload_image),
                        i,
                        "UPLOAD_IMG",
                        (call, response, tag) -> {
                            if(response.code()==200){
                                this.article_reply_img=response.body().string();
                                runOnUiThread(()->((RadioButton)findViewById(R.id.image_btn)).setSelected(true));
                            }
                        },
                        this
                );
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.set_star:
                //长按一键收藏
                //这里应该放个动画
                SummaryDialogActivity.fast_mode=true;
                Intent star_intent=new Intent(this,SummaryDialogActivity.class);
                star_intent.putExtra("article_id",article_id);
                startActivity(star_intent);
                break;
            case R.id.set_comment_content:
            case R.id.set_comment_outside: //这俩情况下就撤销回复
                CommentAdapter.comment_id=""; //置空
                CommentAdapter.comment_outside.setHint(R.string.content_comment_hint);
                CommentAdapter.comment_inside.setHint(R.string.content_comment_hint); //取消回复
                break;
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.top:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_article_set_top).replace("{0}",article_id),
                        "GET",
                        null,
                        "TOP",
                        this,
                        this
                );
                break;
            case R.id.up:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_article_up).replace("0",article_id),
                        "GET",
                        null,
                        "NO_MORE",
                        this,
                        this
                );
                break;
            case R.id.delete:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_article_delete).replace("0",article_id),
                        "GET",
                        null,
                        "DELETE",
                        this,
                        this
                );
                break;
            case R.id.report:
                Intent jump=new Intent(this, ReportDialogActivity.class);
                jump.putExtra("sth_id",article_id);
                jump.putExtra("type",AuroraApplication.Constants.Article);
                startActivity(jump);
                break;
        }
        return false;
    }

    public void writeMeta(){}

    public void refreshMenu(PopupMenu menu){
        menu.getMenu().findItem(R.id.delete).setVisible(article.is_author || article.is_admin);
        menu.getMenu().findItem(R.id.top).setVisible(article.is_admin);
        menu.getMenu().findItem(R.id.top).setTitle(article.is_top?getString(R.string.cancel_top):getString(R.string.top));
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch(tag) {
            case "ARTICLE_MAIN":
                if (response.code() == 200) {
                    //读取对象
                    String s = response.body().string();
                    this.article = AuroraApplication.httpPackage.gson.fromJson(s, new TypeToken<ArticleDetailObject>() {
                    }.getType());
                    this.writeMeta();
                } else if (response.code()== 404){
                    this.finish();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.content_article_not_found_hint), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else {
                    this.finish();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_stream_fail), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                break;
            case "SET_COMMENT":
                if (response.code() == 200) {
                    String s=response.body().string();
                    runOnUiThread(()->{
                        ((TextView)findViewById(R.id.set_comment_content)).setText(""); //清空回复栏
                        ((TextView)findViewById(R.id.set_comment_outside)).setText("");
                        CommentMetaViewHolder.CommentMetaObject o=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<CommentMetaViewHolder.CommentMetaObject>() {}.getType());
                        comment_fragment.items.add(o); //设置回复后会直接返回json
                        comment_fragment.adapter.notifyDataSetChanged();
                        //这里添加一个回复item
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(findViewById(R.id.set_comment_content).getWindowToken(), 0); //收起键盘
                        article.a_comment=String.valueOf(Integer.parseInt(article.a_comment)+1);
                        ((RadioButton)findViewById(R.id.comment)).setText(getString(R.string.comment)+article.a_comment);
                    });
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_comment_success), Toast.LENGTH_SHORT).show();
                }else{
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
                break;
            case "SET_LIKE": //点赞
                if (response.code() == 200) {
                    boolean status= response.body().string().equals("True");
                    runOnUiThread(()->{
                        article.a_prefer=String.valueOf(Integer.parseInt(article.a_prefer)+(status?1:-1)); //增减数值
                        ((TextView)findViewById(R.id.like)).setText(getString(R.string.like)+article.a_prefer);
                    });
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_set_success), Toast.LENGTH_SHORT).show();
                }else{
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_set_fail), Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            default:
                break;
            case "NO_MORE": //突然想到这个其实可以进一步封装到OkHttpPackage里，可惜
                OkHttpPackage.showToastResponse(this,response);
                break;
            case "DELETE":
                this.finish();
                OkHttpPackage.showToastResponse(this,response);
                break;
            case "TOP": //置顶与取消置顶
                if(response.code()==200){article.is_top=!article.is_top;}
                OkHttpPackage.showToastResponse(this,response);
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus(); //获取当前焦点
            if (isHideKeyboard(v, ev)) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (im != null) {
                    im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    findViewById(R.id.keyboard_reply).setVisibility(View.GONE);
                    findViewById(R.id.bottom_1).setVisibility(View.VISIBLE); //切换到外部回复
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }


    public boolean isHideKeyboard(View v, MotionEvent event) { //判断是否需要隐藏键盘
        if (v==null){return false;}
        if (v instanceof EditText) {  //如果按着的不是EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int top = l[1];
            int bottom = top + v.getHeight();
            //左右不算
            if (event.getRawY() > top && event.getRawY() < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略
        return false;
    }

    @Override
    public void onFocusChange(View view,boolean b){
        switch(view.getId()){
            case R.id.set_comment_content: //回复
                if(b){return;}
                findViewById(R.id.keyboard_reply).setVisibility(View.GONE);
                findViewById(R.id.bottom_1).setVisibility(View.VISIBLE); //切换回普通模式
                break;
        }
    }

}
