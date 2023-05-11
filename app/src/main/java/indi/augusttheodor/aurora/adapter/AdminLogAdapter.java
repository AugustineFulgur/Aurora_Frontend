package indi.augusttheodor.aurora.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.data.AdminLogViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;

public class AdminLogAdapter extends RecyclerView.Adapter<AdminLogViewHolder> {

    public AdapterItems<AdminLogViewHolder.AdminLogObject> item;
    public Context context;

    public AdminLogAdapter(AdapterItems<AdminLogViewHolder.AdminLogObject> item,Context context){
        this.item=item;
        this.context=context;
    }

    @NonNull
    @Override
    public AdminLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_admin_log, parent, false);
        return new AdminLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminLogViewHolder holder, int position) {
        AdminLogViewHolder.AdminLogObject map=this.item.getItems().get(position);
        holder.time.setText(AuroraApplication.shiftTime(map.time));
        holder.content.setText(map.content);
        holder.admin.setText(map.admin);
        holder.operation.setText(map.operation);
    }

    @Override
    public int getItemCount() { return this.item.getItems().size(); }

}
