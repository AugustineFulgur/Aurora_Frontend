package indi.augusttheodor.aurora.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.ArticleActivity;
import indi.augusttheodor.aurora.activity.TransmitActivity;
import indi.augusttheodor.aurora.activity.TrendActivity;
import indi.augusttheodor.aurora.activity.UserDetailActivity;
import indi.augusttheodor.aurora.data.StreamMetaViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;

public class StreamOutSuitAdapter extends RecyclerView.Adapter<StreamMetaViewHolder>{

    public AdapterItems<StreamMetaViewHolder.StreamMetaObject> item; //绑定的Item组
    public Context context;
    public static Map<String,Integer> map;

    public StreamOutSuitAdapter(AdapterItems<StreamMetaViewHolder.StreamMetaObject> item, Context context){
        this.item=item;
        this.context=context;
    }

    @NonNull
    @Override
    public StreamMetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view;
        if(viewType==2){
            view=LayoutInflater.from(this.context).inflate(R.layout.item_transmit,parent,false);
        }
        else{
            view=LayoutInflater.from(this.context).inflate(R.layout.item_trend,parent,false);
        }
        return new StreamMetaViewHolder(view,viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull StreamMetaViewHolder holder, int position) {
        StreamMetaViewHolder.StreamMetaObject map=this.item.getItems().get(position);
        switch(this.getItemViewType(position)){
            case 0: //帖子
                holder.area.setText(map.group_name);
                holder.trend_title.setText(map.title);
                holder.trend_meta.setText(map.content);
                holder.trend_meta.setTextSize(20);
                holder.trend_link.setTag(map.s_id);
                AuroraApplication.image_loader.displayImage(context.getString(R.string.backend_image)+map.nick_img,holder.nick_img,AuroraApplication.short_memory_opt);
                holder.trend_link.setOnClickListener(ListenerAssemble.jumpToArticle); //设置监听器
                break;
            case 1: //动态
                holder.area.setVisibility(View.GONE);
                holder.trend_title.setText(map.nick);
                holder.trend_meta.setText(map.content);
                holder.trend_meta.setTextSize(15);
                AuroraApplication.image_loader.displayImage(context.getString(R.string.backend_image)+map.nick_img,holder.nick_img,AuroraApplication.short_memory_opt);
                holder.trend_link.setOnClickListener(v->{
                    Intent jump=new Intent(context, TrendActivity.class);
                    jump.putExtra("trend_id",map.s_id);
                    context.startActivity((jump));
                });
                break;
            case 2: //转发
                holder.area.setVisibility(View.GONE);
                holder.trend_title.setText(map.title);
                holder.trend_meta.setText(map.content);
                holder.trend_meta.setTextSize(15);
                AuroraApplication.image_loader.displayImage(context.getString(R.string.backend_image)+map.nick_img,holder.nick_img,AuroraApplication.short_memory_opt);
                AuroraApplication.image_loader.displayImage(context.getString(R.string.backend_image)+map.origin_author_nick,holder.origin_author_nick,AuroraApplication.short_memory_opt);
                holder.trend_link.setOnClickListener(v->{
                    Intent jump=new Intent(context, TransmitActivity.class);
                    jump.putExtra("transmit_id",map.s_id);
                    context.startActivity((jump));
                }); //设置监听器
                holder.origin_author_name.setText(map.origin_author_name);
                holder.origin_article_link.setOnClickListener(v->{
                    Intent jump=new Intent(context, ArticleActivity.class);
                    jump.putExtra("article_id",map.origin);
                    context.startActivity((jump));
                });
                holder.origin_article_title.setText(map.origin_title);
                holder.origin_author_nick.setOnClickListener(v->{
                    Intent jump=new Intent(context, UserDetailActivity.class);
                    jump.putExtra("user_id",map.origin_author_id);
                    context.startActivity((jump));
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        StreamMetaViewHolder.StreamMetaObject map=this.item.getItems().get(position);
        if(map.group_name==null){
            if(map.origin!=null){
                return 2; //转发
            }
            return 1; //动态
        }
        return 0; //帖子
    }

    @Override
    public int getItemCount(){
        return this.item.getItems().size();
    }
}
