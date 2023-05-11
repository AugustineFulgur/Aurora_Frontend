package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class MemberViewHolder extends RecyclerView.ViewHolder {

    public ImageView member_nick;
    public TextView member_name;
    public View user_link; //为了使用ListenerAssemble
    public RadioButton member_option;


    public MemberViewHolder(@NonNull View itemView) {
        super(itemView);
        this.member_nick=itemView.findViewById(R.id.member_nick);
        this.member_name=itemView.findViewById(R.id.member_name);
        this.user_link=itemView.findViewById(R.id.member);
        this.member_option=itemView.findViewById(R.id.manage);
    }

    public class MemberObject{

        public String member_name;
        public String member_nick;
        public String member_id;

    }

}
