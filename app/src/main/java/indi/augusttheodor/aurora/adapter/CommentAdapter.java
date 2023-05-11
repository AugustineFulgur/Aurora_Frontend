package indi.augusttheodor.aurora.adapter;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.ReportDialogActivity;
import indi.augusttheodor.aurora.activity.UserDetailActivity;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.data.CommentMetaViewHolder;
import indi.augusttheodor.aurora.tools.OkHttpPackage;

public class CommentAdapter extends RecyclerView.Adapter<CommentMetaViewHolder>{ //帖子的Adapter

    public AdapterItems<CommentMetaViewHolder.CommentMetaObject> item; //绑定的Item组
    public Fragment fragment;
    public boolean is_article; //是文章就开回复回复功能（实在不知道怎么描述这玩意）
    public boolean is_admin=false;
    public static String comment_id;
    public static TextView comment_inside;
    public static TextView comment_outside;
    public Handler handler; //用于运行UI操作

    public CommentAdapter(AdapterItems<CommentMetaViewHolder.CommentMetaObject> item, Fragment fragment,boolean is_article,Handler handler) {
        this.item = item;
        this.fragment = fragment;
        this.is_article=is_article;
        comment_id=""; //重置comment_id
        this.handler=handler;
    }

    @NonNull
    @Override
    public CommentMetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.fragment.getContext()).inflate(R.layout.item_article_comment, parent, false);
        return new CommentMetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentMetaViewHolder holder, int position) {
        CommentMetaViewHolder.CommentMetaObject map=this.item.getItems().get(position);
        if(map.reply_comment_author_name!=null){ //包含上下文，渲染上下文
            holder.reply.setVisibility(View.VISIBLE);
            holder.reply.setText(Html.fromHtml("<b>"+map.reply_comment_author_name+"</b>&nbsp;&nbsp;&nbsp;&nbsp;"+AuroraApplication.autoEllipsis(map.reply_comment_content,100),FROM_HTML_MODE_COMPACT));
        }else{
            holder.reply.setVisibility(View.GONE);
        } //如果有回复对象就渲染为楼中楼，否则就正常渲染
        holder.id.setTag(map.id);
        AuroraApplication.setCommentSpan(map.image,holder.content,map.content,fragment.getContext(),fragment.getActivity()); //设置图片和@
        AuroraApplication.image_loader.displayImage(fragment.getString(R.string.backend_image)+AuroraApplication.shiftString(map.author_nick),holder.nick_img,AuroraApplication.short_memory_opt);
        holder.author_name.setOnClickListener(
                v->{
                    Intent jump=new Intent(fragment.getContext(), UserDetailActivity.class);
                    jump.putExtra("user_id",map.author);
                    fragment.startActivity(jump);
                }
        );
        holder.nick_img.setOnClickListener(
                v->{
                    Intent jump=new Intent(fragment.getContext(), UserDetailActivity.class);
                    jump.putExtra("user_id",map.author);
                    fragment.startActivity(jump);
                }
        );
        //切换回复对象
        holder.content.setOnLongClickListener(v -> { //改为长点击
            comment_id=map.id; //点击此处发起回复
            comment_inside.setHint(fragment.getString(R.string.content_reply_hint).replace("0",map.author_name));
            comment_outside.setHint(fragment.getString(R.string.content_reply_hint).replace("0",map.author_name));
            return false;
        });
        holder.like.setText(map.like);
        holder.like.setChecked(map.is_prefer);
        //点赞
        holder.like.setOnClickListener(v -> {
            AuroraApplication.httpPackage.asyncRequest(
                    fragment.getString(R.string.backend_website) + fragment.getString(R.string.backend_comment_prefer).replace("0", map.id),
                    "GET",
                    null,
                    "SET_LIKE",
                    (call, response, tag) -> {
                        if(response.code()==200){
                            boolean status= response.body().string().equals("True");
                            holder.like.setChecked(status);
                            holder.like.setText(Integer.parseInt(holder.like.getText().toString())+(status?1:-1)+""); //对应数据的增减
                        }else{
                            Looper.prepare();
                            Toast.makeText(fragment.getContext(), fragment.getString(R.string.hint_set_fail), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    },
                    fragment.getContext()
            ); //点赞
        });
        holder.time.setText(AuroraApplication.shiftTime(map.time));
        holder.author_name.setText(map.author_name);
        holder.option.setOnClickListener(v->{
            PopupMenu menu=new PopupMenu(fragment.getContext(),holder.option);
            menu.getMenuInflater().inflate(R.menu.comment_option_menu,menu.getMenu());
            menu.getMenu().findItem(R.id.delete).setVisible(map.author.equals(AuroraApplication.userID) || is_admin); //判断
            menu.setOnMenuItemClickListener(m->{
                switch (m.getItemId()){
                    case R.id.report:
                        Intent jump=new Intent(fragment.getContext(), ReportDialogActivity.class);
                        jump.putExtra("sth_id",map.id);
                        jump.putExtra("type",AuroraApplication.Constants.Comment);
                        fragment.getActivity().startActivity(jump);
                        break;
                    case R.id.delete:
                        AuroraApplication.httpPackage.asyncRequest(
                                fragment.getString(R.string.backend_website) + fragment.getString(R.string.backend_comment_delete).replace("{0}", map.id),
                                "GET",
                                null,
                                null,
                                (call, response, tag) -> {
                                    this.item.getItems().remove(map); //删除本身
                                    handler.post(()->{
                                        this.notifyDataSetChanged();
                                    });
                                    OkHttpPackage.showToastResponse(fragment.getContext(),response);
                                },
                                fragment.getContext()
                        );
                        break;
                }

                return false;
            });
            menu.show();
        });
        if(position==getItemCount()-1){ //最后一个，重设高度
            RecyclerView.LayoutParams p= (RecyclerView.LayoutParams) holder.id.getLayoutParams();
            p.setMargins(0,0,0,AuroraApplication.dp2px(fragment.getContext(),65));
            holder.id.setLayoutParams(p);
        }else{
            RecyclerView.LayoutParams p= (RecyclerView.LayoutParams) holder.id.getLayoutParams();
            p.setMargins(0,0,0,0);
            holder.id.setLayoutParams(p);
        }
    }

    @Override
    public int getItemCount() { return this.item.getItems().size(); }

}
