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
import indi.augusttheodor.aurora.activity.GroupActivity;
import indi.augusttheodor.aurora.data.GroupListViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListViewHolder> {

    public AdapterItems<GroupListViewHolder.GroupListObject> item;
    public Context context;

    public GroupListAdapter(AdapterItems<GroupListViewHolder.GroupListObject> item,Context context){
        this.item=item;
        this.context=context;
    }

    @NonNull
    @Override
    public GroupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_group_list,parent,false);
        return new GroupListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupListViewHolder holder, int position) {
        GroupListViewHolder.GroupListObject map=this.item.getItems().get(position);
        holder.group_name.setText(map.group_name);
        holder.group_desc.setText(map.group_desc);
        holder.group_number.setText(map.group_number);
        holder.group_link.setOnClickListener(v->{
            Intent jump=new Intent(context, GroupActivity.class);
            jump.putExtra("group_id",map.group_id);
            context.startActivity(jump);
        });
        AuroraApplication.image_loader.displayImage(context.getString(R.string.backend_image)+AuroraApplication.shiftImg(map.group_pic,context),holder.group_pic,AuroraApplication.r_long_rect_opt);
    }

    @Override
    public int getItemCount() {
        return this.item.getItems().size();
    }
}
