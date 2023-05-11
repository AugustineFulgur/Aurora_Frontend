package indi.augusttheodor.aurora.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.SummaryAdapter;
import indi.augusttheodor.aurora.data.SummaryViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import okhttp3.Call;
import okhttp3.Response;

public class UserCreateSummaryActivity extends AppCompatActivity implements HttpInterface , RecyclerViewListenerInterface ,View.OnClickListener {

    private RecyclerView recycler;
    private SummaryAdapter adapter;
    private AdapterItems<SummaryViewHolder.SummaryObject> items;
    private int p=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_list);
        this.recycler=findViewById(R.id.recycle);
        this.items=new AdapterItems<>();
        this.adapter=new SummaryAdapter(this.items,this,ListenerAssemble.jumpToSummary,SummaryAdapter.LARGE);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        findViewById(R.id.create_summary).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        this.items.clear();
        this.p=0; //重置
        onNext();
        super.onResume();
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        String s=response.body().string();
        if(!s.equals("[]")){
            //不是空的
            List<SummaryViewHolder.SummaryObject> list=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<SummaryViewHolder.SummaryObject>>(){}.getType()); //操作是一样的
            for(SummaryViewHolder.SummaryObject o:list){
                this.items.add(o);
            }
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }else{
            Looper.prepare();
            Toast.makeText(this, getString(R.string.hint_reach_bottom), Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    @Override
    public void onNext() {
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_user_summary_list)+this.p,
                "GET",
                null,
                "CREATED_SUMMARY",
                this,
                this
        );
        this.p++;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_summary:
                startActivity(new Intent(this, CreateSummaryActivity.class));
                break;
        }
    }

}
