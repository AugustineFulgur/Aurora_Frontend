package indi.augusttheodor.aurora.inter;

import android.view.View;
import android.widget.RadioButton;

import androidx.viewpager2.widget.ViewPager2;

import java.util.HashMap;
import java.util.Map;

public class ViewPager2PageChange extends ViewPager2.OnPageChangeCallback { //负责对应的按钮切换

    public HashMap<View,Integer> ids; //id映射

    public ViewPager2PageChange(HashMap<View,Integer> ids){
        this.ids=ids;
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        //设置对应的选项被选中
        for(Map.Entry<View,Integer> i: ids.entrySet()){
            if(i.getValue()==position){
                i.getKey();
                ((RadioButton)i.getKey()).setChecked(true); //设置被选中
            }
        }
    }

}
