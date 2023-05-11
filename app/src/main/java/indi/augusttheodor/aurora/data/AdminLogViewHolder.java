package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class AdminLogViewHolder extends RecyclerView.ViewHolder {

    public TextView content;
    public TextView operation;
    public TextView admin;
    public TextView time;

    public AdminLogViewHolder(@NonNull View itemView) {
        super(itemView);
        this.content=itemView.findViewById(R.id.content);
        this.admin=itemView.findViewById(R.id.admin_name);
        this.time=itemView.findViewById(R.id.time);
        this.operation=itemView.findViewById(R.id.operation);
    }

    public class AdminLogObject{

        public String content;
        public String operation;
        public String admin;
        public String time;

    }

}
