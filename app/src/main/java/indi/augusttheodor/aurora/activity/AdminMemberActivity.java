package indi.augusttheodor.aurora.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.MemberAdapter;
import indi.augusttheodor.aurora.data.MemberViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public class AdminMemberActivity extends AppCompatActivity implements HttpInterface , RecyclerViewListenerInterface {

    public RecyclerView recycler;
    public MemberAdapter adapter;
    public AdapterItems<MemberViewHolder.MemberObject> items;
    private String group_id;
    private int p=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_member);
        this.recycler=findViewById(R.id.recycle);
        this.items=new AdapterItems<>();
        Intent intent=getIntent();
        this.group_id=intent.getStringExtra("group_id");
        this.adapter=new MemberAdapter(this.items,this,getIntent().getBooleanExtra("is_master",false),MemberAdapter.ADMIN_MEMBER,group_id);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_admin_master).replace("0",group_id),
                "GET",
                null,
                "ADMIN_MASTER",
                this,
                this
        );
        onNext();
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "ADMIN_MEMBER":
                if(response.code()==200){
                    String s=response.body().string();
                    List<MemberViewHolder.MemberObject> ra=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<MemberViewHolder.MemberObject>>(){}.getType());
                    for(MemberViewHolder.MemberObject rao:ra){
                        this.items.add(rao);
                    }
                    runOnUiThread(() -> { this.adapter.notifyDataSetChanged(); });
                }else{
                    OkHttpPackage.showToastResponse(this,response);
                }
                break;
            case "ADMIN_MASTER":
                if(response.code()==200){
                    String s=response.body().string();
                    MemberViewHolder.MemberObject ra=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<MemberViewHolder.MemberObject>(){}.getType());
                    runOnUiThread(()->{
                        ((TextView) findViewById(R.id.master_name)).setText(ra.member_name);
                        AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+AuroraApplication.shiftString(ra.member_nick),(ImageView) findViewById(R.id.master_nick),AuroraApplication.short_memory_opt);
                        findViewById(R.id.master_link).setTag(ra.member_id);
                    });
                }else{
                    OkHttpPackage.showToastResponse(this,response);
                }
                break;
        }
    }

    @Override
    public void onNext() {
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_admin_member).replace("0",group_id)+p,
                "GET",
                null,
                "ADMIN_MEMBER",
                this,
                this
        );
        p++;
    }
}
