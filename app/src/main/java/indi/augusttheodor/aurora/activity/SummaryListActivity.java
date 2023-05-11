package indi.augusttheodor.aurora.activity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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

public class SummaryListActivity extends AppCompatActivity implements RecyclerViewListenerInterface , HttpInterface {

    private RecyclerView recycler;
    private SummaryAdapter adapter;
    private AdapterItems<SummaryViewHolder.SummaryObject> items;
    public String link; //获取列表的链接（因为有用户关注的列表、用户关注的公开列表两种进入方式）
    private int p=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_recycler);
        this.recycler=findViewById(R.id.recycle);
        this.items=new AdapterItems<>();
        this.adapter=new SummaryAdapter(this.items,this,ListenerAssemble.jumpToSummary,SummaryAdapter.LARGE);
        this.link=getIntent().getStringExtra("link");
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
    }

    @Override
    protected void onResume() { //刷新
        super.onResume();
        this.items.clear();
        onNext();
    }

    @Override
    public void onNext() {
        String id=getIntent().getStringExtra("user_id");
        String url=id==null?getString(R.string.backend_website)+this.link+p:getString(R.string.backend_website)+this.link.replace("{0}",id)+p;
        AuroraApplication.httpPackage.asyncRequest(
                url,
                "GET",
                null,
                "FOLLOWING_SUMMARY",
                this,
                this
        );
        p++;
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        String s=response.body().string();
        Log.e("s",s);
        if(!s.equals("[]") && response.code()==200){
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
    
}
