package indi.augusttheodor.aurora.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.MemberAdapter;
import indi.augusttheodor.aurora.data.MemberViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public class GroupDetailActivity extends AppCompatActivity implements HttpInterface {

    public String group_name;
    public String group_pic;
    public String group_id;
    public MemberAdapter adapter;
    public RecyclerView recycler;
    public AdapterItems<MemberViewHolder.MemberObject> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        this.recycler=findViewById(R.id.recycler);
        this.items=new AdapterItems<>();
        this.adapter=new MemberAdapter(items,this);
        Intent intent=getIntent();
        this.group_name=intent.getStringExtra("group_name");
        this.group_pic=intent.getStringExtra("group_pic");
        this.group_id=intent.getStringExtra("group_id");
        ((TextView)findViewById(R.id.group_name)).setText(group_name);
        AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+AuroraApplication.shiftString(group_pic), (ImageView) findViewById(R.id.group_pic),AuroraApplication.short_memory_opt);
        ((TextView)findViewById(R.id.group_introduction)).setText(intent.getStringExtra("group_introduction"));
        findViewById(R.id.background).setBackgroundColor(Color.parseColor(intent.getStringExtra("group_color")));
        ((TextView)findViewById(R.id.group_member)).setText(intent.getStringExtra("group_member"));
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_admin_master).replace("0",group_id),
                "GET",
                null,
                null,
                this,
                this
        );
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        if(response.code()==200){
            String s=response.body().string();
            MemberViewHolder.MemberObject ra=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<MemberViewHolder.MemberObject>(){}.getType());
            runOnUiThread(()->{
                ((TextView) findViewById(R.id.master_name)).setText(ra.member_name);
                AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+AuroraApplication.shiftString(ra.member_nick),(ImageView) findViewById(R.id.master_nick),AuroraApplication.short_memory_opt);
                findViewById(R.id.master_link).setTag(ra.member_id);
                adapter.is_master= false; //仅普通显示
                adapter.notifyDataSetChanged();
            });
        }else{
            OkHttpPackage.showToastResponse(this,response);
        }
    }
}