package indi.augusttheodor.aurora.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.StreamOutSuitAdapter;
import indi.augusttheodor.aurora.adapter.SummaryRectAdapter;
import indi.augusttheodor.aurora.data.StreamMetaViewHolder;
import indi.augusttheodor.aurora.data.SummaryRectViewHolder;
import indi.augusttheodor.aurora.data.UserMetaObject;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import okhttp3.Call;
import okhttp3.Response;

public class UserDetailActivity extends AppCompatActivity implements HttpInterface ,View.OnClickListener , PopupMenu.OnMenuItemClickListener {

    private StreamOutSuitAdapter detail_adapter;
    private SummaryRectAdapter summary_adapter;
    private String user_id;
    private Button follow_btn;
    private boolean is_following;
    private PopupMenu menu; //菜单
    private boolean is_blocking;
    private AdapterItems<SummaryRectViewHolder.SummaryRectObject> summary_items;
    private AdapterItems<StreamMetaViewHolder.StreamMetaObject> items;
    private int p=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        setContentView(R.layout.activity_user_detail);
        this.menu=new PopupMenu(this,findViewById(R.id.options));
        this.menu.getMenuInflater().inflate(R.menu.user_option_menu,this.menu.getMenu());
        this.user_id=intent.getStringExtra("user_id");
        findViewById(R.id.options).setOnClickListener(this);
        this.items=new AdapterItems<>();
        RecyclerView recycler=(RecyclerView) findViewById(R.id.user_trend_container);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);
        this.detail_adapter=new StreamOutSuitAdapter(this.items,this);
        recycler.setAdapter(this.detail_adapter);
        //下方动态Recycler的布局
        RecyclerView summary_recycler=findViewById(R.id.summary_rect);
        LinearLayoutManager summary_manager=new LinearLayoutManager(this);
        summary_manager.setOrientation(LinearLayoutManager.HORIZONTAL); //设置横向放置
        this.summary_items=new AdapterItems<>();
        this.summary_adapter=new SummaryRectAdapter(this,this.summary_items);
        summary_recycler.setLayoutManager(summary_manager);
        summary_recycler.setAdapter(this.summary_adapter);
        //上方收藏夹Recycler的布局
        this.follow_btn=(Button)findViewById(R.id.follow_btn);
        this.follow_btn.setOnClickListener(this);
        findViewById(R.id.summary_list).setOnClickListener(v->{
            Intent public_summary=new Intent(this,SummaryListActivity.class);
            public_summary.putExtra("link",getString(R.string.backend_user_public_summary));
            public_summary.putExtra("user_id",user_id);
            startActivity(public_summary);
        });
        AuroraApplication.httpPackage.asyncRequest( //获取基本数据
                getString(R.string.backend_website)+getString(R.string.backend_user)+user_id,
                "GET",
                null,
                "USER_META",
                this,
                this
        );
        AuroraApplication.httpPackage.asyncRequest( //获取前五个
                getString(R.string.backend_website)+getString(R.string.backend_user_public_summary_rect).replace("{0}",user_id),
                "GET",
                null,
                "USER_SUMMARY",
                this,
                this
        );
        refresh_stream(); //先获取一组
    }

    public void refresh_stream(){
        //刷新，获取新一组数据
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_user_history_public).replace("0",user_id)+p,
                "GET",
                null,
                "REFRESH",
                this,
                (AppCompatActivity) this
        );
        p++;
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        if(tag.equals("UNFOLLOW")){
            if(response.code()!=200){
                Looper.prepare();
                Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                Looper.loop();
                return;
            }
            this.is_following=!this.is_following;
            runOnUiThread(()->{
                refreshMenu();
                ((Button)findViewById(R.id.follow_btn)).setEnabled(!is_following);
                if(is_following==true){ //关注与不关注的处理
                    ((Button)findViewById(R.id.follow_btn)).setText(getString(R.string.user_follow_t));
                }else{
                    ((Button)findViewById(R.id.follow_btn)).setText(getString(R.string.user_follow_f));
                }
            });
            return;
        }
        if(response.code()!=200){
            Looper.prepare();
            Toast.makeText(getApplicationContext(), getString(R.string.hint_stream_fail), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        }
        String s=response.body().string();
        switch(tag){
            case "REFRESH":
                //读取对象
                List<StreamMetaViewHolder.StreamMetaObject> stream=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<StreamMetaViewHolder.StreamMetaObject>>(){}.getType());
                for(StreamMetaViewHolder.StreamMetaObject o :stream){
                    this.items.add(o);
                }
                if(stream.size()==0){
                    Looper.prepare();
                    Toast.makeText(this, getString(R.string.hint_reach_bottom), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }
                runOnUiThread(() -> detail_adapter.notifyDataSetChanged());
                break;
            case "USER_META":
                UserMetaObject o=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<UserMetaObject>(){}.getType());
                runOnUiThread(() -> {
                    ((TextView)findViewById(R.id.name)).setText(o.user_name);
                    ((TextView)findViewById(R.id.title_text)).setText(o.user_name+((TextView)findViewById(R.id.title_text)).getText().toString());
                    ((TextView)findViewById(R.id.signature)).setText(o.user_signature);
                    ((TextView)findViewById(R.id.following)).setText(o.user_following);
                    ((TextView)findViewById(R.id.followed)).setText(o.user_followed);
                    ((TextView)findViewById(R.id.article)).setText(o.user_article);
                    ((TextView)findViewById(R.id.group)).setText(o.user_group);
                    ((TextView)findViewById(R.id.summary_sum)).setText(o.summary_sum);
                    is_following=o.is_following;
                    is_blocking=o.is_blocking;
                    follow_btn.setEnabled(!o.is_following);
                    if(o.user_bimg!=null){
                        AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+AuroraApplication.shiftString(o.user_himg),((ImageView)findViewById(R.id.himg)),AuroraApplication.short_memory_opt); //加载图片
                    }
                    if(o.user_himg!=null){
                        AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+AuroraApplication.shiftString(o.user_bimg),((ImageView)findViewById(R.id.bimg)),AuroraApplication.short_memory_opt);
                    }
                    if(is_following){ //关注与不关注的处理
                        ((Button)findViewById(R.id.follow_btn)).setEnabled(false);
                        ((Button)findViewById(R.id.follow_btn)).setText(getString(R.string.user_follow_t));
                    }else{
                        ((Button)findViewById(R.id.follow_btn)).setText(getString(R.string.user_follow_f));
                    }
                });
                break;
            case "USER_SUMMARY":
                List<SummaryRectViewHolder.SummaryRectObject> summary=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<SummaryRectViewHolder.SummaryRectObject>>(){}.getType());
                for(SummaryRectViewHolder.SummaryRectObject so :summary){
                    this.summary_items.add(so);
                }
                runOnUiThread(() -> summary_adapter.notifyDataSetChanged());
                break;
            case "BLOCK":
                this.is_blocking=!this.is_blocking;
                runOnUiThread(()->refreshMenu());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.options:
                runOnUiThread(()->refreshMenu());
                this.menu.setOnMenuItemClickListener(this);
                this.menu.show();
                break;
            case R.id.follow_btn:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_user_follow).replace("0",user_id),
                        "GET",
                        null,
                        "UNFOLLOW",
                        this,
                        this
                );
                break;
            case R.id.block:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_user_block).replace("0",user_id),
                        "GET",
                        null,
                        "BLOCK",
                        this,
                        this
                );
                break;
        }
    }

    private void refreshMenu(){
        //刷新Menu的状态菜单
        this.menu.getMenu().findItem(R.id.unfollow).setVisible(is_following);
        if(is_blocking){ //拉黑了对方，改变文字
            this.menu.getMenu().findItem(R.id.block).setTitle(getString(R.string.unblock));
        }else{
            this.menu.getMenu().findItem(R.id.block).setTitle(getString(R.string.block));
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.send_message:
                Intent jump=new Intent(this, ChatMessageActivity.class);
                jump.putExtra("user_id",user_id);
                jump.putExtra("user_name",((TextView)findViewById(R.id.name)).getText().toString());
                ChatMessageActivity.waitForHeader(jump,this);
                break;
            case R.id.unfollow:
                AuroraApplication.httpPackage.asyncRequest(
                    getString(R.string.backend_website)+getString(R.string.backend_user_follow).replace("0",user_id),
                        "GET",
                        null,
                        "UNFOLLOW",
                        this,
                        this
                );
                break;
            case R.id.block:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_user_block).replace("0",user_id),
                        "GET",
                        null,
                        "BLOCK",
                        this,
                        this
                );
                break;
            case R.id.report:
                Intent report_jump=new Intent(this,ReportDialogActivity.class);
                report_jump.putExtra("sth_id",user_id);
                report_jump.putExtra("type",AuroraApplication.Constants.User);
                startActivity(report_jump);
                break;
        }
        return false;
    }
}