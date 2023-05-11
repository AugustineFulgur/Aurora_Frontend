package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import indi.augusttheodor.aurora.R;

//各种推送（文章、转发、动态）
public class StreamMetaViewHolder extends RecyclerView.ViewHolder {

    //对应实体类
    public class StreamMetaObject {
        public String s_id; //帖子id
        public String nick; //头像旁边的标题
        public String nick_img;
        public String group_name; //所属的分组
        public String content;
        public String title;

        public String origin; //如果是转发，就有值
        public String origin_title;
        public String origin_author_name;
        public String origin_author_nick;
        public String origin_author_id;

    }

    public TextView trend_title;
    public TextView area;
    public LinearLayout trend_link; //链接放在整个区域内
    public RoundedImageView nick_img; //左上角图片
    public TextView trend_meta;

    public TextView origin_author_name;
    public ImageView origin_author_nick;
    public TextView origin_article_title;
    public LinearLayout origin_article_link;


    public StreamMetaViewHolder(@NonNull View itemView,int viewType) {
        super(itemView);
        this.area=(TextView) itemView.findViewById(R.id.trend_area);
        this.nick_img=(RoundedImageView) itemView.findViewById(R.id.area_img);
        this.trend_title=(TextView) itemView.findViewById(R.id.trend_article);
        this.trend_link= (LinearLayout) itemView.findViewById(R.id.trend_link);
        this.trend_meta=(TextView) itemView.findViewById(R.id.trend_article_meta);
        if(viewType==2){
            //转发
            this.origin_article_link=itemView.findViewById(R.id.origin_article_link);
            this.origin_article_title=itemView.findViewById(R.id.origin_article_title);
            this.origin_author_nick=itemView.findViewById(R.id.origin_author_nick);
            this.origin_author_name=itemView.findViewById(R.id.origin_author_name);
        }
    }

}

