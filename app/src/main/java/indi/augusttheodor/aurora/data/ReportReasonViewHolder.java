package indi.augusttheodor.aurora.data;

import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.R;

public class ReportReasonViewHolder extends RecyclerView.ViewHolder {

    public RadioButton item;

    public ReportReasonViewHolder(@NonNull View itemView) {
        super(itemView);
        this.item=itemView.findViewById(R.id.report_reason);
    }

    public class ReportReasonObject{
        //简单的事情复杂化并非我本意
        public String reason;
        public Integer id;
    }
}
