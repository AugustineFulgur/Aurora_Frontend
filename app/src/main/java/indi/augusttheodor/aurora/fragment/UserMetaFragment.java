package indi.augusttheodor.aurora.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.CreateTrendActivity;
import indi.augusttheodor.aurora.activity.PeopleListActivity;
import indi.augusttheodor.aurora.activity.SettingActivity;
import indi.augusttheodor.aurora.activity.UserCreateSummaryActivity;
import indi.augusttheodor.aurora.activity.UserDetailActivity;
import indi.augusttheodor.aurora.activity.SummaryListActivity;
import indi.augusttheodor.aurora.activity.UserGroupActivity;
import indi.augusttheodor.aurora.activity.UserHistoryArticleActivity;
import indi.augusttheodor.aurora.data.UserMetaObject;
import indi.augusttheodor.aurora.inter.HttpInterface;
import okhttp3.Call;
import okhttp3.Response;

public class UserMetaFragment extends Fragment implements View.OnClickListener, HttpInterface{

    public Activity activity;
    public View view;

    @Override //container 父layout saveInstanceState 当fragment被恢复时存储中断前的数据 inflater 加载layout的id 父layout 在加载期间是否负载到父layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_meta, container, false);
        this.view=view;
        view.findViewById(R.id.seek_article).setOnClickListener(this);
        view.findViewById(R.id.seek_group).setOnClickListener(this);
        view.findViewById(R.id.seek_following).setOnClickListener(this);
        view.findViewById(R.id.seek_followed).setOnClickListener(this);
        view.findViewById(R.id.seek_homepage).setOnClickListener(this);
        view.findViewById(R.id.seek_summary).setOnClickListener(this);
        view.findViewById(R.id.seek_following_summary).setOnClickListener(this);
        view.findViewById(R.id.seek_setting).setOnClickListener(this);
        view.findViewById(R.id.set_trend).setOnClickListener(this);
        //设置监听
        return view;
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity=getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_user)+AuroraApplication.userID,
                "GET",
                null,
                "USER_META",
                this,
                this.getContext()
        );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.seek_article:
                startActivity(new Intent(getContext(), UserHistoryArticleActivity.class));
                break;
            case R.id.seek_group:
                startActivity(new Intent(getContext(), UserGroupActivity.class));
                break;
            case R.id.seek_following:
                Intent following=new Intent(getContext(), PeopleListActivity.class);
                following.putExtra("TYPE",PeopleListActivity.FOLLOWING);
                startActivity(following);
                break;
            case R.id.seek_followed:
                Intent followed=new Intent(getContext(), PeopleListActivity.class);
                followed.putExtra("TYPE",PeopleListActivity.FOLLOWED);
                startActivity(followed);
                break;
            case R.id.seek_setting:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            case R.id.seek_summary:
                view.getContext().startActivity(new Intent(getContext(), UserCreateSummaryActivity.class));
                break;
            case R.id.seek_following_summary:
                Intent following_summary_intent=new Intent(getContext(), SummaryListActivity.class);
                following_summary_intent.putExtra("link",getString(R.string.backend_user_following_summary));
                view.getContext().startActivity(following_summary_intent);
                break;
            case R.id.seek_homepage:
                Intent homepage_intent=new Intent(getContext(), UserDetailActivity.class);
                homepage_intent.putExtra("user_id",AuroraApplication.userID);
                view.getContext().startActivity(homepage_intent);
                break;
            case R.id.set_trend:
                startActivity(new Intent(getContext(), CreateTrendActivity.class));
                break;
        }
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "USER_META":
                if(response.code()==200){
                    String s=response.body().string();
                    UserMetaObject o=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<UserMetaObject>(){}.getType());
                    activity.runOnUiThread(() -> {
                        if(!isAdded()){return;}
                        AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+o.user_himg,((ImageView)view.findViewById(R.id.himg)),AuroraApplication.long_memory_opt); //加载图片
                        AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+o.user_bimg,((ImageView)view.findViewById(R.id.bimg)),AuroraApplication.long_memory_opt);
                        ((TextView)view.findViewById(R.id.name)).setText(o.user_name);
                        ((TextView)view.findViewById(R.id.signature)).setText(o.user_signature);
                        ((TextView)view.findViewById(R.id.following)).setText(o.user_following);
                        ((TextView)view.findViewById(R.id.followed)).setText(o.user_followed);
                        ((TextView)view.findViewById(R.id.article)).setText(o.user_article);
                        ((TextView)view.findViewById(R.id.group)).setText(o.user_group);
                    });
                }
                else{
                    Looper.prepare();
                    Toast.makeText(getContext(), getString(R.string.hint_stream_fail), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                break;
        }

    }

}