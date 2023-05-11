package indi.augusttheodor.aurora.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import java.util.HashMap;

import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.fragment.SquareFragment;
import indi.augusttheodor.aurora.fragment.SquareStreamFragment;
import indi.augusttheodor.aurora.fragment.SubscribeGroupFragment;
import indi.augusttheodor.aurora.fragment.UserMetaFragment;
import indi.augusttheodor.aurora.inter.ViewPager2PageChange;
import indi.augusttheodor.aurora.tools.HeartBeat;
import indi.augusttheodor.aurora.inter.ViewPager2LayoutAdapter;

public class NavigatorActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup navigate_bar; //导航栏组件
    private ViewPager2 view_pager;
    private HashMap<View,Integer> ids=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HeartBeat.getInstance(this);
        //初始化心跳包发送器，因为这里必然是登陆态所以放这里
        setContentView(R.layout.activity_navigator); //这里设置容器视图为R中的activity_main layout文件
        setIds();
        this.view_pager=findViewById(R.id.viewpager2); //查找viewpager2的位置
        this.view_pager.setAdapter(new NavigatorLayoutAdapter(getSupportFragmentManager(),getLifecycle())); //初始化Adapter
        this.view_pager.setUserInputEnabled(true); //禁止滑动
        this.view_pager.registerOnPageChangeCallback(new ViewPager2PageChange(this.ids));
        this.navigate_bar=((RadioGroup) findViewById(R.id.navigate_bar));
        this.navigate_bar.setOnCheckedChangeListener(this); //添加事件监听器
        ((RadioButton)findViewById(R.id.news)).setChecked(true);
        findViewById(R.id.notice).setOnClickListener(HeartBeat.v);
        findViewById(R.id.new_notice).setOnClickListener(HeartBeat.v);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                findViewById(R.id.new_notice).setVisibility(HeartBeat.sum==0? View.INVISIBLE:View.VISIBLE);
                ((Button)findViewById(R.id.new_notice)).setText(String.valueOf(HeartBeat.sum));
            }
        },new IntentFilter("HEARTBEAT"));
        super.onCreate(savedInstanceState);
    }

    private void setIds(){
        this.ids.put(findViewById(R.id.news),0);
        this.ids.put(findViewById(R.id.square),1);
        this.ids.put(findViewById(R.id.subscribe),2);
        this.ids.put(findViewById(R.id.mine),3); //建立映射
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup,int checkId){ //当导航栏图标被点击设定页面转换
        this.view_pager.setCurrentItem(this.ids.get(radioGroup.findViewById(checkId)),false); //禁止平滑切换
    }

    private final class NavigatorLayoutAdapter extends ViewPager2LayoutAdapter {

        public NavigatorLayoutAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
            super.fragment_list.add(new SquareStreamFragment()); //第一个是动态
            super.fragment_list.add(new SquareFragment()); //广场
            super.fragment_list.add(new SubscribeGroupFragment()); //订阅
            super.fragment_list.add(new UserMetaFragment()); //我的
        }

    }

}
