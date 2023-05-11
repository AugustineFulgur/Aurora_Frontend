package indi.augusttheodor.aurora.data;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class SummaryRectViewHolder extends RecyclerView.ViewHolder {

    public TextView summary_name;
    public TextView summary_sum;
    public ImageView summary_pic;
    public LinearLayout summary_detail;

    public SummaryRectViewHolder(@NonNull View itemView) {
        super(itemView);
        this.summary_name=itemView.findViewById(R.id.summary_name);
        this.summary_pic=itemView.findViewById(R.id.summary_pic);
        this.summary_sum=itemView.findViewById(R.id.summary_sum);
        this.summary_detail=itemView.findViewById(R.id.summary_detail);
    }

    public class SummaryRectObject{

        public String summary_name;
        public String summary_sum;
        public String summary_pic;
        public String summary_id;
        
    }

}
