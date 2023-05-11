package indi.augusttheodor.aurora.data;

import android.media.Image;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class NoticeMessageOutSuitViewHolder extends RecyclerView.ViewHolder {

    public Button unread;
    public TextView name;
    public ImageView nick;
    public TextView last_message;
    public TextView last_time;
    public RelativeLayout block;

    public NoticeMessageOutSuitViewHolder(@NonNull View itemView) {
        super(itemView);
        unread=itemView.findViewById(R.id.new_notice);
        nick=itemView.findViewById(R.id.chat_nick);
        name=itemView.findViewById(R.id.name);
        last_message=itemView.findViewById(R.id.context);
        last_time=itemView.findViewById(R.id.time);
        block=itemView.findViewById(R.id.chat_link);
    }

}
