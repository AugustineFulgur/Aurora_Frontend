package indi.augusttheodor.aurora.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.ArticleActivity;
import indi.augusttheodor.aurora.data.TopArticleViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;

public class TopArticleAdapter extends RecyclerView.Adapter<TopArticleViewHolder> {

    public AdapterItems<TopArticleViewHolder.TopArticleObject> item;
    public Context context;

    public TopArticleAdapter(AdapterItems<TopArticleViewHolder.TopArticleObject> item,Context context){
        this.item=item;
        this.context=context;
    }

    @NonNull
    @Override
    public TopArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_top_article, parent, false);
        return new TopArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopArticleViewHolder holder, int position) {
        TopArticleViewHolder.TopArticleObject map=this.item.getItems().get(position);
        holder.title.setText(map.title);
        holder.link.setOnClickListener(v->{
            Intent jump=new Intent(context, ArticleActivity.class);
            jump.putExtra("article_id",map.id);
            context.startActivity((jump));
        });
    }

    @Override
    public int getItemCount() {
        return this.item.getItems().size();
    }
}
