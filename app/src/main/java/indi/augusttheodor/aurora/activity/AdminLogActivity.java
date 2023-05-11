package indi.augusttheodor.aurora.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.AdminLogAdapter;
import indi.augusttheodor.aurora.data.AdminLogViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public class AdminLogActivity extends AppCompatActivity implements RecyclerViewListenerInterface , HttpInterface {

    public RecyclerView recycler;
    public AdminLogAdapter adapter;
    public AdapterItems<AdminLogViewHolder.AdminLogObject> items;
    private String group_id;
    private int p=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_admin_recycler);
        ((TextView)findViewById(R.id.title)).setText(R.string.manage_log);
        this.recycler=findViewById(R.id.recycler);
        this.items=new AdapterItems<>();
        Intent intent=getIntent();
        this.group_id=intent.getStringExtra("group_id");
        this.adapter=new AdminLogAdapter(this.items,this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        onNext();
    }

    @Override
    public void onNext() {
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_admin_log).replace("0",group_id)+p,
                "GET",
                null,
                "ADMIN_LOG",
                this,
                this
        );
        p++;
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "ADMIN_LOG":
                if(response.code()==200){
                    String s=response.body().string();
                    List<AdminLogViewHolder.AdminLogObject> nl=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<AdminLogViewHolder.AdminLogObject>>(){}.getType());
                    for(AdminLogViewHolder.AdminLogObject nlo:nl){
                        this.items.add(nlo);
                    }
                    runOnUiThread(() -> { this.adapter.notifyDataSetChanged(); });
                }else{
                    OkHttpPackage.showToastResponse(this,response);
                }
                break;
        }
    }
}
