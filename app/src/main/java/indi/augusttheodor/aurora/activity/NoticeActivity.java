package indi.augusttheodor.aurora.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import java.util.HashMap;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.fragment.NoticeLogFragment;
import indi.augusttheodor.aurora.fragment.NoticeMessageOutSuitFragment;
import indi.augusttheodor.aurora.fragment.NoticeReplyFragment;
import indi.augusttheodor.aurora.inter.ViewPager2PageChange;
import indi.augusttheodor.aurora.others.AuroraEasyBubbleListener;
import indi.augusttheodor.aurora.others.DragBubbleView;
import indi.augusttheodor.aurora.tools.AuroraDismissBubbleView;
import indi.augusttheodor.aurora.tools.HeartBeat;
import indi.augusttheodor.aurora.inter.ViewPager2LayoutAdapter;
import indi.augusttheodor.aurora.tools.SettingObject;
import me.shihao.library.XRadioGroup;

public class NoticeActivity extends AppCompatActivity implements XRadioGroup.OnCheckedChangeListener {

    private ViewPager2 view_pager;
    private HashMap<View,Integer> ids;
    private DragBubbleView sum_reply;
    private DragBubbleView sum_message;
    private DragBubbleView sum_information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        setIds();
        this.view_pager=findViewById(R.id.viewpager2);
        this.view_pager.setAdapter(new NoticeLayoutAdapter(getSupportFragmentManager(),getLifecycle())); //初始化Adapter
        this.view_pager.setUserInputEnabled(true);
        this.view_pager.registerOnPageChangeCallback(new ViewPager2PageChange(this.ids));
        ((XRadioGroup)findViewById(R.id.radio_group)).setOnCheckedChangeListener(this);
        this.sum_message=findViewById(R.id.new_message);
        this.sum_reply=findViewById(R.id.new_reply);
        this.sum_information=findViewById(R.id.new_information);
        this.sum_reply.setText(String.valueOf(HeartBeat.q_reply));
        this.sum_message.setText(String.valueOf(HeartBeat.q_message));
        this.sum_information.setText(String.valueOf(HeartBeat.q_notice));
        findViewById(R.id.notice_reply).setOnClickListener(v->removeReply());
        findViewById(R.id.notice_information).setOnClickListener(v->removeInformation());
        findViewById(R.id.notice_message).setOnClickListener(v->removeMessage());//放数据
        this.sum_reply.setOnBubbleStateListener(new AuroraEasyBubbleListener() {
            @Override
            public void onDismiss() {
                removeReply();
            }
        });
        this.sum_information.setOnBubbleStateListener(new AuroraEasyBubbleListener() {
            @Override
            public void onDismiss() {
                removeInformation();
            }
        });
        this.sum_message.setOnBubbleStateListener(new AuroraEasyBubbleListener() {
            @Override
            public void onDismiss() {
                removeMessage();
            }
        });
        this.sum_message.setVisibility(HeartBeat.q_message==0? View.INVISIBLE:View.VISIBLE);
        this.sum_reply.setVisibility(HeartBeat.q_reply==0? View.INVISIBLE:View.VISIBLE);
        this.sum_information.setVisibility(HeartBeat.q_notice==0? View.INVISIBLE:View.VISIBLE);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                findViewById(R.id.new_message).setVisibility(HeartBeat.q_message==0? View.INVISIBLE:View.VISIBLE);
                ((DragBubbleView)findViewById(R.id.new_message)).setText(String.valueOf(HeartBeat.q_message));
            }
        },new IntentFilter("HEARTBEAT"));
    }

    private void removeReply(){
        sum_reply.setVisibility(View.INVISIBLE);
        SettingObject.setReplyHint(0);
        SettingObject.apply();
        HeartBeat.q_reply=0;
    }

    private void removeMessage(){
        sum_message.setVisibility(View.INVISIBLE);
        HeartBeat.q_message=0;
        SettingObject.setMessageHint(0);
        SettingObject.apply();
    }

    private void removeInformation(){
        sum_information.setVisibility(View.INVISIBLE);
        SettingObject.setNoticeHint(0);
        SettingObject.apply();
        HeartBeat.q_notice=0;
    }

    private void setIds(){
        this.ids=new HashMap<>();
        this.ids.put(findViewById(R.id.notice_reply),0);
        this.ids.put(findViewById(R.id.notice_message),1);
        this.ids.put(findViewById(R.id.notice_information),2);
    }

    @Override
    public void onCheckedChanged(XRadioGroup group, int checkedId) {
        this.view_pager.setCurrentItem(this.ids.get(group.findViewById(checkedId)),false);
    }

    class NoticeLayoutAdapter extends ViewPager2LayoutAdapter {

        public NoticeLayoutAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
            super.fragment_list.add(new NoticeReplyFragment());
            super.fragment_list.add(new NoticeMessageOutSuitFragment());
            super.fragment_list.add(new NoticeLogFragment());
        }
    }

}
