package indi.augusttheodor.aurora.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.GroupActivity;
import indi.augusttheodor.aurora.data.StreamMetaViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.adapter.StreamOutSuitAdapter;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.inter.ScrollToTop;
import okhttp3.Call;
import okhttp3.Response;

public class SquareStreamFragment extends Fragment implements RadioGroup.OnCheckedChangeListener , View.OnClickListener { //使用Adapter获取数据并绑定

    private ViewPager2 view_pager;
    private SquareStreamLayoutAdapter layout_adapter;
    private RadioGroup radio_group;
    private HashMap<Integer,Integer> ids;
    public Activity activity;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square_stream, container, false);
        this.view_pager=view.findViewById(R.id.view_pager);
        this.radio_group=view.findViewById(R.id.radio_group);
        this.radio_group.setOnCheckedChangeListener(this);
        this.setIds();
        this.layout_adapter=new SquareStreamFragment.SquareStreamLayoutAdapter(getChildFragmentManager(),getLifecycle()); //是Fragment嵌套所以需要调用getChildFragmentManager
        this.view_pager.setAdapter(this.layout_adapter); //初始化Adapter
        this.view_pager.setUserInputEnabled(false); //允许滑动，这是Fragment嵌套就不加手势识别了
        view.findViewById(R.id.top).setOnClickListener(this);
        ((RadioButton)view.findViewById(R.id.random)).setChecked(true);
        return view;
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity=getActivity();
    }

    public void setIds(){
        this.ids=new HashMap<>();
        this.ids.put(R.id.random,0);
        this.ids.put(R.id.trends,1);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkId) {
        this.view_pager.setCurrentItem(this.ids.get(checkId),true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.top:
                ((ScrollToTop)this.layout_adapter.fragment_list.get(this.view_pager.getCurrentItem())).scroll();
                break;
        }
    }

    private class SquareStreamLayoutAdapter extends FragmentStateAdapter{

        private ArrayList<Fragment> fragment_list=new ArrayList<Fragment>();

        public SquareStreamLayoutAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager,lifecycle);
            RandomStreamFragment random=new RandomStreamFragment();
            SubscribeStreamFragment subscribe=new SubscribeStreamFragment();
            this.fragment_list.add(random);
            this.fragment_list.add(subscribe);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return this.fragment_list.get(position);
        }

        @Override
        public int getItemCount() { return fragment_list.size(); }
    }

}
