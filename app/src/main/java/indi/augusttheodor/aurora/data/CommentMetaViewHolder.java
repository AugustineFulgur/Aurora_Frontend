package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indi.augusttheodor.aurora.R;

public class CommentMetaViewHolder extends RecyclerView.ViewHolder{

    public LinearLayout id; //评论
    public TextView author_name; //回复作者
    public LinearLayout author_id; //作者id
    public TextView time; //回复时间
    public RoundedImageView nick_img; //头像
    public TextView content; //评论
    public RadioButton like; //点赞数
    public TextView reply; //上下文
    public Button option; //菜单

    public CommentMetaViewHolder(@NonNull View itemView) {
        super(itemView);
        this.id=(LinearLayout)itemView.findViewById(R.id.comment);
        this.author_name=(TextView)itemView.findViewById(R.id.username);
        this.author_id=(LinearLayout)itemView.findViewById(R.id.user_link);
        this.time=(TextView)itemView.findViewById(R.id.time);
        this.nick_img=(RoundedImageView)itemView.findViewById(R.id.nick_img);
        this.content=(TextView)itemView.findViewById(R.id.comment_content);
        this.like=(RadioButton)itemView.findViewById(R.id.like_btn);
        this.reply=itemView.findViewById(R.id.reply_block);
        this.option=itemView.findViewById(R.id.option);
    }

    //对应实体类
    public class CommentMetaObject { //下次再取这么傻缺的传参我自己抽死自己，转发的传参格式和这个一样，但是还是再写个类- -

        public String author; //作者id
        public String author_name; //作者名
        public String author_nick; //作者头像
        public String content; //评论内容
        public String id; //评论id
        public String time; //时间
        public String like; //点赞
        //如果是回复的回复，以下字段会有值
        public String reply_comment_content; //上下文的内容
        public String reply_comment_author_name; //上下文作者的名字
        public boolean is_prefer; //是否喜欢过
        public String image; //回复带图

    }

}


