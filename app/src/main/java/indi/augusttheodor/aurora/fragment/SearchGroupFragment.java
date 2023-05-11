package indi.augusttheodor.aurora.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.GroupListAdapter;
import indi.augusttheodor.aurora.data.GroupListViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.inter.SearchInter;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import okhttp3.Call;
import okhttp3.Response;

public class SearchGroupFragment extends Fragment implements HttpInterface , RecyclerViewListenerInterface , SearchInter {

    private RecyclerView recycler;
    private String keyword=""; //关键词，初始为空
    GroupListAdapter adapter;
    AdapterItems<GroupListViewHolder.GroupListObject> items;
    private Activity activity;
    private int p=0;

    public SearchGroupFragment(Activity activity){
        this.items=new AdapterItems<>();
        this.activity=activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_recycler, container, false);
        this.adapter=new GroupListAdapter(this.items,this.getContext());
        this.recycler=view.findViewById(R.id.recycle);
        this.recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        return view;
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "SEARCH":
                List<GroupListViewHolder.GroupListObject> rank=AuroraApplication.httpPackage.gson.fromJson(response.body().string(),new TypeToken<List<GroupListViewHolder.GroupListObject>>(){}.getType());
                for(GroupListViewHolder.GroupListObject i:rank){
                    this.items.add(i);
                }
                activity.runOnUiThread(()->{
                    adapter.notifyDataSetChanged();
                });
                break;
        }
    }

    @Override
    public void setKeywordNext(String keyword) {
        this.keyword=keyword;
        onNext();
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    @Override
    public void onNext() {
        if(keyword.equals("")){return;}
        HashMap<String,String> dict=new HashMap<>();
        dict.put("keyword",this.keyword);
        AuroraApplication.httpPackage.asyncRequest(
                activity.getString(R.string.backend_website)+activity.getString(R.string.backend_search_group)+p,
                "GET",
                dict,
                "SEARCH",
                this,
                activity
        );
        p++;
    }
}
