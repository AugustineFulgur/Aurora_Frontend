package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class GroupListViewHolder extends RecyclerView.ViewHolder {
    //分组排行榜的ViewHolder

    public LinearLayout group_link;
    public ImageView group_pic;
    public TextView group_desc;
    public TextView group_name;
    public TextView group_number;

    public GroupListViewHolder(@NonNull View itemView) {
        super(itemView);
        this.group_link=itemView.findViewById(R.id.group_link);
        this.group_pic=itemView.findViewById(R.id.group_pic);
        this.group_name=itemView.findViewById(R.id.group_name);
        this.group_desc=itemView.findViewById(R.id.group_desc);
        this.group_number=itemView.findViewById(R.id.group_number);
    }

    public class GroupListObject{

        public String group_pic;
        public String group_name;
        public String group_desc;
        public String group_number;
        public String group_id;

    }

}
