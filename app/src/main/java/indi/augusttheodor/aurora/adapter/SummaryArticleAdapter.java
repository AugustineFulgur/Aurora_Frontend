package indi.augusttheodor.aurora.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.HashMap;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.ArticleActivity;
import indi.augusttheodor.aurora.activity.UserDetailActivity;
import indi.augusttheodor.aurora.data.SummaryArticleViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.OkHttpPackage;

public class SummaryArticleAdapter extends RecyclerView.Adapter<SummaryArticleViewHolder> {

    public AdapterItems<SummaryArticleViewHolder.SummaryArticleObject> item;
    public Activity activity;
    public String summary_id;
    public Handler handler;

    public SummaryArticleAdapter(AdapterItems<SummaryArticleViewHolder.SummaryArticleObject> items,Activity activity,String summary_id,Handler handler){
        this.item=items;
        this.activity=activity;
        this.summary_id=summary_id;
        this.handler=handler;
    }

    @NonNull
    @Override
    public SummaryArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.activity).inflate(R.layout.item_summary_article, parent, false);
        return new SummaryArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryArticleViewHolder holder, int position) {
        SummaryArticleViewHolder.SummaryArticleObject map=this.item.getItems().get(position);
        View.OnClickListener l=v->{
            Intent jump=new Intent(activity, UserDetailActivity.class);
            jump.putExtra("user_id",map.author_id);
            activity.startActivity((jump));
        };
        holder.article.setTag(map.article_id);
        holder.article_title.setText(map.article_title);
        AuroraApplication.image_loader.displayImage(activity.getString(R.string.backend_image)+map.author_nick,holder.author_nick,AuroraApplication.short_memory_opt);
        holder.author_name.setOnClickListener(l);
        holder.author_nick.setOnClickListener(l);
        holder.author_name.setText(map.author_name);
        holder.article.setOnClickListener(v->{
            Intent jump=new Intent(activity, ArticleActivity.class);
            jump.putExtra("article_id",map.article_id);
            activity.startActivity((jump));
        });
        holder.star_desc.setVisibility(map.desc.equals("")?View.GONE:View.VISIBLE);
        holder.star_desc.setText(map.desc);
        holder.star_time.setText(AuroraApplication.shiftTime(map.time));
        holder.desc_block.setOnClickListener(v -> { //修改收藏理由
            AlertDialog.Builder builder=new AlertDialog.Builder(activity);
            EditText edit=new EditText(activity);
            edit.setHint(R.string.hint_star_desc);
            builder.setTitle(R.string.hint_star_desc);
            builder.setNegativeButton(R.string.chancel,null);
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                HashMap<String,String> dict=new HashMap<>();
                String desc=edit.getText().toString();
                dict.put("desc",desc);
                AuroraApplication.httpPackage.asyncRequest(
                        activity.getString(R.string.backend_website) + activity.getString(R.string.backend_summary_article_modify).replace("{0}", summary_id).replace("{1}", map.article_id),
                        "POST",
                        dict,
                        null,
                        (call, response, tag) -> {
                            holder.star_desc.setText(desc);
                            OkHttpPackage.showToastResponse(activity,response);
                        },
                        activity
                );
            });
            builder.setView(edit);
            builder.show();
        });
        holder.delete.setOnClickListener(v -> { //删除收藏的帖子
            AlertDialog.Builder builder=new AlertDialog.Builder(activity);
            builder.setTitle(R.string.delete_summary_article);
            TextView hint=new TextView(activity);
            hint.setText(R.string.hint_are_you_sure);
            builder.setNegativeButton(R.string.chancel,null);
            builder.setPositiveButton(R.string.ok, (dialog, which) -> AuroraApplication.httpPackage.asyncRequest(
                    activity.getString(R.string.backend_website)  + activity.getString(R.string.backend_summary_article_delete).replace("{0}", summary_id).replace("{1}", map.article_id),
                    "GET",
                    null,
                    null,
                    (call, response, tag) -> {
                        this.item.getItems().remove(map);
                        handler.post(()->{
                            item.getItems().remove(map);
                            this.notifyItemRemoved(position);
                            this.notifyItemRangeChanged(position,position+1);
                        });
                        OkHttpPackage.showToastResponse(activity,response);
                    },
                    activity
            ));
            builder.setView(hint);
            builder.show();

        });
    }

    @Override
    public int getItemCount() { return this.item.getItems().size(); }
}
