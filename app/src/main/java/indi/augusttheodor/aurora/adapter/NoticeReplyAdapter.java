package indi.augusttheodor.aurora.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.TransmitActivity;
import indi.augusttheodor.aurora.activity.TrendActivity;
import indi.augusttheodor.aurora.data.NoticeReplyViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;

public class NoticeReplyAdapter extends RecyclerView.Adapter<NoticeReplyViewHolder> {

    public AdapterItems<NoticeReplyViewHolder.NoticeReplyObject> item; //绑定的Item组
    public Fragment fragment;

    public NoticeReplyAdapter(AdapterItems<NoticeReplyViewHolder.NoticeReplyObject> item, Fragment fragment) {
        this.item = item;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public NoticeReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.fragment.getContext()).inflate(R.layout.item_notice_reply, parent, false);
        return new NoticeReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeReplyViewHolder holder, int position) { //官方给的type函数不方便，我自己写
        NoticeReplyViewHolder.NoticeReplyObject map=this.item.getItems().get(position);
        holder.author.setText(map.author_name);
        holder.author.setTag(map.author);
        holder.content.setText(map.content);
        holder.block.setTag(map.trend);
        holder.time.setText(AuroraApplication.shiftTime(map.time));
        holder.title.setText(map.reply_title);
        //公共部分的代码
        if(map.reply_content.equals("")){
            holder.reply_block.setVisibility(View.GONE);
        }else{
            holder.reply_block.setVisibility(View.VISIBLE);
            holder.reply_content.setText(map.reply_content);
        } //回复内容判定
        switch (map.trend_type){
            case AuroraApplication.Constants.Article:
                holder.block.setOnClickListener(ListenerAssemble.jumpToArticle);
                holder.trend_type.setText(fragment.getString(R.string.in_article));
                break;
            case AuroraApplication.Constants.Transmit:
                holder.trend_type.setText(fragment.getString(R.string.in_transmit));
                holder.block.setOnClickListener(v->{
                    Intent jump=new Intent(v.getContext().getApplicationContext(), TransmitActivity.class);
                    jump.putExtra("transmit_id",map.trend);
                    v.getContext().startActivity((jump));
                });
                break;
            case AuroraApplication.Constants.Trend:
                holder.trend_type.setText(fragment.getString(R.string.in_trend));
                holder.block.setOnClickListener(v->{
                    Intent jump=new Intent(v.getContext().getApplicationContext(), TrendActivity.class);
                    jump.putExtra("trend_id",map.trend);
                    v.getContext().startActivity((jump));
                });
        } //通知对应的对象
        //不同类型通知的处理
    }


    @Override
    public int getItemCount() {
        return this.item.getItems().size();
    }
}
