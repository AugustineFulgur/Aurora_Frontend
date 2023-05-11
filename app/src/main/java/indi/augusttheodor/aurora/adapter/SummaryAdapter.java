package indi.augusttheodor.aurora.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.data.SummaryViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryViewHolder> {

    public static final int LARGE=0;
    public static final int SMALL=1;

    public AdapterItems<SummaryViewHolder.SummaryObject> item;
    public Activity activity;
    public View.OnClickListener listener; //我好聪明
    public int type;

    public SummaryAdapter(AdapterItems<SummaryViewHolder.SummaryObject> items, Activity activity, View.OnClickListener listener, int type){
        this.item=items;
        this.activity=activity;
        this.listener=listener;
        this.type=type;
    }

    @NonNull
    @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (type){
            case LARGE:
                view= LayoutInflater.from(this.activity).inflate(R.layout.item_summary,parent,false);
                break;
            case SMALL:
                view= LayoutInflater.from(this.activity).inflate(R.layout.item_summary_small,parent,false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return new SummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, int position) {
        SummaryViewHolder.SummaryObject map=item.getItems().get(position);
        holder.summary_link.setOnClickListener(listener);
        holder.summary_link.setTag(map.summary_link);
        holder.summary_name.setText(map.summary_name);
        holder.summary_desc.setText(map.summary_desc);
        AuroraApplication.image_loader.displayImage(activity.getString(R.string.backend_image)+AuroraApplication.shiftString(map.summary_pic),holder.summary_pic,AuroraApplication.r_long_rect_opt); //加载图片
    }

    @Override
    public int getItemCount() {
        return this.item.getItems().size();
    }
}
