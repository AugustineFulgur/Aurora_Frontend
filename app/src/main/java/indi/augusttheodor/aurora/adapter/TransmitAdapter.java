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
import indi.augusttheodor.aurora.data.TransmitMetaViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;

public class TransmitAdapter extends RecyclerView.Adapter<TransmitMetaViewHolder> { //帖子的Adapter

    public AdapterItems<TransmitMetaViewHolder.TransmitMetaObject> item; //绑定的Item组
    public Fragment fragment;

    public TransmitAdapter(AdapterItems<TransmitMetaViewHolder.TransmitMetaObject> item, Fragment fragment) {
        this.item = item;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public TransmitMetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.fragment.getContext()).inflate(R.layout.item_article_transmit, parent, false);
        return new TransmitMetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransmitMetaViewHolder holder, int position) {
        TransmitMetaViewHolder.TransmitMetaObject map=this.item.getItems().get(position);
        holder.content.setText(map.content);
        holder.id.setOnClickListener(v->{
            Intent jump=new Intent(fragment.getContext(), TransmitActivity.class);
            jump.putExtra("transmit_id",map.id);
            fragment.startActivity((jump));
        });
        AuroraApplication.image_loader.displayImage(fragment.getString(R.string.backend_image)+map.author_nick,holder.nick_img,AuroraApplication.short_memory_opt);
        holder.time.setText(AuroraApplication.shiftTime(map.time));
        holder.author_id.setTag(map.author);
        holder.author_name.setText(map.author_name);
        holder.author_name.setOnClickListener(ListenerAssemble.jumpToUser);
    }

    @Override
    public int getItemCount() { return this.item.getItems().size(); }

}
