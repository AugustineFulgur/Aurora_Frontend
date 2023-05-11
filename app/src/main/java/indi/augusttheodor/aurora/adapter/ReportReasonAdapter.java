package indi.augusttheodor.aurora.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.data.ReportReasonViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;

public class ReportReasonAdapter extends RecyclerView.Adapter<ReportReasonViewHolder> {

    public AdapterItems<ReportReasonViewHolder.ReportReasonObject> item;
    public AppCompatActivity activity;
    public int report_id; //选择的举报理由的编号
    private List<ReportReasonViewHolder> li; //用这个实现下，单选效果

    public ReportReasonAdapter(AdapterItems<ReportReasonViewHolder.ReportReasonObject> item,AppCompatActivity activity){
        this.item=item;
        this.activity=activity;
        this.li= new ArrayList<>();
    }

    @NonNull
    @Override
    public ReportReasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(this.activity).inflate(R.layout.item_report_radio,parent,false);
        return new ReportReasonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportReasonViewHolder holder, int position) {
        li.add(holder);
        ReportReasonViewHolder.ReportReasonObject map=this.item.getItems().get(position);
        holder.item.setText(map.reason);
        holder.item.setOnClickListener(v -> {
            reSetChecked();
            holder.item.setChecked(true);
            this.report_id=map.id;
        });
    }

    private void reSetChecked(){
        for(ReportReasonViewHolder v:li){
            v.item.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return this.item.getItems().size();
    }
}
