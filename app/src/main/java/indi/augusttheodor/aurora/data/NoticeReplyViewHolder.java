package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class NoticeReplyViewHolder extends RecyclerView.ViewHolder {

    public TextView author;
    public LinearLayout block;
    public TextView time;
    public TextView content;
    public TextView reply_content; //回复
    public LinearLayout reply_block; //回复区块
    public TextView trend_type; //回复对象的类型
    public TextView title; //回复对象的标题或内容

    public NoticeReplyViewHolder(@NonNull View itemView) {
        super(itemView);
        this.block=itemView.findViewById(R.id.trend_link);
        this.author=itemView.findViewById(R.id.user_name);
        this.time=itemView.findViewById(R.id.time);
        this.content=itemView.findViewById(R.id.content);
        this.reply_block=itemView.findViewById(R.id.reply_block);
        this.reply_content=itemView.findViewById(R.id.origin_content);
        this.trend_type=itemView.findViewById(R.id.trend_type);
        this.title=itemView.findViewById(R.id.origin_title);
    }

    public class NoticeReplyObject{

        public String author_name; //回复者的名字
        public String author; //回复者的id
        public String time; //时间
        public String content; //回复或者提到的内容
        public String reply_content; //回复的发言（如果有）
        public String reply_title; //回复的帖子，如果是动态则是动态内容
        public String trend; //回复对象的id
        public String trend_type; //回复的对象的类型 帖子、转发、动态

    }

}
