package indi.augusttheodor.aurora.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.util.InnerToaster;

import java.io.IOException;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.GroupArticleAdapter;
import indi.augusttheodor.aurora.data.GroupArticleViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.inter.ScrollToTop;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import okhttp3.Call;
import okhttp3.Response;

public class GroupArticleFragment extends Fragment implements HttpInterface , RecyclerViewListenerInterface , SwipeRefreshLayout.OnRefreshListener , ScrollToTop ,RadioGroup.OnCheckedChangeListener {

    public final static int EXPLORE_NEW=1;
    public final static int EXPLORE_HOT=2;

    private int type=EXPLORE_NEW; //type

    private GroupArticleAdapter adapter;
    private AdapterItems<GroupArticleViewHolder.GroupArticleObject> hot_items;
    private AdapterItems<GroupArticleViewHolder.GroupArticleObject> new_items;
    private RadioGroup explore_type;
    private String group_id;
    private String title; //所属的分类的关键词
    private int hot_page=0;
    private int new_page=0;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipe;
    private Activity activity;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity=getActivity();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_article, container, false);
        this.recycler=view.findViewById(R.id.recycler);
        this.swipe=view.findViewById(R.id.swipe);
        this.swipe.setColorSchemeColors(R.color.aurora_theme);
        this.swipe.setOnRefreshListener(this);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this.getContext());
        this.new_items=new AdapterItems<>();
        this.hot_items=new AdapterItems<>();
        this.adapter=new GroupArticleAdapter(this.new_items,this.getContext());
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        this.explore_type=view.findViewById(R.id.explore_type);
        this.explore_type.setOnCheckedChangeListener(this);
        ((RadioButton)view.findViewById(R.id.fresh)).setChecked(true);
        return view;
    }

    public GroupArticleFragment(String group_id,String title){
        this.group_id=group_id;
        this.title=title;
    }

    public GroupArticleFragment(){
        this.group_id=null;
        this.title=null;
    }

    public void onNext(){
        //刷新，获取新一组数据
        switch (this.type){
            case EXPLORE_NEW:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_group)+this.group_id+"/"+getString(R.string.backend_fresh)+this.new_page,
                        "GET",
                        null,
                        "REFRESH_NEW",
                        this,
                        this.getContext()
                );
                this.new_page++;
                break;
            case EXPLORE_HOT:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_group_hot_article).replace("{0}",group_id)+this.hot_page,
                        "GET",
                        null,
                        "REFRESH_HOT",
                        this,
                        this.getContext()
                );
                this.hot_page++;
                break;
        }
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        AdapterItems<GroupArticleViewHolder.GroupArticleObject> item=null;
        switch(tag){
            case "REFRESH_NEW":
                item=this.new_items;
                break;
            case "REFRESH_HOT":
                item=this.hot_items;
                break;
        }
        if(response.code()==200){
            //读取对象
            String s=response.body().string();
            List<GroupArticleViewHolder.GroupArticleObject> stream=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<GroupArticleViewHolder.GroupArticleObject>>(){}.getType());
            for(GroupArticleViewHolder.GroupArticleObject o :stream){
                item.add(o);

            }
            adapter.changeItem(item);
            activity.runOnUiThread(() -> adapter.notifyDataSetChanged());

        }
        else{
            Looper.prepare();
            Toast.makeText(activity.getApplicationContext(), getString(R.string.hint_stream_fail), Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    @Override
    public void onRefresh() {
        AdapterItems<GroupArticleViewHolder.GroupArticleObject> item=null;
        String uri=null;
        switch (this.type){
            case EXPLORE_HOT:
                item=hot_items;
                this.hot_page=1;
                uri=getString(R.string.backend_group_hot_article);
                break;
            default: //EXPLORE_NEW
                item=new_items;
                this.new_page=1;
                uri=getString(R.string.backend_group_fresh_article);
                break;
        }
        item.clear(); //清空然后重新取
        AdapterItems<GroupArticleViewHolder.GroupArticleObject> finalItem = item;
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website) + uri.replace("{0}",group_id) + 0,
                "GET",
                null,
                null,
                (call, response, tag) -> {
                    if(response.code()==200){
                        //读取对象
                        String s=response.body().string();
                        List<GroupArticleViewHolder.GroupArticleObject> stream=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<GroupArticleViewHolder.GroupArticleObject>>(){}.getType());
                        for(GroupArticleViewHolder.GroupArticleObject o :stream){
                            finalItem.add(o);
                        }
                        adapter.changeItem(finalItem);
                        activity.runOnUiThread(() -> adapter.notifyDataSetChanged());

                    }
                    else{
                        Looper.prepare();
                        Toast.makeText(activity.getApplicationContext(), getString(R.string.hint_stream_fail), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    activity.runOnUiThread(()->this.swipe.setRefreshing(false));
                },
                this.getContext()
        );
    }

    @Override
    public void scroll() {
        this.recycler.smoothScrollToPosition(0);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.hot:
                this.type=EXPLORE_HOT;
                break;
            case R.id.fresh:
                this.type=EXPLORE_NEW;
                break;
        }
        AdapterItems<GroupArticleViewHolder.GroupArticleObject> o=new AdapterItems<>();
        adapter.changeItem(o);
        activity.runOnUiThread(() -> adapter.notifyDataSetChanged()); //清空一下
        onRefresh();
    }
}
