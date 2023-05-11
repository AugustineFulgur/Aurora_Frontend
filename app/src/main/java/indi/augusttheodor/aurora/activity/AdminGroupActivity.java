package indi.augusttheodor.aurora.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.data.MemberViewHolder;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.tools.AuroraImageLoader;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public class AdminGroupActivity extends AppCompatActivity implements HttpInterface , View.OnClickListener , ColorPickerDialogListener {

    private String group_id;
    private String group_name;
    private String group_pic;
    public boolean is_master=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_group);
        //获取传值
        Intent intent=getIntent();
        this.group_id=intent.getStringExtra("group_id");
        this.group_name=intent.getStringExtra("group_name");
        this.group_pic=intent.getStringExtra("group_pic");
        //展示
        ((TextView)findViewById(R.id.group_name)).setText(this.group_name);
        AuroraApplication.image_loader.displayImage(getString(R.string.backend_image)+AuroraApplication.shiftString(this.group_pic),(ImageView)findViewById(R.id.group_pic),AuroraApplication.r_long_rect_opt); //加载图片
        //监听器-管理
        findViewById(R.id.manage_join_apply).setOnClickListener(this);
        findViewById(R.id.manage_admin).setOnClickListener(this);
        findViewById(R.id.manage_block_member).setOnClickListener(this);
        findViewById(R.id.manage_report_by_hot).setOnClickListener(this);
        findViewById(R.id.manage_report_by_time).setOnClickListener(this);
        findViewById(R.id.manage_search_member).setOnClickListener(this);
        //监听器-设定 （功能上没啥区别，分开写因为我闲^ ^，skr~）
        findViewById(R.id.manage_pic).setOnClickListener(this);
        findViewById(R.id.manage_member_name).setOnClickListener(this);
        findViewById(R.id.manage_area).setOnClickListener(this);
        findViewById(R.id.manage_introduction).setOnClickListener(this);
        findViewById(R.id.manage_theme_color).setOnClickListener(this);
        findViewById(R.id.manage_join_type).setOnClickListener(this);
        //监听器-杂项
        findViewById(R.id.manage_log).setOnClickListener(this);
        //辞职
        findViewById(R.id.resignation).setOnClickListener(this);
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_admin_master).replace("0",group_id),
                "GET",
                null,
                "ADMIN_MASTER",
                this,
                this
        );
    }


    @Override
    public void onClick(View v) {
        Intent jump;
        EditText edit=new EditText(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        HashMap<String,String> dict=new HashMap<>();
        switch (v.getId()){
            case R.id.manage_report_by_hot:
                //转到举报浏览界面
                jump=new Intent(this, AdminReportByHotActivity.class);
                jump.putExtra("group_id",group_id); //后端一定要做好鉴权
                startActivity(jump);
                break;
            case R.id.manage_report_by_time:
                //转到举报浏览界面
                jump=new Intent(this, AdminReportByTimeActivity.class);
                jump.putExtra("group_id",group_id); //后端一定要做好鉴权
                startActivity(jump);
                break;
            case R.id.manage_admin:
                //查看管理员
                jump=new Intent(this, AdminMemberActivity.class);
                jump.putExtra("group_id",group_id); //后端一定要做好鉴权
                jump.putExtra("is_master",this.is_master);
                startActivity(jump);
                break;
            case R.id.manage_search_member:
                //搜索成员
                jump=new Intent(this, AdminSearchMemberActivity.class);
                jump.putExtra("group_id",group_id); //后端一定要做好鉴权
                jump.putExtra("is_master",this.is_master);
                startActivity(jump);
                break;
            case R.id.manage_block_member:
                //查看黑名单
                jump=new Intent(this, AdminBlockMemberActivity.class);
                jump.putExtra("group_id",group_id); //后端一定要做好鉴权
                startActivity(jump);
                break;
            case R.id.manage_join_type:
                //加入分组的方式
                jump=new Intent(this, AdminJoinTypeActivity.class);
                jump.putExtra("group_id",group_id); //后端一定要做好鉴权
                startActivity(jump);
                break;
            case R.id.manage_theme_color:
                //修改主题色
                ColorPickerDialog.newBuilder().show(this); //居然这么好用，我看了都哭了，这作者，我要给他磕头^ ^
                break;
            case R.id.manage_introduction:
                //修改简介
                builder.setTitle(R.string.manage_group_introduction);
                builder.setView(edit);
                builder.setNegativeButton(R.string.chancel,null);
                builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                    dict.put("introduction",edit.getText().toString());
                    callModifyGroupData(dict);
                });
                builder.show();
                break;
            case R.id.manage_member_name:
                //修改成员名
                builder.setTitle(R.string.manage_group_member_name);
                builder.setView(edit);
                builder.setNegativeButton(R.string.chancel,null);
                builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                    dict.put("member_name",edit.getText().toString());
                    callModifyGroupData(dict);
                });
                builder.show();
                break;
            case R.id.manage_pic:
                //修改小组头像
                ImagePicker picker=ImagePicker.getInstance();
                picker.setImageLoader(new AuroraImageLoader());
                picker.setCrop(true);
                picker.setMultiMode(false);
                picker.setFocusWidth(600);
                picker.setFocusHeight(600);
                startActivityForResult(new Intent(this, ImageGridActivity.class), 1001); //选照片
                break;
            case R.id.resignation:
                //辞职？
                builder.setTitle(R.string.manage_dismiss);
                TextView hint=new TextView(this);
                hint.setGravity(View.TEXT_ALIGNMENT_CENTER);
                hint.setText(R.string.hint_are_you_sure);
                builder.setNegativeButton(R.string.chancel,null);
                builder.setPositiveButton(R.string.ok, (dialog, which) ->
                        AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website) + getString(R.string.backend_admin_dismiss).replace("0", group_id),
                        "GET",
                        null,
                        null,
                        (call, response, tag) -> {
                            OkHttpPackage.showToastResponse(getApplicationContext(),response);
                        },
                        getApplicationContext()
                ));
                builder.setView(hint);
                builder.show();
                break;
            case R.id.manage_log:
                //管理日志
                jump=new Intent(this, AdminLogActivity.class);
                jump.putExtra("group_id",group_id); //后端一定要做好鉴权
                startActivity(jump);
                break;
            case R.id.manage_join_apply:
                //查看加入申请
                jump=new Intent(this, AdminApplyActivity.class);
                jump.putExtra("group_id",group_id); //后端一定要做好鉴权
                startActivity(jump);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //选择头像和背景的回调
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) { //上传回调，插入图片和上传（不整这些虚的了，就直接来吧^ ^）
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            for(ImageItem i:images){
                AuroraApplication.httpPackage.asyncImageRequest(
                        getString(R.string.backend_website) + getString(R.string.backend_upload_image),
                        i,
                        "UPLOAD_IMG",
                        (call, response, tag) -> {
                            HashMap<String,String> dict=new HashMap<>();
                            if(response.code()==200){
                                dict.put("group_pic",response.body().string());
                                callModifyGroupData(dict);
                            }
                        },
                        this
                );
            }
        }
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "GROUP_DATA_MODIFY":
                OkHttpPackage.showToastResponse(this,response);
                break;
            case "ADMIN_MASTER":
                if(response.code()==200){
                    String s=response.body().string();
                    MemberViewHolder.MemberObject ra=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<MemberViewHolder.MemberObject>(){}.getType());
                    this.is_master= ra.member_id.equals(AuroraApplication.userID);
                }else{
                    OkHttpPackage.showToastResponse(this,response);
                }
                break;
        }
    }

    @Override
    public void onColorSelected(int dialogId, int color) { //修改主题颜色
        HashMap<String,String> dict=new HashMap<>();
        dict.put("theme_color",AuroraApplication.shiftColor(color));
        callModifyGroupData(dict);
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        //那也没啥，就是被取消了
    }

    private void callModifyGroupData(HashMap<String,String> dict){
        AuroraApplication.httpPackage.asyncRequest(
            getString(R.string.backend_website)+getString(R.string.backend_change_group_data).replace("0",group_id),
                "POST",
                dict,
                "GROUP_DATA_MODIFY",
                this,
                this
        );
    }
}
