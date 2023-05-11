package indi.augusttheodor.aurora.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.SummaryAdapter;
import indi.augusttheodor.aurora.data.SummaryViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import okhttp3.Call;
import okhttp3.Response;

public class SummaryDialogActivity extends AppCompatActivity implements HttpInterface , RecyclerViewListenerInterface , View.OnClickListener{

    private RecyclerView recycler;
    private SummaryAdapter adapter;
    private AdapterItems<SummaryViewHolder.SummaryObject> items;
    public static final int USER_SUMMARY=0;
    public static final int FOLLOWING_SUMMARY=1;
    private int type;
    private int p=0;
    public static boolean fast_mode=false; //快速收藏模式（不用写理由）
    private String summary_ids; //选中的收藏夹的id
    private String article_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_dialog);
        this.recycler=findViewById(R.id.recycle);
        this.items=new AdapterItems<>();
        this.adapter=new SummaryAdapter(this.items,this,this,SummaryAdapter.SMALL);
        this.article_ids=getIntent().getStringExtra("article_id");
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        Intent intent=getIntent();
        this.type=intent.getIntExtra("type",USER_SUMMARY);
        findViewById(R.id.create_summary).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        this.items.clear();
        this.p=0; //重置
        onNext();
        super.onResume();
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        String s=response.body().string();
        if(!s.equals("[]")){
            //不是空的
            List<SummaryViewHolder.SummaryObject> list=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<SummaryViewHolder.SummaryObject>>(){}.getType()); //操作是一样的
            for(SummaryViewHolder.SummaryObject o:list){
                this.items.add(o);
            }
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }else{
            Looper.prepare();
            Toast.makeText(this, getString(R.string.hint_reach_bottom), Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    @Override
    public void onNext() {
        switch (this.type){
            case USER_SUMMARY:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_user_summary_list)+this.p,
                        "GET",
                        null,
                        "USER_SUMMARY_LIST",
                        this,
                        this
                );
                this.p++;
                break;
            case FOLLOWING_SUMMARY:
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_user_summary_list)+this.p,
                        "GET",
                        null,
                        "FOLLOWING_SUMMARY_LIST",
                        this,
                        this
                );
                this.p++;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_summary:
                startActivity(new Intent(this,CreateSummaryActivity.class));
                break;
            case R.id.summary_link:
                //这个是recyclerItem的事件
                this.summary_ids=v.getTag().toString();
                HashMap<String,String> dict=new HashMap<>();
                if(SummaryDialogActivity.fast_mode){ //快速模式直接收藏
                    dict.put("desc","");
                    addArticleToSummary(dict);

                }else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    EditText edit=new EditText(this);
                    edit.setHint(R.string.hint_star_desc);
                    builder.setTitle(R.string.hint_star_desc);
                    builder.setNegativeButton(R.string.chancel,null);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dict.put("desc",edit.getText().toString());
                            addArticleToSummary(dict);
                        }
                    });
                    builder.setView(edit);
                    builder.show();
                }
                break;
        }
    }

    private void addArticleToSummary(HashMap<String,String> dict){
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website) + getString(R.string.backend_add_article_to_summary).replace("{0}", summary_ids).replace("{1}", article_ids),
                "POST",
                dict,
                "ADD_ARTICLE_TO_SUMMARY",
                (call, response, tag) -> {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                    finish(); //关闭页面
                    Looper.loop();
                },
                this
        );
    }

}
