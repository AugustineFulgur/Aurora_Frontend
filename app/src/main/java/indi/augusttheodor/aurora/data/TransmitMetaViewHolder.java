package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indi.augusttheodor.aurora.R;

//转发就这点东西，其实没啥区别（就多了一个所属帖子的，待会让它继承一下），但是还是要分两个类
public class TransmitMetaViewHolder extends RecyclerView.ViewHolder{

    public class TransmitMetaObject{

        public String author; //作者id
        public String author_name; //作者名
        public String author_nick; //作者头像
        public String content; //评论内容
        public String id; //转发id
        public String time; //时间

    }

    public LinearLayout id; //转发的编号
    public TextView author_name; //转发作者名
    public LinearLayout author_id; //作者id
    public TextView time; //转发时间
    public RoundedImageView nick_img; //头像
    public TextView content; //内容

    public TransmitMetaViewHolder(@NonNull View itemView) {
        super(itemView);
        this.id=(LinearLayout)itemView.findViewById(R.id.transmit);
        this.author_name=(TextView)itemView.findViewById(R.id.username);
        this.author_id=(LinearLayout)itemView.findViewById(R.id.user_link);
        this.time=(TextView)itemView.findViewById(R.id.time);
        this.nick_img=(RoundedImageView)itemView.findViewById(R.id.nick_img);
        this.content=(TextView)itemView.findViewById(R.id.comment_content);
    }
}

