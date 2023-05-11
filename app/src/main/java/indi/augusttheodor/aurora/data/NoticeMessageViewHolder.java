package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

import indi.augusttheodor.aurora.R;

public class NoticeMessageViewHolder extends RecyclerView.ViewHolder {

    public TextView content;
    public ImageView nick;
    public TextView time;

    public NoticeMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        this.content=itemView.findViewById(R.id.message);
        this.nick=itemView.findViewById(R.id.nick);
        this.time=itemView.findViewById(R.id.time);
    }

}
