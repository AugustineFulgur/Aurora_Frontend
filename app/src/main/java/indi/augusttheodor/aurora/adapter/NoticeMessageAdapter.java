package indi.augusttheodor.aurora.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.data.NoticeMessageViewHolder;
import indi.augusttheodor.aurora.db.DbChatHeader;
import indi.augusttheodor.aurora.db.DbChatMessage;
import indi.augusttheodor.aurora.inter.AdapterItems;

public class NoticeMessageAdapter extends RecyclerView.Adapter<NoticeMessageViewHolder> {

    private static final int MESSAGE_RIGHT=0;
    private static final int MESSAGE_LEFT=1;

    public AdapterItems<DbChatMessage> item; //绑定的Item组
    public Activity activity;
    public DbChatHeader header;

    public NoticeMessageAdapter(AdapterItems<DbChatMessage> item, Activity activity, DbChatHeader header) {
        this.item = item;
        this.activity = activity;
        this.header = header;
    }

    @NonNull
    @Override
    public NoticeMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case MESSAGE_LEFT:
                view = LayoutInflater.from(this.activity).inflate(R.layout.item_chat_message_left, parent, false);
                break;
            case MESSAGE_RIGHT:
                view = LayoutInflater.from(this.activity).inflate(R.layout.item_chat_message_right, parent, false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return new NoticeMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeMessageViewHolder holder, int position) { //官方给的type函数不方便，我自己写
        switch (getItemViewType(position)){
            case MESSAGE_LEFT:
                AuroraApplication.image_loader.displayImage(activity.getString(R.string.backend_image)+header.author_nick,holder.nick,AuroraApplication.long_memory_opt);
                break;
            default:
                AuroraApplication.image_loader.displayImage(activity.getString(R.string.backend_image)+AuroraApplication.userNick,holder.nick,AuroraApplication.long_memory_opt);
                break;
        }
        DbChatMessage map=this.item.getItems().get(position);
        holder.time.setText(map.time);
        AuroraApplication.setImgSpan(map.img,holder.content,map.content,activity,activity); //放内容

    }


    @Override
    public int getItemCount() {
        return this.item.getItems().size();
    }

    @Override
    public int getItemViewType(int position) {
        DbChatMessage map=this.item.getItems().get(position);
        if(map.author_id.equals(AuroraApplication.userID)){
            //是自己发的
            return MESSAGE_RIGHT;
        }else{
            return MESSAGE_LEFT;
        }
    }
}
