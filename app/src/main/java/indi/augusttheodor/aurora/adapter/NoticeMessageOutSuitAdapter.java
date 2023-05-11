package indi.augusttheodor.aurora.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.ChatMessageActivity;
import indi.augusttheodor.aurora.data.NoticeMessageOutSuitViewHolder;
import indi.augusttheodor.aurora.db.DbChatHeader;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.HeartBeat;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import indi.augusttheodor.aurora.tools.SettingObject;

public class NoticeMessageOutSuitAdapter extends RecyclerView.Adapter<NoticeMessageOutSuitViewHolder> {

    public AdapterItems<DbChatHeader> item; //绑定的Item组
    public Fragment fragment;

    public NoticeMessageOutSuitAdapter(AdapterItems<DbChatHeader> item, Fragment fragment) {
        this.item = item;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public NoticeMessageOutSuitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.fragment.getContext()).inflate(R.layout.item_chat, parent, false);
        return new NoticeMessageOutSuitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeMessageOutSuitViewHolder holder, int position) { //官方给的type函数不方便，我自己写
        DbChatHeader map=this.item.getItems().get(position);
        holder.block.setOnClickListener(v->{
            Intent jump=new Intent(fragment.getContext().getApplicationContext(), ChatMessageActivity.class);
            jump.putExtra("user_id",map.author_id);
            jump.putExtra("user_name",map.author_name);
            //HeartBeat.changeMessage(-map.unread); //点击之后减少未读消息数量
            fragment.getContext().startActivity((jump));
        });
        holder.last_time.setText(map.last_time);
        AuroraApplication.image_loader.displayImage(fragment.getString(R.string.backend_image)+map.author_nick,holder.nick,AuroraApplication.long_memory_opt);
        holder.last_message.setText(map.last_message);
        holder.name.setText(map.author_name);
        if(map.unread==0){
            //无消息，隐藏泡泡
            holder.unread.setVisibility(View.INVISIBLE);
        }else{
            holder.unread.setVisibility(View.VISIBLE);
            holder.unread.setText(String.valueOf(map.unread));
        }
    }


    @Override
    public int getItemCount() {
        return this.item.getItems().size();
    }
}
