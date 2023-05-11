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
import indi.augusttheodor.aurora.activity.TransmitActivity;
import indi.augusttheodor.aurora.activity.UserDetailActivity;
import indi.augusttheodor.aurora.data.NoticeLogViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;

public class NoticeLogAdapter extends RecyclerView.Adapter<NoticeLogViewHolder> {

    public AdapterItems<NoticeLogViewHolder.NoticeLogObject> item;
    public Context context;

    public NoticeLogAdapter(AdapterItems<NoticeLogViewHolder.NoticeLogObject> items,Context context){
        this.item=items;
        this.context=context;
    }

    @NonNull
    @Override
    public NoticeLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_notice_log, parent, false);
        return new NoticeLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeLogViewHolder holder, int position) {
        NoticeLogViewHolder.NoticeLogObject map=this.item.getItems().get(position);
        holder.time.setText(AuroraApplication.shiftTime(map.time));
        holder.content.setText(map.content);
        holder.area.setOnClickListener(v->{
            Intent jump;
            switch (map.type){
                case AuroraApplication.Constants.Transmit:
                    jump=new Intent(context, TransmitActivity.class);
                    jump.putExtra("transmit_id",map.type_id);
                    context.startActivity((jump));
                    break;
                case AuroraApplication.Constants.User:
                    jump=new Intent(context, UserDetailActivity.class);
                    jump.putExtra("user_id",map.type_id);
                    context.startActivity((jump));
                    break;
            }
        });
    }

    @Override
    public int getItemCount() { return this.item.getItems().size(); }
}
