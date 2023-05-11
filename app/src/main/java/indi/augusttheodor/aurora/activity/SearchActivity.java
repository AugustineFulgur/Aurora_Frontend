package indi.augusttheodor.aurora.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.fragment.SearchArticleFragment;
import indi.augusttheodor.aurora.fragment.SearchGroupFragment;
import indi.augusttheodor.aurora.inter.SearchInter;
import indi.augusttheodor.aurora.inter.ViewPager2LayoutAdapter;
import indi.augusttheodor.aurora.inter.ViewPager2PageChange;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    public ViewPager2 view_pager;
    private HashMap<View,Integer> ids;
    public RadioGroup group;
    public TextView search;
    private SearchLayoutAdapter layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setIds();
        String content=getIntent().getStringExtra("content");
        this.view_pager=findViewById(R.id.viewpager2);
        this.group=findViewById(R.id.bar);
        this.layout=new SearchLayoutAdapter(getSupportFragmentManager(),getLifecycle(),this);
        this.view_pager.setAdapter(this.layout);
        this.view_pager.setUserInputEnabled(true);
        this.view_pager.registerOnPageChangeCallback(new ViewPager2PageChange(this.ids));
        this.search=findViewById(R.id.search);
        this.group.setOnCheckedChangeListener(this);
        ((RadioButton)findViewById(R.id.article)).setChecked(true); //设置选中第一个
        findViewById(R.id.search_btn).setOnClickListener(this);
        this.search.setText(content);
        if(!content.equals("")){
            //有值，就发起搜索
            ((SearchInter)this.layout.fragment_list.get(this.view_pager.getCurrentItem())).setKeywordNext(content);
        }
    }

    private void setIds(){
        this.ids=new HashMap<>();
        this.ids.put(findViewById(R.id.article),0);
        this.ids.put(findViewById(R.id.group),1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_btn:
                //发起搜索
                ((SearchInter)this.layout.fragment_list.get(this.view_pager.getCurrentItem())).clear();
                ((SearchInter)this.layout.fragment_list.get(this.view_pager.getCurrentItem())).setKeywordNext(this.search.getText().toString());
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String keyword=this.search.getText().toString();
        this.view_pager.setCurrentItem(this.ids.get(group.findViewById(checkedId)),true);
        if(!keyword.equals("")){
            //有值，就发起搜索
            ((SearchInter)this.layout.fragment_list.get(this.view_pager.getCurrentItem())).clear();
            ((SearchInter)this.layout.fragment_list.get(this.view_pager.getCurrentItem())).setKeywordNext(keyword);
        }
    }

    private class SearchLayoutAdapter extends ViewPager2LayoutAdapter { //这里不能继承ViewPager2LayoutAdapter

        private ArrayList<Fragment> fragment_list=new ArrayList<>();

        public SearchLayoutAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Activity activity) {
            super(fragmentManager, lifecycle);
            this.fragment_list.add(new SearchArticleFragment(activity));
            this.fragment_list.add(new SearchGroupFragment(activity));
            super.fragment_list=this.fragment_list;
        }

    }
    
}