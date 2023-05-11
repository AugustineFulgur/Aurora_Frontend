package indi.augusttheodor.aurora.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.ArticleActivity;
import indi.augusttheodor.aurora.activity.GroupActivity;
import indi.augusttheodor.aurora.activity.TransmitActivity;
import indi.augusttheodor.aurora.activity.TrendActivity;
import indi.augusttheodor.aurora.activity.UserDetailActivity;

public class AuroraTextClickableSpan extends ClickableSpan { //重写的SpanClickable

    private String id;
    private Context context;
    private String type;

    public static final String group="分组";
    public static final String article="帖子";
    public static final String user="用户";
    public static final String transmit="转发";
    public static final String trend="动态";

    public AuroraTextClickableSpan(String id,Context context,String name){
        this.context=context;
        this.id=id;
        String[] s=name.split(":");
        this.type=s[0];
    }

    @Override
    public void onClick(@NonNull View view) { //根据type跳转
        Intent jump;
        switch (type){
            case group:
                jump=new Intent(view.getContext().getApplicationContext(), GroupActivity.class);
                jump.putExtra("group_id",id);
                view.getContext().startActivity((jump));
                break;
            case article:
                jump=new Intent(view.getContext().getApplicationContext(), ArticleActivity.class);
                jump.putExtra("article_id",id);
                view.getContext().startActivity((jump));
                break;
            case user:
                jump=new Intent(view.getContext().getApplicationContext(), UserDetailActivity.class);
                jump.putExtra("user_id",id);
                view.getContext().startActivity((jump));
                break;
            case transmit:
                jump=new Intent(view.getContext().getApplicationContext(), TransmitActivity.class);
                jump.putExtra("transmit_id",id);
                view.getContext().startActivity((jump));
                break;
            case trend:
                jump=new Intent(view.getContext().getApplicationContext(), TrendActivity.class);
                jump.putExtra("trend_id",id);
                view.getContext().startActivity((jump));
                break;
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        //绘画效果
        super.updateDrawState(ds);
        ds.setColor(context.getColor(R.color.aurora_theme));
        ds.setFakeBoldText(true);
        ds.setUnderlineText(false);
    }

}
