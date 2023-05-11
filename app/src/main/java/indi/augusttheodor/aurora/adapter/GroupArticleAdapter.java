package indi.augusttheodor.aurora.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.UserDetailActivity;
import indi.augusttheodor.aurora.data.GroupArticleViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;

public class GroupArticleAdapter extends RecyclerView.Adapter<GroupArticleViewHolder> {

    public AdapterItems<GroupArticleViewHolder.GroupArticleObject> item; //绑定的Item组
    public Context context;

    public GroupArticleAdapter(AdapterItems<GroupArticleViewHolder.GroupArticleObject> item, Context context) {
        this.item = item;
        this.context = context;
    }

    public void changeItem(AdapterItems<GroupArticleViewHolder.GroupArticleObject> item){
        this.item=item;
    } //特殊情况，允许多装点

    @NonNull
    @Override
    public GroupArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_article_group, parent, false);
        return new GroupArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupArticleViewHolder holder, int position) {
        GroupArticleViewHolder.GroupArticleObject map=this.item.getItems().get(position);
        View.OnClickListener l=v->{
            Intent jump=new Intent(context, UserDetailActivity.class);
            jump.putExtra("user_id",map.author_id);
            v.getContext().startActivity((jump));
        };
        holder.article.setTag(map.article_id);
        holder.article_title.setText(map.article_title);
        holder.article_amount.setText(map.article_amount);
        AuroraApplication.image_loader.displayImage(context.getString(R.string.backend_image)+AuroraApplication.shiftString(map.author_nick),holder.author_nick,AuroraApplication.short_memory_opt);
        holder.author_name.setText(map.author_name);
        holder.author_name.setOnClickListener(l);
        holder.author_nick.setOnClickListener(l);
        holder.article.setOnClickListener(ListenerAssemble.jumpToArticle);
        holder.time.setText(AuroraApplication.shiftTime(map.article_time));
    }

    @Override
    public int getItemViewType(int position) {
        return 0; //只有一个type
    }

    @Override
    public int getItemCount() { return this.item.getItems().size(); }
}
