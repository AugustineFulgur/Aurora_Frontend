package indi.augusttheodor.aurora.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.ArticleActivity;
import indi.augusttheodor.aurora.data.AdminReportArticleViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.OkHttpPackage;

public class AdminReportArticleAdapter extends RecyclerView.Adapter<AdminReportArticleViewHolder> {

    public AdapterItems<AdminReportArticleViewHolder.AdminReportArticleObject> item;
    public Activity activity;

    public AdminReportArticleAdapter(AdapterItems<AdminReportArticleViewHolder.AdminReportArticleObject> item, Activity activity){
        this.item=item;
        this.activity=activity;
    }

    @NonNull
    @Override
    public AdminReportArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.activity).inflate(R.layout.item_report_article, parent, false);
        return new AdminReportArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminReportArticleViewHolder holder, int position) {
        AdminReportArticleViewHolder.AdminReportArticleObject map=this.item.getItems().get(position);
        holder.type.setText(AuroraApplication.Constants.Article);
        holder.report_content.setText(map.report_content);
        holder.report_content.setOnClickListener(v -> {
            Intent jump=new Intent(activity, ArticleActivity.class);
            jump.putExtra("article_id",map.article_id);
            activity.startActivity((jump));
        });
        //监听事件
        holder.report_time.setText(map.report_time);
        holder.option.setOnClickListener(v -> {
            AlertDialog.Builder builder=new AlertDialog.Builder(activity);
            builder.setTitle(R.string.delete);
            builder.setNegativeButton(R.string.chancel,null);
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                AuroraApplication.httpPackage.asyncRequest(
                        activity.getString(R.string.backend_website) + activity.getString(R.string.backend_article_delete).replace("0", map.article_id),
                        "GET",
                        null,
                        "NO_MORE",
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
