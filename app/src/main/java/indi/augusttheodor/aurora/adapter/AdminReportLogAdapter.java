package indi.augusttheodor.aurora.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.ArticleActivity;
import indi.augusttheodor.aurora.data.AdminReportLogViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.OkHttpPackage;

public class AdminReportLogAdapter extends RecyclerView.Adapter<AdminReportLogViewHolder> {

    public AdapterItems<AdminReportLogViewHolder.AdminReportLogObject> item;
    public Activity activity;

    public AdminReportLogAdapter(AdapterItems<AdminReportLogViewHolder.AdminReportLogObject> item, Activity activity){
        this.item=item;
        this.activity=activity;
    }

    @NonNull
    @Override
    public AdminReportLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.activity).inflate(R.layout.item_report_log, parent, false);
        return new AdminReportLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminReportLogViewHolder holder, int position) {
        AdminReportLogViewHolder.AdminReportLogObject map=this.item.getItems().get(position);
        holder.type.setText(map.type);
        holder.report_content.setText(map.report_content);
        holder.report_content.setOnClickListener(v -> {
            Intent jump=new Intent(activity, ArticleActivity.class);
            jump.putExtra("article_id",map.content_id);
            activity.startActivity((jump));
        });
        //监听事件
        holder.time.setText(AuroraApplication.shiftTime(map.report_time));
        holder.report_reason.setText(map.report_reason);
        holder.submit.setOnClickListener(v -> {
            AlertDialog.Builder builder=new AlertDialog.Builder(activity);
            builder.setTitle(R.string.hint_are_you_sure);
            builder.setNegativeButton(R.string.chancel,null);
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                HashMap<String,String> dict=new HashMap<>();
                dict.put("method",(holder.deal_delete.isChecked()?"D":"")+(holder.deal_remove.isChecked()?"R":"")+(holder.deal_kick.isChecked()?"K":"")+(holder.deal_reject.isChecked()?"N":""));
                AuroraApplication.httpPackage.asyncRequest(
                        activity.getString(R.string.backend_website) + activity.getString(R.string.backend_deal_report).replace("{0}", map.id),
                        "GET",
                        dict,
                        null,
                        (call, response, tag) -> {
                            OkHttpPackage.showToastResponse(activity,response);
                        },
                        activity
                );
            });
            builder.show();
        });

    }

    @Override
    public int getItemCount() { return this.item.getItems().size(); }
}
