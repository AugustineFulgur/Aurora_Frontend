package indi.augusttheodor.aurora.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.ReportReasonAdapter;
import indi.augusttheodor.aurora.data.ReportReasonViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public class ReportDialogActivity extends AppCompatActivity implements View.OnClickListener , HttpInterface {

    public AdapterItems<ReportReasonViewHolder.ReportReasonObject> items;
    public RecyclerView recycler;
    public ReportReasonAdapter adapter;
    public String sth_id; //id
    public String type; //类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_dialog);
        Intent intent=getIntent();
        this.sth_id=intent.getStringExtra("sth_id");
        this.type=intent.getStringExtra("type");
        this.recycler=findViewById(R.id.recycle);
        this.items=new AdapterItems<>();
        this.adapter=new ReportReasonAdapter(this.items,this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        findViewById(R.id.submit).setOnClickListener(this);
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_report_reasons).replace("{0}",this.type).replace("{1}",this.sth_id),
                "GET",
                null,
                "REPORT_REASONS",
                this,
                this
        );
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                HashMap<String,String> dict=new HashMap<>();
                dict.put("reason_id", String.valueOf(this.adapter.report_id));
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_report_submit).replace("{0}",this.type).replace("{1}",this.sth_id),
                        "POST",
                        dict,
                        "REPORT_SUBMIT",
                        this,
                        this
                );
                break;
        }
    }



    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "REPORT_REASONS":
                if(response.code()==200){
                    String s=response.body().string();
                    List<ReportReasonViewHolder.ReportReasonObject> lr=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<List<ReportReasonViewHolder.ReportReasonObject>>(){}.getType());
                    for(ReportReasonViewHolder.ReportReasonObject rr:lr){
                        this.items.add(rr);
                    }
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                    });
                }
                break;
            case "REPORT_SUBMIT":
                this.finish();
                OkHttpPackage.showToastResponse(this,response);
                break;
        }
    }

}
