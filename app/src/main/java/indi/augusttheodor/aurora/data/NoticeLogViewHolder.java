package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class NoticeLogViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout area;
    public TextView content;
    public TextView time;

    public NoticeLogViewHolder(@NonNull View itemView) {
        super(itemView);
        this.area=itemView.findViewById(R.id.notice);
        this.content=itemView.findViewById(R.id.notice_content);
        this.time=itemView.findViewById(R.id.time);
    }

    public class NoticeLogObject{

        public String content;
        public String time;
        public String type;
        public String type_id;

    }

}
