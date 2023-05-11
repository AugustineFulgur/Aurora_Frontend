package indi.augusttheodor.aurora.activity;

import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.StreamOutSuitAdapter;
import indi.augusttheodor.aurora.data.StreamMetaViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import okhttp3.Call;
import okhttp3.Response;

public class UserHistoryArticleActivity extends AppCompatActivity implements RecyclerViewListenerInterface , HttpInterface {
    //简单的开摆一下（is 地）

    private RecyclerView recycler;
    private int p=0;
    private StreamOutSuitAdapter adapter;
    private AdapterItems<StreamMetaViewHolder.StreamMetaObject> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_recycler); //这里设置容器视图为R中的activity_main layout文件
        this.recycler=findViewById(R.id.recycle);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        this.items=new AdapterItems<>();
        this.adapter=new StreamOutSuitAdapter(this.items,this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        onNext();
    }

    @Override
    public void onNext() {
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_user_history_article)+p,
                "GET",
                null,
                "HISTORY_ARTICLE",
                this,
                this
        );
        p++;
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "HISTORY_ARTICLE":
                if(response.code()==200){
                    //读取对象
                    String s=response.body().string();
                    List<StreamMetaViewHolder.StreamMetaObject> stream=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<StreamMetaViewHolder.StreamMetaObject>>(){}.getType());
                    if(stream.size()==0){
                        Looper.prepare();
                        Toast.makeText(this, getString(R.string.hint_reach_bottom), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        return;
                    }
                    for(StreamMetaViewHolder.StreamMetaObject o :stream){
                        this.items.add(o);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
                else{
                    Looper.prepare();
                    Toast.makeText(this, getString(R.string.hint_stream_fail), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                break;
        }
    }
}
