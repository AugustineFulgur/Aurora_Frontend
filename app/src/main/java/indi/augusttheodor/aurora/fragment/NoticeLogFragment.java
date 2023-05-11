package indi.augusttheodor.aurora.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.NoticeLogAdapter;
import indi.augusttheodor.aurora.data.NoticeLogViewHolder;
import indi.augusttheodor.aurora.data.NoticeReplyViewHolder;
import indi.augusttheodor.aurora.db.DbNoticeLog;
import indi.augusttheodor.aurora.db.DbNoticeReply;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.HeartBeat;
import indi.augusttheodor.aurora.tools.ListenerAssemble;

public class NoticeLogFragment extends Fragment implements RecyclerViewListenerInterface {

    private NoticeLogAdapter adapter;
    private AdapterItems<NoticeLogViewHolder.NoticeLogObject> items;
    private RecyclerView recycler;
    private Iterator<DbNoticeLog> log_set;
    private Activity activity;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_recycler, container, false); //完全可以共用
        this.items = new AdapterItems<>();
        this.adapter = new NoticeLogAdapter(this.items, this.getContext());
        this.recycler = view.findViewById(R.id.recycle);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getContext());
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        log_set= Select.from(DbNoticeLog.class).orderBy("timestamp DESC").iterator(); //获取列表
        onNext();
        return view;
    }

    @Override
    public void onNext() {
        for(int i=0;i<10;i++){
            if(log_set.hasNext()){
                this.items.add(AuroraApplication.httpPackage.gson.fromJson(log_set.next().log,new TypeToken<NoticeLogViewHolder.NoticeLogObject>(){}.getType()));
            }else{
                break;
            }
        }
        activity.runOnUiThread(()->{adapter.notifyDataSetChanged();});
    }
}
