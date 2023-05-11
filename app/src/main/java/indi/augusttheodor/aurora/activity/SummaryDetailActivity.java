package indi.augusttheodor.aurora.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.SummaryArticleAdapter;
import indi.augusttheodor.aurora.data.SummaryArticleViewHolder;
import indi.augusttheodor.aurora.data.SummaryViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public class SummaryDetailActivity extends AppCompatActivity implements HttpInterface , RecyclerViewListenerInterface , View.OnClickListener , PopupMenu.OnMenuItemClickListener {
    //单个收藏夹的详细展示页面
    private int p=0;
    private TextView summary_following; //关注人数
    private TextView summary_dict; //文章数量
    public String summary_id;
    public RecyclerView recycler;
    public SummaryArticleAdapter adapter;
    public AdapterItems<SummaryArticleViewHolder.SummaryArticleObject> items;
    private boolean is_author=false;
    private boolean is_following=false;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        this.summary_dict=findViewById(R.id.summary_dict);
        this.summary_following=findViewById(R.id.summary_following);
        this.recycler=findViewById(R.id.recycler);
        this.items=new AdapterItems<>();
        this.summary_id=getIntent().getStringExtra("summary_id");
        this.adapter=new SummaryArticleAdapter(this.items,this,summary_id,new Handler());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        findViewById(R.id.option).setOnClickListener(this); //菜单
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_summary).replace("0",summary_id),
                "GET",
                null,
                "SUMMARY_META",
                this,
                this
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.p=0;
        this.items.clear();
        onNext();
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "SUMMARY_META":
                if(response.code()==200){
                    SummaryViewHolder.SummaryMetaObject o=AuroraApplication.httpPackage.gson.fromJson(response.body().string(),new TypeToken<SummaryViewHolder.SummaryMetaObject>(){}.getType());
                    runOnUiThread(() -> {
                        ((TextView)findViewById(R.id.summary_name)).setText(o.summary_name);
                        summary_following.setText(getString(R.string.replace_summary_following).replace("0",o.summary_follow));
                        summary_dict.setText(getString(R.string.replace_summary_dict).replace("0",o.summary_dict));
                        AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+AuroraApplication.shiftString(o.summary_pic),(ImageView)findViewById(R.id.summary_pic),AuroraApplication.r_long_rect_opt);
                        ((TextView)findViewById(R.id.summary_desc)).setText(o.summary_desc);
                        ((TextView)findViewById(R.id.summary_create_time)).setText(o.create_time);
                        is_author=o.is_author;
                        is_following=o.is_following;
                    });
                }else{
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_set_fail), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                break;
            case "SUMMARY_ARTICLE":
                if(response.code()==200){
                    List<SummaryArticleViewHolder.SummaryArticleObject> sao=AuroraApplication.httpPackage.gson.fromJson(response.body().string(),new TypeToken<List<SummaryArticleViewHolder.SummaryArticleObject>>(){}.getType());
                    for(SummaryArticleViewHolder.SummaryArticleObject o:sao){
                        this.items.add(o);
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }else{
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_set_fail), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                break;
        }
    }

    @Override
    public void onNext() {
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_summary_articles).replace("0",this.summary_id)+this.p,
                "GET",
                null,
                "SUMMARY_ARTICLE",
                this,
                this
        );
        this.p++;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.option: //呼出菜单
                PopupMenu menu=new PopupMenu(v.getContext(),findViewById(R.id.option));
                menu.getMenuInflater().inflate(R.menu.summary_option_menu,menu.getMenu());
                refreshMenu(menu);
                menu.setOnMenuItemClickListener(this);
                menu.show();
                break;
        }
    }

    private void refreshMenu(PopupMenu menu){
        menu.getMenu().findItem(R.id.delete).setVisible(is_author);
        menu.getMenu().findItem(R.id.change_name).setVisible(is_author);
        menu.getMenu().findItem(R.id.change_pic).setVisible(is_author);
        menu.getMenu().findItem(R.id.change_desc).setVisible(is_author);
        menu.getMenu().findItem(R.id.follow).setTitle(is_following?R.string.user_follow_t:R.string.user_follow_f);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        EditText edit=new EditText(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        HashMap<String,String> dict=new HashMap<>();
        switch (item.getItemId()){
            case R.id.follow:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website) + getString(R.string.backend_summary_follow).replace("0", summary_id),
                        "GET",
                        null,
                        null,
                        (call, response, tag) -> {
                            OkHttpPackage.showToastResponse(getApplicationContext(),response);
                        },
                        this
                );
                is_following=!is_following;
                break;
            case R.id.change_name:
                builder.setTitle(R.string.change_summary_name);
                builder.setNegativeButton(R.string.chancel,null);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dict.put("name",edit.getText().toString());
                        callModifySummaryData(dict);
                    }
                });
                builder.setView(edit);
                builder.show();
                break;
            case R.id.change_pic:
                AuroraApplication.pickerPic(1);
                startActivityForResult(new Intent(this, ImageGridActivity.class), 100); //选照片
                break;
            case R.id.change_desc:
                builder.setTitle(R.string.change_summary_desc);
                builder.setNegativeButton(R.string.chancel,null);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dict.put("desc",edit.getText().toString());
                        callModifySummaryData(dict);
                    }
                });
                builder.setView(edit);
                builder.show();
                break;
            case R.id.delete: //删除收藏夹
                builder.setTitle(R.string.delete_summary);
                TextView hint=new TextView(this);
                hint.setGravity(View.TEXT_ALIGNMENT_CENTER);
                hint.setText(R.string.hint_are_you_sure);
                builder.setNegativeButton(R.string.chancel,null);
                builder.setPositiveButton(R.string.ok, (dialog, which) -> AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website) + getString(R.string.backend_delete_summary).replace("0", summary_id),
                        "GET",
                        null,
                        null,
                        (call, response, tag) -> {
                            this.finish(); //删除之后返回
                            OkHttpPackage.showToastResponse(getApplicationContext(),response);
                        },
                        this
                ));
                builder.setView(hint);
                builder.show();
                break;
            case R.id.report:
                Intent jump=new Intent(this,ReportDialogActivity.class);
                jump.putExtra("sth_id",summary_id);
                jump.putExtra("type",AuroraApplication.Constants.Summary);
                startActivity(jump);
                break;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //选择头像和背景的回调
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) { //上传回调，插入图片和上传（不整这些虚的了，就直接来吧^ ^）
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            for(ImageItem i:images){
                AuroraApplication.httpPackage.asyncImageRequest(
                        getString(R.string.backend_website) + getString(R.string.backend_upload_image),
                        i,
                        "UPLOAD_IMG",
                        (call, response, tag) -> {
                            HashMap<String,String> dict=new HashMap<>();
                            if(response.code()==200){
                                dict.put("pic",response.body().string());
                                callModifySummaryData(dict);
                            }
                        },
                        this
                );
            }
        }
    }

    private void callModifySummaryData(HashMap<String,String> dict){
        AuroraApplication.httpPackage.asyncRequest(
            getString(R.string.backend_website)+getString(R.string.backend_change_summary_data).replace("0",summary_id),
            "POST",
            dict,
            null,
                (call, response, tag) -> {
                    handler.post(()->this.onResume()); //刷新
                    OkHttpPackage.showToastResponse(getApplicationContext(),response);
                },
            getApplicationContext()
        );
    }
}
