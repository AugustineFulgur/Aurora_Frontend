package indi.augusttheodor.aurora.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.AdminReportLogAdapter;
import indi.augusttheodor.aurora.data.AdminReportArticleViewHolder;
import indi.augusttheodor.aurora.data.AdminReportLogViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public class AdminReportByTimeActivity extends AppCompatActivity implements RecyclerViewListenerInterface, HttpInterface {
    //说实话这种类也挺有代表性的，要不封装下？
    //主要是我Java学的没那么好，关于父类和子类的认知也就那样，不然我真封装了。

    public RecyclerView recycler;
    public AdminReportLogAdapter adapter;
    public AdapterItems<AdminReportLogViewHolder.AdminReportLogObject> items;
    private String group_id;
    private int p=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_admin_recycler);
        ((TextView)findViewById(R.id.title)).setText(getString(R.string.manage_report_by_time));
        this.recycler=findViewById(R.id.recycler);
        this.items=new AdapterItems<>();
        this.adapter=new AdminReportLogAdapter(this.items,this);
        Intent intent=getIntent();
        this.group_id=intent.getStringExtra("group_id");
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        recycler.setOnScrollListener(new ListenerAssemble.RecyclerViewToBottom(this));
        onNext();
    }

    @Override
    public void onNext() {
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_report_log).replace("0",group_id)+p,
                "GET",
                null,
                "REPORT_LOG",
                this,
                this
        );
        p++;
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "REPORT_LOG":
                if(response.code()==200){
                    String s=response.body().string();
                    List<AdminReportLogViewHolder.AdminReportLogObject> ra=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<AdminReportLogViewHolder.AdminReportLogObject>>(){}.getType());
                    for(AdminReportLogViewHolder.AdminReportLogObject rao:ra){
                        this.items.add(rao);
                    }
                    runOnUiThread(() -> { this.adapter.notifyDataSetChanged(); });
                }else{
                    OkHttpPackage.showToastResponse(this,response);
                }
                break;
        }
    }
}
