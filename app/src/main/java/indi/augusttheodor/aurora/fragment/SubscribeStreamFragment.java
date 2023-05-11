package indi.augusttheodor.aurora.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.StreamOutSuitAdapter;
import indi.augusttheodor.aurora.data.StreamMetaViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.ScrollToTop;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import okhttp3.Call;
import okhttp3.Response;

public class SubscribeStreamFragment extends Fragment implements HttpInterface , SwipeRefreshLayout.OnRefreshListener, RecyclerViewListenerInterface , ScrollToTop {
    //随机推送帖子的页面

    private StreamOutSuitAdapter adapter;
    private AdapterItems<StreamMetaViewHolder.StreamMetaObject> items;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipe;
    private int p=0;
    private Activity activity;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity=getActivity();
    }

    @SuppressLint("ResourceAsColor")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_swipe_recycler, container, false);
        this.recycler=view.findViewById(R.id.recycle);
        this.items=new AdapterItems<>();
        this.adapter=new StreamOutSuitAdapter(this.items,this.getContext());
        this.swipe=view.findViewById(R.id.swipe);
        this.swipe.setColorSchemeColors(R.color.aurora_theme);
        this.swipe.setOnRefreshListener(this);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this.getContext());
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        onRefresh();
        return view;
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        if(response.code()==200){
            //读取对象
            String s=response.body().string();
            List<StreamMetaViewHolder.StreamMetaObject> stream=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<StreamMetaViewHolder.StreamMetaObject>>(){}.getType());
            for(StreamMetaViewHolder.StreamMetaObject o :stream){
                this.items.add(o);
            }
            activity.runOnUiThread(() -> adapter.notifyDataSetChanged());

        }
        else{
            Looper.prepare();
            Toast.makeText(activity.getApplicationContext(), getString(R.string.hint_stream_fail), Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        if(tag.equals("REFRESH")){activity.runOnUiThread(()->this.swipe.setRefreshing(false));}
    }

    @Override
    public void onRefresh() {
        this.items.clear(); //清空
        this.p=0;
        //刷新，获取新一组数据
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_user_subscribe)+p,
                "GET",
                null,
                "REFRESH",
                this,
                this.getContext()
        );
        this.p++;
    }

    @Override
    public void onNext() {
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_user_subscribe)+p,
                "GET",
                null,
                "NEXT",
                this,
                getContext()
        );
        this.p++;
    }

    @Override
    public void scroll() {
        this.recycler.smoothScrollToPosition(0);
    }

}
