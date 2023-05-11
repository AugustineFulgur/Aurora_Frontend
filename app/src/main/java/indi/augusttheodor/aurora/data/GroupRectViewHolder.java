package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class GroupRectViewHolder extends RecyclerView.ViewHolder {

    public class GroupRectObject{
        public @NonNull String group_pic="history.jpg"; //图片
        public @NonNull String group_name; //分组名
        public @NonNull String group_id; //id

    }

    public ImageView group_pic;
    public TextView group_name;
    public LinearLayout group_link;

    public GroupRectViewHolder(View view){
        super(view);
        this.group_pic=view.findViewById(R.id.group_pic);
        this.group_name=view.findViewById(R.id.group_name);
        this.group_link=view.findViewById(R.id.group_link);
    }

}
