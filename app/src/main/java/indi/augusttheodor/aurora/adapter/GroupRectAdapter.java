package indi.augusttheodor.aurora.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.data.GroupRectViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.ListenerAssemble;

public class GroupRectAdapter extends RecyclerView.Adapter<GroupRectViewHolder> {

    public AdapterItems<GroupRectViewHolder.GroupRectObject> item;
    public Fragment fragment;

    public GroupRectAdapter(AdapterItems<GroupRectViewHolder.GroupRectObject> item, Fragment fragment) {
        this.item = item;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public GroupRectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.fragment.getContext()).inflate(R.layout.item_group_rect, parent, false);
        return new GroupRectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupRectViewHolder holder, int position) {
        @NonNull GroupRectViewHolder.GroupRectObject map=this.item.getItems().get(position);
        holder.group_name.setText(map.group_name);
        holder.group_link.setTag(map.group_id);
        holder.group_link.setOnClickListener(ListenerAssemble.jumpToTopic);
        AuroraApplication.image_loader.displayImage(fragment.getString(R.string.backend_image)+AuroraApplication.shiftString(map.group_pic),holder.group_pic,AuroraApplication.r_long_rect_opt); //加载图片
    }

    @Override
    public int getItemCount() { return this.item.getItems().size(); }

}
