package indi.augusttheodor.aurora.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.TopArticleAdapter;
import indi.augusttheodor.aurora.data.GroupMetaObject;
import indi.augusttheodor.aurora.data.TopArticleViewHolder;
import indi.augusttheodor.aurora.fragment.GroupArticleFragment;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.ScrollToTop;
import indi.augusttheodor.aurora.tools.AuroraTextClickableSpan;
import indi.augusttheodor.aurora.tools.HeartBeat;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public class GroupActivity extends AppCompatActivity implements HttpInterface ,RadioGroup.OnCheckedChangeListener, View.OnClickListener , PopupMenu.OnMenuItemClickListener {

    private String group_id;
    private String group_name;
    private GroupMetaObject group_meta;
    private ViewPager2 view_pager;
    TopArticleAdapter top_adapter;
    GroupLayoutAdapter layout_adapter;
    AdapterItems<TopArticleViewHolder.TopArticleObject> top_items;
    RecyclerView top_recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group); //这里设置容器视图为R中的activity_main layout文件
        if(savedInstanceState!=null){
            //如果另外存了数据就取数据，否则取传值
            this.group_id=savedInstanceState.getString("group_id");
        }else{
            //获取传值
            Intent intent=getIntent();
            this.group_id=intent.getStringExtra("group_id");
        }
        findViewById(R.id.follow_btn).setOnClickListener(this);
        findViewById(R.id.set_article).setOnClickListener(this);
        findViewById(R.id.option).setOnClickListener(this);
        findViewById(R.id.notice).setOnClickListener(HeartBeat.v);
        findViewById(R.id.new_notice).setOnClickListener(HeartBeat.v);
        findViewById(R.id.top).setOnClickListener(this);
        this.view_pager=findViewById(R.id.viewpager2);
        this.layout_adapter=new GroupLayoutAdapter(getSupportFragmentManager(),getLifecycle());
        this.view_pager.setAdapter(this.layout_adapter); //初始化Adapter
        this.view_pager.setUserInputEnabled(false); //禁止滑动
        this.view_pager.setCurrentItem(0);
        this.top_items=new AdapterItems<>();
        this.top_adapter=new TopArticleAdapter(this.top_items,this);
        this.top_recycler=findViewById(R.id.recycler_top);
        this.top_recycler.setLayoutManager(new LinearLayoutManager(this));
        this.top_recycler.setAdapter(this.top_adapter);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                findViewById(R.id.new_notice).setVisibility(HeartBeat.sum==0? View.INVISIBLE:View.VISIBLE);
                ((Button)findViewById(R.id.new_notice)).setText(String.valueOf(HeartBeat.sum));
            }
        },new IntentFilter("HEARTBEAT"));
    }

    @Override
    public void onResume() {
        super.onResume();
        this.top_items.clear();
        if (group_id != null) {
            AuroraApplication.httpPackage.asyncRequest(
                    getString(R.string.backend_website) + getString(R.string.backend_group) + this.group_id,
                    "GET",
                    null,
                    "GROUP_MAIN",
                    this,
                    this
            ); //获取分组主页内容
            AuroraApplication.httpPackage.asyncRequest(
                    getString(R.string.backend_website)+getString(R.string.backend_group_top_articles).replace("{0}",group_id),
                    "GET",
                    null,
                    "TOP_ARTICLES",
                    this,
                    this
            ); //获取置顶文章
        }
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "TOP_ARTICLES":
                List<TopArticleViewHolder.TopArticleObject> li=AuroraApplication.httpPackage.gson.fromJson(response.body().string(),new TypeToken<List<TopArticleViewHolder.TopArticleObject>>(){}.getType());
                for(TopArticleViewHolder.TopArticleObject i:li){
                    this.top_items.add(i);
                }
                runOnUiThread(()->{
                    top_adapter.notifyDataSetChanged();
                });
                break;
            case "GROUP_MAIN":
                if (response.code() == 200) {
                    //读取对象
                    String s = response.body().string();
                    this.group_meta = AuroraApplication.httpPackage.gson.fromJson(s, new TypeToken<GroupMetaObject>() {}.getType());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+AuroraApplication.shiftString(group_meta.group_pic),(ImageView)findViewById(R.id.group_pic),AuroraApplication.r_long_rect_opt); //加载图片
                            ((TextView)findViewById(R.id.group_name)).setText(group_meta.group_name);
                            group_name=group_meta.group_name;
                            ((TextView)findViewById(R.id.group_member)).setText(group_meta.group_member+" "+group_meta.group_member_name);
                            ((TextView)findViewById(R.id.group_introduction)).setText(group_meta.group_introduction);
                            ((LinearLayout)findViewById(R.id.top_background)).setBackgroundColor(Color.parseColor(group_meta.group_color)); //设置背景颜色
                            ((LinearLayout)findViewById(R.id.background)).setBackgroundColor(Color.parseColor(group_meta.group_color)); //设置背景颜色
                            /*String[] li=group_meta.group_typelist.split(" "); //分割类别列表
                            for(String i:li){ //动态创建分类

                            }*/
                            if(group_meta.is_follow==true){ //关注与不关注的处理
                                ((Button)findViewById(R.id.follow_btn)).setEnabled(false);
                                ((Button)findViewById(R.id.follow_btn)).setText(getString(R.string.follow_t));
                            }else{
                                ((Button)findViewById(R.id.follow_btn)).setText(getString(R.string.follow_f));
                            }
                        }
                    });
                } else {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_stream_fail), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                break;
            case "FOLLOW_SIGN":
                new Handler(Looper.getMainLooper()).post(()->{
                    ((Button)findViewById(R.id.follow_btn)).setText(group_meta.is_follow?getString(R.string.follow_f):getString(R.string.follow_t));
                    findViewById(R.id.follow_btn).setEnabled(group_meta.is_follow);
                    group_meta.is_follow=!group_meta.is_follow;
                    group_meta.group_member=String.valueOf(Integer.parseInt(group_meta.group_member)+(group_meta.is_follow?1:-1));
                    ((TextView)findViewById(R.id.group_member)).setText(group_meta.group_member+" "+group_meta.group_member_name);
                });
                OkHttpPackage.showToastResponse(this,response);
                break;
            case "FOLLOW":
                OkHttpPackage.showToastResponse(this,response);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkId){ //当导航栏图标被点击设定页面转换
        this.view_pager.setCurrentItem(1,false); //平滑切换
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.follow_btn: //关注
                if(group_meta.group_join_type.equals("1") || group_meta.group_join_type.equals("2")){
                    AuroraApplication.httpPackage.asyncRequest(
                            getString(R.string.backend_website)+getString(R.string.backend_group_follow).replace("0",group_id),
                            "GET",
                            null,
                            "FOLLOW_SIGN",
                            this,
                            this
                    );
                }else if(group_meta.group_join_type.equals("3") || group_meta.group_join_type.equals("4")){
                    //申请加入或回答问题
                    EditText edit=new EditText(this);
                    edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    HashMap<String,String> dict=new HashMap<>();
                    builder.setTitle(R.string.content_apply_hint);
                    edit.setWidth(300);
                    edit.setHeight(200);
                    builder.setNegativeButton(R.string.chancel,null);
                    builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                        dict.put("content",edit.getText().toString());
                        AuroraApplication.httpPackage.asyncRequest(
                                getString(R.string.backend_website)+getString(R.string.backend_group_follow).replace("0",group_id),
                                "POST",
                                dict,
                                "FOLLOW",
                                this,
                                this
                        );
                    });
                    builder.setView(edit);
                    builder.show();
                }
                break;
            case R.id.set_article:
                if(!group_meta.is_follow){
                    Toast.makeText(this, getString(R.string.hint_not_in_group), Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent jump=new Intent(view.getContext().getApplicationContext(), CreateArticleActivity.class);
                jump.putExtra("group_id",group_id);
                jump.putExtra("group_name",group_name);
                view.getContext().startActivity((jump));//跳转到转发页面
                break;
            case R.id.option: //其他选项的菜单
                PopupMenu menu=new PopupMenu(view.getContext(),findViewById(R.id.option));
                menu.getMenuInflater().inflate(R.menu.group_option_menu,menu.getMenu());
                refreshMenu(menu);
                menu.setOnMenuItemClickListener(this);
                menu.show();
                break;
            case R.id.notice: //右上角那个信封按钮
                view.getContext().startActivity(new Intent(view.getContext().getApplicationContext(), NoticeActivity.class));
                break;
            case R.id.top:
                ((ScrollToTop)this.layout_adapter.fragment_list.get(this.view_pager.getCurrentItem())).scroll();
                break;
        }
    }

    private void refreshMenu(PopupMenu menu){
        menu.getMenu().findItem(R.id.manage).setVisible(group_meta.is_admin);
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent jump;
        switch (item.getItemId()){
            case R.id.star_group:
                SharedPreferences pref=getSharedPreferences(getString(R.string.file_star_group),MODE_APPEND+MODE_PRIVATE);
                if(pref.getAll().values().remove(group_id)){
                    //有移除，说明已经加入了
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_already_star), Toast.LENGTH_SHORT).show();
                    return false;
                }
                SharedPreferences.Editor editor=pref.edit();
                editor.putString(String.valueOf(pref.getAll().size()),group_id);
                editor.apply();//加入收藏列表
                Toast.makeText(getApplicationContext(), getString(R.string.hint_set_success), Toast.LENGTH_SHORT).show();
                break;
            case R.id.sign_out:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_group_follow).replace("0",group_id),
                        "GET",
                        null,
                        "FOLLOW_SIGN",
                        this,
                        this
                );
                break;
            case R.id.share_inside: //将id粘贴到剪贴板
                ClipboardManager cm=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("AURORA_LINK",getString(R.string.sign_link).replace("{0}", AuroraTextClickableSpan.group).replace("{1}",group_name).replace("{2}",group_id)));
                Toast.makeText(getApplicationContext(), getString(R.string.hint_clipboard_success), Toast.LENGTH_SHORT).show();
                break;
            case R.id.report: //跳转到投诉页面
                jump=new Intent(this,ReportDialogActivity.class);
                jump.putExtra("sth_id",group_id);
                jump.putExtra("type",AuroraApplication.Constants.Group);
                startActivity(jump);
                break;
            case R.id.manage:
                jump=new Intent(this,AdminGroupActivity.class);
                jump.putExtra("group_name",group_name);
                jump.putExtra("group_id",group_id);
                jump.putExtra("group_pic",group_meta.group_pic);
                startActivity(jump);
                break;
            case R.id.detail:
                jump=new Intent(this,GroupDetailActivity.class);
                jump.putExtra("group_name",group_name);
                jump.putExtra("group_id",group_id);
                jump.putExtra("group_pic",group_meta.group_pic);
                jump.putExtra("group_introduction",group_meta.group_introduction);
                jump.putExtra("group_color",group_meta.group_color);
                jump.putExtra("group_member",group_meta.group_member+" "+group_meta.group_member_name);
                startActivity(jump);
                break;
        }
        return false;
    }

    private class GroupLayoutAdapter extends FragmentStateAdapter{

        private ArrayList<Fragment> fragment_list=new ArrayList<Fragment>();

        public GroupLayoutAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
            this.fragment_list.add(new GroupArticleFragment(group_id,null));
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) { //按照position创建fragment，我在想可以用字典
            return fragment_list.get(position);
        }

        @Override
        public int getItemCount() {
            return fragment_list.size();
        }
    }

}