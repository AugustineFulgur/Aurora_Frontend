package indi.augusttheodor.aurora.adapter;

import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.UserDetailActivity;
import indi.augusttheodor.aurora.data.MemberViewHolder;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.OkHttpPackage;

public class MemberAdapter extends RecyclerView.Adapter<MemberViewHolder> {

    public static final int SEARCH_MEMBER=1;
    public static final int ADMIN_MEMBER=2; //这是否也算一种枚举
    public static final int NULL=1000; //为空


    public AdapterItems<MemberViewHolder.MemberObject> items;
    public String group_id;
    public Context context;
    public boolean is_master; //是否master
    public int member_type=MemberAdapter.NULL;

    public MemberAdapter(AdapterItems<MemberViewHolder.MemberObject> item,Context context,boolean is_master, int member_type,String group_id){
        this.items=item;
        this.context=context;
        this.is_master=is_master;
        this.member_type=member_type;
        this.group_id=group_id;
    }

    public MemberAdapter(AdapterItems<MemberViewHolder.MemberObject> item,Context context){
        //只是显示用户的情况
        this.items=item;
        this.context=context;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        MemberViewHolder.MemberObject map=this.items.getItems().get(position);
        holder.member_name.setText(map.member_name);
        AuroraApplication.image_loader.displayImage(context.getString(R.string.backend_image)+AuroraApplication.shiftString(map.member_nick),holder.member_nick,AuroraApplication.short_memory_opt);
        holder.user_link.setOnClickListener(V->{
            Intent intent=new Intent(context, UserDetailActivity.class);
            intent.putExtra("user_id",map.member_id);
            context.startActivity(intent);
        });
        if(member_type==MemberAdapter.NULL){
            holder.member_option.setVisibility(GONE);
            //不需要额外处理，只要填数据即可
            return;
        }else{
            PopupMenu.OnMenuItemClickListener listener= item -> {
                HashMap<String,String> dict=new HashMap<>();
                switch (item.getItemId()){
                    case R.id.upgrade:
                        dict.put("operation","UPGRADE");
                        break;
                    case R.id.dismiss:
                        dict.put("operation","DISMISS");
                        break;
                    case R.id.transfer_master:
                        dict.put("operation","TRANSFER_MASTER");
                        break;
                    case R.id.kick_out:
                        dict.put("operation","KICK_OUT");
                        break;
                    case R.id.block:
                        dict.put("operation","BLOCK");
                        break;
                }
                AuroraApplication.httpPackage.asyncRequest(
                        context.getString(R.string.backend_website)+context.getString(R.string.backend_admin_manage_member).replace("{0}",group_id).replace("{1}", map.member_id),
                        "POST",
                        dict,
                        null,
                        (call, response, tag) -> { OkHttpPackage.showToastResponse(context,response); },
                        context
                );
                return false;
            }; //好长哦
            switch (this.member_type){
                case MemberAdapter.ADMIN_MEMBER:
                    if(is_master){
                        holder.member_option.setVisibility(View.VISIBLE);
                        holder.member_option.setOnClickListener(v->{
                            PopupMenu menu=new PopupMenu(context,holder.member_option);
                            menu.getMenuInflater().inflate(R.menu.item_member_menu,menu.getMenu());
                            menu.setOnMenuItemClickListener(listener);
                            menu.show();
                        });
                    }
                    else{
                        holder.member_option.setVisibility(View.GONE);
                    }
                    break;
                case MemberAdapter.SEARCH_MEMBER:
                    holder.member_option.setVisibility(View.VISIBLE);
                    holder.member_option.setOnClickListener(v->{
                        PopupMenu menu=new PopupMenu(context,holder.member_option);
                        menu.getMenuInflater().inflate(R.menu.item_member_menu,menu.getMenu());
                        menu.getMenu().findItem(R.id.upgrade).setVisible(is_master);
                        menu.getMenu().findItem(R.id.transfer_master).setVisible(is_master);
                        menu.getMenu().findItem(R.id.dismiss).setVisible(false); //这是普通界面，不需要这玩意
                        menu.setOnMenuItemClickListener(listener);
                        menu.show();
                    });
                    break;
            }
        }
    }

    @Override
    public int getItemCount() { return this.items.getItems().size(); }

}
