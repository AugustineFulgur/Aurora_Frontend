package indi.augusttheodor.aurora.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orm.query.Select;

import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.NoticeMessageOutSuitAdapter;
import indi.augusttheodor.aurora.db.DbChatHeader;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;

public class NoticeMessageOutSuitFragment extends Fragment implements RecyclerViewListenerInterface {

    private NoticeMessageOutSuitAdapter adapter;
    private AdapterItems<DbChatHeader> items;
    private RecyclerView recycler;
    private int p=0;
    private List<DbChatHeader> message_header_set;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_recycler, container, false); //完全可以共用
        this.items = new AdapterItems<>();
        this.adapter = new NoticeMessageOutSuitAdapter(this.items, this);
        this.recycler = view.findViewById(R.id.recycle);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getContext());
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.items.clear();
        p=0;
        message_header_set= Select.from(DbChatHeader.class).where("bankId = ?", new String[]{AuroraApplication.userID}).orderBy("timestamp DESC").list(); //获取所有头,按照时间排序
        onNext();
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onNext() {
        for(int i=p*10+0;i<p*10+11;i++){
            if(i<message_header_set.size()){
                this.items.add(message_header_set.get(i));
            }
        }
        p++;
    }
}
