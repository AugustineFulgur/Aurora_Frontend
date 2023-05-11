package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class TopArticleViewHolder extends RecyclerView.ViewHolder {
    //置顶文章的ViewHolder

    public ConstraintLayout link;
    public TextView title;

    public TopArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        this.link=itemView.findViewById(R.id.top_article);
        this.title=itemView.findViewById(R.id.article_title);
    }

    public class TopArticleObject{

        public String title;
        public String id;

    }

}
