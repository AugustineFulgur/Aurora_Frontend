package indi.augusttheodor.aurora.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.GroupListAdapter;
import indi.augusttheodor.aurora.data.GroupListViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import okhttp3.Call;
import okhttp3.Response;

public class UserGroupActivity extends AppCompatActivity implements RecyclerViewListenerInterface, HttpInterface {

    private RecyclerView recycler;
    private int p=0;
    private GroupListAdapter adapter;
    private AdapterItems<GroupListViewHolder.GroupListObject> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_recycler); //这里设置容器视图为R中的activity_main layout文件
        this.recycler=findViewById(R.id.recycle);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        this.items=new AdapterItems<>();
        this.adapter=new GroupListAdapter(this.items,this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        onNext();
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        if(response.code()==200){
            List<GroupListViewHolder.GroupListObject> rank= AuroraApplication.httpPackage.gson.fromJson(response.body().string(),new TypeToken<List<GroupListViewHolder.GroupListObject>>(){}.getType());
            for(GroupListViewHolder.GroupListObject i:rank){
                this.items.add(i);
            }
            runOnUiThread(()->{
                adapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    public void onNext() {
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_user_detail_follow_group)+p,
                "GET",
                null,
                null,
                this,
                this
        );
        p++;
    }
}
