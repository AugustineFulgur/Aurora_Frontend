package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class SummaryViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout summary_link; //放id用的
    public TextView summary_name;
    public TextView summary_desc;
    public ImageView summary_pic;

    public SummaryViewHolder(@NonNull View itemView) {
        super(itemView);
        this.summary_link=itemView.findViewById(R.id.summary_link);
        this.summary_pic=itemView.findViewById(R.id.summary_pic);
        this.summary_name=itemView.findViewById(R.id.summary_name);
        this.summary_desc=itemView.findViewById(R.id.summary_desc);
    }

    public class SummaryObject {

        public String summary_link;
        public String summary_name;
        public String summary_desc;
        public String summary_pic;
    }

    public class SummaryMetaObject{

        public String summary_link;
        public String summary_name;
        public String summary_desc;
        public String summary_pic;
        public String summary_dict; //帖子数量
        public String summary_follow; //关注数量
        public boolean is_following; //是否关注了
        public boolean is_author; //是否是创建者
        public String create_time; //创建时间

    }

}
