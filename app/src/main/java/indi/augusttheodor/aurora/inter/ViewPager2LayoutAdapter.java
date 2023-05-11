package indi.augusttheodor.aurora.inter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public abstract class ViewPager2LayoutAdapter extends FragmentStateAdapter { //ViewPager2用的Adapter的一个统一的封装

    public ArrayList<Fragment> fragment_list=new ArrayList<Fragment>();

    //这个方法需要重写，所有类加到这个super的对象里
    public ViewPager2LayoutAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return this.fragment_list.get(position);
    }

    @Override
    public int getItemCount() {
        return this.fragment_list.size();
    }
}
