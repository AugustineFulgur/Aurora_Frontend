package indi.augusttheodor.aurora.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.StreamOutSuitAdapter;
import indi.augusttheodor.aurora.data.StreamMetaViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.inter.SearchInter;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import okhttp3.Call;
import okhttp3.Response;

public class SearchArticleFragment extends Fragment implements HttpInterface , RecyclerViewListenerInterface , SearchInter {

    private StreamOutSuitAdapter adapter;
    private AdapterItems<StreamMetaViewHolder.StreamMetaObject> items;
    private RecyclerView recycler;
    private String keyword=""; //关键词，初始为空
    private Activity activity;
    private int p=0;

    public SearchArticleFragment(Activity activity){
        this.items=new AdapterItems<>();
        this.activity=activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_recycler, container, false);
        this.recycler=view.findViewById(R.id.recycle);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this.getContext());
        this.adapter=new StreamOutSuitAdapter(this.items,this.getContext());
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        return view;
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "SEARCH":
                if(response.code()==200){
                    //读取对象
                    String s=response.body().string();
                    List<StreamMetaViewHolder.StreamMetaObject> stream=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<StreamMetaViewHolder.StreamMetaObject>>(){}.getType());
                    if(stream.size()==0){
                        Looper.prepare();
                        Toast.makeText(this.getContext(), getString(R.string.hint_reach_bottom), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        return;
                    }
                    for(StreamMetaViewHolder.StreamMetaObject o :stream){
                        this.items.add(o);
                    }
                    activity.runOnUiThread(() -> adapter.notifyDataSetChanged());

                }
                else{
                    Looper.prepare();
                    Toast.makeText(getContext(), getString(R.string.hint_stream_fail), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                break;
        }
    }

    public void setKeywordNext(String keyword){ //设置关键词并且获取一组数据
        this.keyword=keyword;
        onNext();
    }

    @Override
    public void clear() { this.items.clear(); }

    @Override
    public void onNext() {
        if(keyword.equals("")){return;}
        HashMap<String,String> dict=new HashMap<>();
        dict.put("keyword",this.keyword);
        AuroraApplication.httpPackage.asyncRequest(
                activity.getString(R.string.backend_website)+activity.getString(R.string.backend_search_article)+p,
                "GET",
                dict,
                "SEARCH",
                this,
                activity
        );
        p++;
    }
}
