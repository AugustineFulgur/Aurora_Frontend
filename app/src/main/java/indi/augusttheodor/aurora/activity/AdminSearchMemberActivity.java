
package indi.augusttheodor.aurora.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
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

public class AdminSearchMemberActivity extends AppCompatActivity implements HttpInterface, RecyclerViewListenerInterface , View.OnClickListener {

    public RecyclerView recycler;
    public MemberAdapter adapter;
    public AdapterItems<MemberViewHolder.MemberObject> items;
    public TextView search_content;
    private String group_id;
    private HashMap<String,String> dict;
    private int p=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_search_member);
        this.recycler=findViewById(R.id.recycle);
        this.search_content=findViewById(R.id.search_content);
        this.items=new AdapterItems<>();
        Intent intent=getIntent();
        this.group_id=intent.getStringExtra("group_id");
        this.adapter=new MemberAdapter(this.items,this,getIntent().getBooleanExtra("is_master",false),MemberAdapter.SEARCH_MEMBER,group_id);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        findViewById(R.id.search).setOnClickListener(this);
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "SEARCH":
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
        }
    }

    @Override
    public void onNext() {
        if(this.dict!=null){
            AuroraApplication.httpPackage.asyncRequest(
                    getString(R.string.backend_website)+getString(R.string.backend_admin_search_member).replace("0",group_id)+p,
                    "GET",
                    dict,
                    "SEARCH",
                    this,
                    this
            );
            p++;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search: //搜索
                this.dict=new HashMap<>();
                this.dict.put("search",this.search_content.getText().toString()); //重置搜索内容
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_admin_search_member).replace("0",group_id)+p,
                        "GET",
                        dict,
                        "SEARCH",
                        this,
                        this
                );
                p++;
        }
    }
}