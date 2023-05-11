package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.utils.L;

import indi.augusttheodor.aurora.R;

public class SummaryArticleViewHolder extends RecyclerView.ViewHolder {

    public TextView article_title;
    public TextView author_name;
    public ImageView author_nick;
    public ConstraintLayout article;
    public TextView star_desc; //收藏时写的备注
    public TextView star_time; //收藏时间
    public ConstraintLayout desc_block;
    public Button delete;

    public SummaryArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        this.article=itemView.findViewById(R.id.article_link);
        this.article_title=(TextView)itemView.findViewById(R.id.article_title);
        this.author_name=(TextView)itemView.findViewById(R.id.author_name);
        this.author_nick=(ImageView)itemView.findViewById(R.id.author_nick);
        this.star_desc=(TextView) itemView.findViewById(R.id.summary_desc);
        this.star_time=(TextView) itemView.findViewById(R.id.create_time);
        this.desc_block=(ConstraintLayout) itemView.findViewById(R.id.desc_block);
        this.delete=itemView.findViewById(R.id.delete);
    }

    public class SummaryArticleObject{
        public String article_id;
        public String article_title;
        public String author_id;
        public String author_name;
        public String author_nick;
        public String desc;
        public String time;
    }

}
