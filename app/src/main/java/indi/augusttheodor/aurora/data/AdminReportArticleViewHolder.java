package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import indi.augusttheodor.aurora.R;

public class AdminReportArticleViewHolder extends RecyclerView.ViewHolder {

    public TextView type;
    public TextView report_content; //这里顺便放跳转id
    public TextView report_time;
    public RadioButton option;

    public AdminReportArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        this.type=itemView.findViewById(R.id.type);
        this.report_content=itemView.findViewById(R.id.report_content);
        this.report_time=itemView.findViewById(R.id.report_reason);
        this.option=itemView.findViewById(R.id.option);
    }

    public class AdminReportArticleObject{

        public String article_id; //内容id
        public String report_content;
        public String report_time; //被举报次数

    }

}
