package indi.augusttheodor.aurora.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.SummaryDetailActivity;
import indi.augusttheodor.aurora.data.SummaryRectViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;

public class SummaryRectAdapter extends RecyclerView.Adapter<SummaryRectViewHolder> {

    public Context context;
    public AdapterItems<SummaryRectViewHolder.SummaryRectObject> item;

    public SummaryRectAdapter(Context context, AdapterItems<SummaryRectViewHolder.SummaryRectObject> item){
        this.context=context;
        this.item=item;
    }

    @NonNull
    @Override
    public SummaryRectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SummaryRectViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_summary_rect,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryRectViewHolder holder, int position) {
        SummaryRectViewHolder.SummaryRectObject map=this.item.getItems().get(position);
        holder.summary_name.setText(map.summary_name);
        holder.summary_sum.setText(map.summary_sum);
        holder.summary_detail.setOnClickListener(v -> {
            Intent intent=new Intent(this.context, SummaryDetailActivity.class);
            intent.putExtra("summary_id",map.summary_id);
            context.startActivity(intent);
        });
        AuroraApplication.image_loader.displayImage(context.getString(R.string.backend_image)+AuroraApplication.shiftString(map.summary_pic),holder.summary_pic,AuroraApplication.r_long_rect_opt); //加载图片
    }

    @Override
    public int getItemCount() {
        return this.item.getItems().size();
    }

}
