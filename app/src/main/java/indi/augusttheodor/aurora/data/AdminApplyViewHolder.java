package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class AdminApplyViewHolder extends RecyclerView.ViewHolder {

    public TextView user_name;
    public ImageView user_nick;
    public TextView content;
    public TextView agree;
    public TextView refuse;

    public AdminApplyViewHolder(@NonNull View itemView) {
        super(itemView);
        this.user_name=itemView.findViewById(R.id.user_name);
        this.user_nick=itemView.findViewById(R.id.user_nick);
        this.content=itemView.findViewById(R.id.content);
        this.agree=itemView.findViewById(R.id.agree);
        this.refuse= itemView.findViewById(R.id.refuse);
    }

    public class AdminApplyObject{

        public String user_name;
        public String user_nick;
        public String content;
        public String id;

    }

}
