package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;

//组内显示的文章
public class GroupArticleViewHolder extends RecyclerView.ViewHolder {

    //对应实体类
    public class GroupArticleObject{
        public String article_id;
        public String article_title;
        public String author_id;
        public String author_name;
        public String author_nick;
        public String article_amount; //回帖数量
        public String article_time; //时间
    }

    public TextView article_title;
    public TextView author_name;
    public ImageView author_nick;
    public TextView article_amount;
    public TextView time;
    public ConstraintLayout article;

    public GroupArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        this.article=itemView.findViewById(R.id.trend_link);
        this.article_title=itemView.findViewById(R.id.article_title);
        this.author_name=itemView.findViewById(R.id.author_name);
        this.author_nick=itemView.findViewById(R.id.author_nick);
        this.article_amount=itemView.findViewById(R.id.article_amount);
        this.time=itemView.findViewById(R.id.time);
    }

}
