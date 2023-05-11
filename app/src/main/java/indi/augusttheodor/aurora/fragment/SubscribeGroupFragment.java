package indi.augusttheodor.aurora.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.GroupRectAdapter;
import indi.augusttheodor.aurora.data.GroupRectViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import okhttp3.Call;
import okhttp3.Response;

public class SubscribeGroupFragment extends Fragment implements HttpInterface {

    AdapterItems<GroupRectViewHolder.GroupRectObject> prefer_items;
    AdapterItems<GroupRectViewHolder.GroupRectObject> subscribe_items;
    GroupRectAdapter prefer_adapter;
    GroupRectAdapter subscribe_adapter;
    Activity activity;

    @Override //container 父layout saveInstanceState 当fragment被恢复时存储中断前的数据 inflater 加载layout的id 父layout 在加载期间是否负载到父layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);
        this.prefer_items=new AdapterItems<>();
        this.subscribe_items=new AdapterItems<>();
        RecyclerView recycler_subscribe=(RecyclerView) view.findViewById(R.id.subscribe_group);
        this.subscribe_adapter=new GroupRectAdapter(this.subscribe_items,this);
        recycler_subscribe.setLayoutManager(new GridLayoutManager(getContext(),4)); //格式布局,spanCount代表一行有几个item
        recycler_subscribe.setAdapter(this.subscribe_adapter);
        RecyclerView recycler_prefer=(RecyclerView) view.findViewById(R.id.prefer_group);
        this.prefer_adapter=new GroupRectAdapter(this.prefer_items,this);
        recycler_prefer.setLayoutManager(new GridLayoutManager(getContext(),4));
        recycler_prefer.setAdapter(this.prefer_adapter);
        return view;
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity=getActivity();
    }

    @Override
    public void onResume() {
        this.subscribe_items.clear();
        this.prefer_items.clear();
        this.get_join_group();
        this.get_prefer_group();
        super.onResume();
    }

    private void get_join_group(){ //获取加入的分组
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_user_follow_group),
                "GET",
                null,
                "JOIN_GROUP",
                this,
                this.getContext()
        );
    }

    private void get_prefer_group(){ //获取置顶的分组（这个是在应用内部存储的）
        SharedPreferences pref= getContext().getSharedPreferences(getString(R.string.file_star_group), Context.MODE_PRIVATE);
        int size=pref.getAll().size();
        for(int i=size-1;i>=0;i--){
            AuroraApplication.httpPackage.asyncRequest(
                    getString(R.string.backend_website)+getString(R.string.backend_short_group)+pref.getString(String.valueOf(i),""),
                    "GET",
                    null,
                    "PREFER_GROUP",
                    this,
                    this.getContext()
            );
        }
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "JOIN_GROUP":
                if(response.code()==200){
                    String s=response.body().string();
                    List<GroupRectViewHolder.GroupRectObject> stream=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<GroupRectViewHolder.GroupRectObject>>(){}.getType());
                    for(GroupRectViewHolder.GroupRectObject o :stream){
                        this.subscribe_items.add(o);
                    }
                    activity.runOnUiThread(() -> subscribe_adapter.notifyDataSetChanged());
                }
                break;
            case "PREFER_GROUP":
                if(response.code()==200){
                    String s=response.body().string();
                    GroupRectViewHolder.GroupRectObject ro=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<GroupRectViewHolder.GroupRectObject>(){}.getType());
                    this.prefer_items.add(ro);
                    activity.runOnUiThread(() -> prefer_adapter.notifyDataSetChanged());
                }
                break;
        }

    }
}