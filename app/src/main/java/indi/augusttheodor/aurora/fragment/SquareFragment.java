package indi.augusttheodor.aurora.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.SearchActivity;
import indi.augusttheodor.aurora.adapter.GroupListAdapter;
import indi.augusttheodor.aurora.adapter.TopArticleAdapter;
import indi.augusttheodor.aurora.data.GroupListViewHolder;
import indi.augusttheodor.aurora.data.TopArticleViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public class SquareFragment extends Fragment implements HttpInterface , View.OnClickListener {
    GroupListAdapter group_adapter;
    AdapterItems<GroupListViewHolder.GroupListObject> group_items;
    RecyclerView group_recycler;
    TopArticleAdapter top_adapter;
    AdapterItems<TopArticleViewHolder.TopArticleObject> top_items;
    RecyclerView top_recycler;
    TextView search;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square, container, false);
        this.group_items=new AdapterItems<>();
        this.group_adapter=new GroupListAdapter(this.group_items,this.getContext());
        this.group_recycler=view.findViewById(R.id.recycler_group);
        this.top_items=new AdapterItems<>();
        this.top_adapter=new TopArticleAdapter(this.top_items,this.getContext());
        this.top_recycler=view.findViewById(R.id.recycler_top);
        this.group_recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.group_recycler.setAdapter(this.group_adapter);
        this.top_recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.top_recycler.setAdapter(this.top_adapter);
        this.search=view.findViewById(R.id.search);
        view.findViewById(R.id.search_btn).setOnClickListener(this);
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_top_articles),
                "GET",
                null,
                "TOP_ARTICLES",
                this,
                this.getContext()
        );
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_group_rank),
                "GET",
                null,
                "GROUP_RANK",
                this,
                this.getContext()
        );
        return view;
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity=getActivity();
    }


    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        if(response.code()==200){
            switch (tag){
                case "TOP_ARTICLES":
                    List<TopArticleViewHolder.TopArticleObject> li=AuroraApplication.httpPackage.gson.fromJson(response.body().string(),new TypeToken<List<TopArticleViewHolder.TopArticleObject>>(){}.getType());
                    for(TopArticleViewHolder.TopArticleObject i:li){
                        this.top_items.add(i);
                    }
                    this.activity.runOnUiThread(()->{
                        top_adapter.notifyDataSetChanged();
                    });
                    break;
                case "GROUP_RANK":
                    List<GroupListViewHolder.GroupListObject> rank=AuroraApplication.httpPackage.gson.fromJson(response.body().string(),new TypeToken<List<GroupListViewHolder.GroupListObject>>(){}.getType());
                    for(GroupListViewHolder.GroupListObject i:rank){
                        this.group_items.add(i);
                    }
                    this.activity.runOnUiThread(()->{
                        group_adapter.notifyDataSetChanged();
                    });
                    break;
            }
        }else{
            OkHttpPackage.showToastResponse(this.getContext(),response);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_btn:
                Intent intent=new Intent(getContext(), SearchActivity.class);
                intent.putExtra("content",this.search.getText().toString());
                this.activity.startActivity(intent); //跳转到搜索页面
                break;
        }
    }
}
