package indi.augusttheodor.aurora.data;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import indi.augusttheodor.aurora.R;

public class AdminReportLogViewHolder extends RecyclerView.ViewHolder {

    public TextView type;
    public TextView report_content; //这里顺便放跳转id
    public TextView report_reason;
    public TextView time;
    public Button submit;
    public RadioButton deal_delete;
    public RadioButton deal_remove;
    public RadioButton deal_kick;
    public RadioButton deal_reject;

    public AdminReportLogViewHolder(@NonNull View itemView) {
        super(itemView);
        this.type=itemView.findViewById(R.id.type);
        this.report_content=itemView.findViewById(R.id.report_content);
        this.report_reason=itemView.findViewById(R.id.report_reason);
        this.time=itemView.findViewById(R.id.time);
        this.submit=itemView.findViewById(R.id.submit);
        this.deal_delete=itemView.findViewById(R.id.deal_delete);
        this.deal_remove=itemView.findViewById(R.id.deal_remove);
        this.deal_kick=itemView.findViewById(R.id.deal_kick);
        this.deal_reject=itemView.findViewById(R.id.deal_reject);
    }

    public class AdminReportLogObject {

        public String type;
        public String id; //投诉的id
        public String content_id; //内容id
        public String report_content;
        public String report_reason;
        public String report_time;

    }

}
