package indi.augusttheodor.aurora.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.data.AdminApplyViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.OkHttpPackage;

public class AdminApplyAdapter extends RecyclerView.Adapter<AdminApplyViewHolder> {

    public AdapterItems<AdminApplyViewHolder.AdminApplyObject> item;
    public Context context;
    public String group_id;

    public AdminApplyAdapter(AdapterItems<AdminApplyViewHolder.AdminApplyObject> item,Context context,String group_id){
        this.context=context;
        this.item=item;
        this.group_id=group_id;
    }

    @NonNull
    @Override
    public AdminApplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_admin_apply, parent, false);
        return new AdminApplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminApplyViewHolder holder, int position) {
        AdminApplyViewHolder.AdminApplyObject map=this.item.getItems().get(position);
        holder.user_name.setText(map.user_name);
        AuroraApplication.image_loader.displayImage(context.getString(R.string.backend_image)+AuroraApplication.shiftString(map.user_nick),holder.user_nick,AuroraApplication.short_memory_opt);
        holder.content.setText(map.content);
        holder.agree.setOnClickListener(v->{
            callDealRequest(context.getString(R.string.backend_deal_apply).replace("{2}","AGREE"),map.id);
            this.item.getItems().remove(map); //移除
            this.notifyDataSetChanged();
        });
        holder.refuse.setOnClickListener(v->{
            callDealRequest(context.getString(R.string.backend_deal_apply).replace("{2}","REFUSE"),map.id);
            this.item.getItems().remove(map); //移除
            this.notifyDataSetChanged();
        });
    }

    private void callDealRequest(String url,String id){
        AuroraApplication.httpPackage.asyncRequest(
                context.getString(R.string.backend_website) + url.replace("{0}",group_id).replace("{1}",id),
                "GET",
                null,
                null,
                (call, response, tag) -> {
                    OkHttpPackage.showToastResponse(context,response);
                },
                context
        );
    }

    @Override
    public int getItemCount() { return this.item.getItems().size(); }

}
