package indi.augusttheodor.aurora.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.CommentAdapter;
import indi.augusttheodor.aurora.data.CommentMetaViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import okhttp3.Call;
import okhttp3.Response;

public class ArticleShowCommentFragment extends Fragment implements HttpInterface, RecyclerViewListenerInterface {

    public CommentAdapter adapter;
    public AdapterItems<CommentMetaViewHolder.CommentMetaObject> items;
    private RecyclerView recycler;
    private int p=0; //评论页数
    private String article_id; //对应文章的id
    private boolean is_article; //是否文章类型（文章+动态）
    private Activity activity;

    public ArticleShowCommentFragment(String article_id,boolean is_article){
        //参一下
        this.article_id=article_id;
        this.is_article=is_article;
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_show_comment, container, false);
        this.items=new AdapterItems<>();
        this.adapter=new CommentAdapter(this.items,this,is_article,new Handler());
        this.recycler=view.findViewById(R.id.recycle);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this.getContext());
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        onNext();
        return view;
    }

    public void onNext(){ //获取一次评论
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_article)+this.article_id+'/'+getString(R.string.backend_page)+this.p,
                "GET",
                null,
                "ARTICLE_COMMENT",
                this,
                this.getContext()
        );
        this.p+=1;
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "ARTICLE_COMMENT":
                if (response.code() == 200) {
                    //读取对象
                    String s = response.body().string();
                    List<CommentMetaViewHolder.CommentMetaObject> article = AuroraApplication.httpPackage.gson.fromJson(s, new TypeToken<List<CommentMetaViewHolder.CommentMetaObject>>() {}.getType());
                    if(article.size()!=0){ //是↘↗→零↑诶↓^ ^
                        for(CommentMetaViewHolder.CommentMetaObject o :article){
                            this.items.add(o);
                        }
                        activity.runOnUiThread(() -> adapter.notifyDataSetChanged());
                    }
                } else {
                    Looper.prepare();
                    Toast.makeText(getContext().getApplicationContext(), getString(R.string.hint_stream_fail), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                break;
        }
    }
}
