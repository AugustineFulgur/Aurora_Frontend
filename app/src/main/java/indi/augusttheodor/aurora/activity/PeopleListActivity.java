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
import indi.augusttheodor.aurora.adapter.MemberAdapter;
import indi.augusttheodor.aurora.data.MemberViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public class PeopleListActivity extends AppCompatActivity implements RecyclerViewListenerInterface, HttpInterface {

    public static final int FOLLOWING=1;
    public static final int FOLLOWED=2; //类型

    public RecyclerView recycler;
    public MemberAdapter adapter;
    public AdapterItems<MemberViewHolder.MemberObject> items;
    private int type;
    private int p=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_recycler); //这里设置容器视图为R中的activity_main layout文件
        this.type=getIntent().getIntExtra("TYPE",FOLLOWING);
        this.recycler=findViewById(R.id.recycle);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        this.items=new AdapterItems<>();
        this.adapter=new MemberAdapter(this.items,this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        onNext();
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        if(response.code()==200){
            String s=response.body().string();
            List<MemberViewHolder.MemberObject> ra= AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<MemberViewHolder.MemberObject>>(){}.getType());
            for(MemberViewHolder.MemberObject rao:ra){
                this.items.add(rao);
            }
            runOnUiThread(() -> { this.adapter.notifyDataSetChanged(); });
        }else{
            OkHttpPackage.showToastResponse(this,response);
        }
    }

    @Override
    public void onNext() {
        String s;
        switch (type){
            case FOLLOWING:
                s=getString(R.string.backend_user_following_people);
                break;
            case FOLLOWED:
                s=getString(R.string.backend_user_followed_people);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+s+p,
                "GET",
                null,
                null,
                this,
                this
        );
        p++;
    }
}
