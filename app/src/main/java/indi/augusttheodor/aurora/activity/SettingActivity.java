package indi.augusttheodor.aurora.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.tools.AuroraImageLoader;
import indi.augusttheodor.aurora.tools.AuroraNoBlankFilter;
import indi.augusttheodor.aurora.tools.AuroraTextClickableSpan;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import indi.augusttheodor.aurora.tools.SettingObject;
import okhttp3.Call;
import okhttp3.Response;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById(R.id.exit).setOnClickListener(this);
        findViewById(R.id.night_theme).setOnClickListener(this);
        findViewById(R.id.change_nickname).setOnClickListener(this);
        findViewById(R.id.change_himg).setOnClickListener(this);
        findViewById(R.id.change_bimg).setOnClickListener(this);
        findViewById(R.id.change_signature).setOnClickListener(this);
        findViewById(R.id.about).setOnClickListener(this);
        findViewById(R.id.get_id).setOnClickListener(this);
        findViewById(R.id.change_password).setOnClickListener(this);
        findViewById(R.id.invite).setOnClickListener(this);
        ((SwitchCompat)findViewById(R.id.night_theme)).setChecked(SettingObject.getDark_theme());
    }

    @Override
    public void onClick(View v) {
        EditText edit=new EditText(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        HashMap<String,String> dict=new HashMap<>();
        switch (v.getId()){
            case R.id.get_id:
                ClipboardManager cm=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("AURORA_ID",AuroraApplication.userID));
                Toast.makeText(getApplicationContext(), getString(R.string.hint_clipboard_success), Toast.LENGTH_SHORT).show();
                break;
            case R.id.exit:
                SettingObject.setSession("");
                SettingObject.apply();
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class); //清除session然后跳转到登录界面
                startActivity(intent);
                finish();
                break;
            case R.id.night_theme:
                SettingObject.setDark_theme(((SwitchCompat)findViewById(R.id.night_theme)).isChecked());
                SettingObject.apply();
                Toast.makeText(this, getString(R.string.hint_require_reboot), Toast.LENGTH_SHORT).show();
                break;
            case R.id.change_nickname:
                edit.setFilters(new InputFilter[] {new AuroraNoBlankFilter()});
                builder.setTitle(R.string.setting_change_nickname);
                builder.setNegativeButton(R.string.chancel,null);
                builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                    dict.put("nick_name",edit.getText().toString());
                    callModifyUserData(dict);
                });
                builder.setView(edit);
                builder.show();
                break;
            case R.id.change_himg:
                ImagePicker picker=ImagePicker.getInstance();
                picker.setImageLoader(new AuroraImageLoader());
                picker.setCrop(true);
                picker.setMultiMode(false);
                picker.setStyle(CropImageView.Style.CIRCLE);
                picker.setFocusWidth(360);
                picker.setFocusHeight(360);
                startActivityForResult(new Intent(this, ImageGridActivity.class), 1001); //选照片
                break;
            case R.id.change_bimg:
                picker=ImagePicker.getInstance();
                picker.setImageLoader(new AuroraImageLoader());
                picker.setCrop(true);
                picker.setMultiMode(false);
                picker.setStyle(CropImageView.Style.RECTANGLE);
                picker.setFocusWidth(500);
                picker.setFocusHeight(250);
                startActivityForResult(new Intent(this, ImageGridActivity.class), 1002); //选照片
                break;
            case R.id.change_signature:
                builder.setTitle(R.string.setting_change_signature);
                builder.setNegativeButton(R.string.chancel,null);
                builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                    dict.put("signature",edit.getText().toString());
                    callModifyUserData(dict);
                });
                builder.setView(edit);
                builder.show();
                break;
            case R.id.about:
                //关于页面
                startActivity(new Intent(this,AboutActivity.class));
                break;
            case R.id.change_password:
                //修改密码
                builder.setTitle(R.string.setting_change_password);
                EditText old_password=new EditText(this);
                old_password.setHint(R.string.hint_old_password);
                EditText new_password=new EditText(this);
                new_password.setHint(R.string.hint_new_password);
                builder.setNegativeButton(R.string.chancel,null);
                builder.setPositiveButton(R.string.ok,((dialog, which) -> {
                    dict.put("password",old_password.getText().toString());
                    dict.put("new",new_password.getText().toString());
                    callModifyUserData(dict);
                }));
                LinearLayout layout=new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(old_password);
                layout.addView(new_password);
                builder.setView(layout);
                builder.show();
                break;
            case R.id.invite:
                builder.setTitle(R.string.hint_invite);
                TextView must_understood=new TextView(this);
                must_understood.setText(R.string.invite_rule);
                must_understood.setTextSize(15);
                must_understood.setPadding(20,20,20,20);
                EditText password=new EditText(this);
                password.setHint(R.string.hint_password);
                EditText email=new EditText(this);
                email.setHint(R.string.hint_invite_email);
                builder.setNegativeButton(R.string.chancel,null);
                builder.setPositiveButton(R.string.ok,((dialog, which) -> {
                   dict.put("password",password.getText().toString());
                   dict.put("email",email.getText().toString());
                   AuroraApplication.httpPackage.asyncRequest(
                           getString(R.string.backend_website) + getString(R.string.backend_invite_email),
                           "POST",
                           dict,
                           null,
                           (call, response, tag) -> {
                               OkHttpPackage.showToastResponse(this,response);
                           },
                           this
                   );

                }));
                LinearLayout ll=new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(password);
                ll.addView(must_understood);
                ll.addView(email);
                builder.setView(ll);
                builder.show();
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
                                dict.put(requestCode==1001?"himg":"bimg",response.body().string());
                                callModifyUserData(dict);
                            }
                        },
                        this
                );
            }
        }
    }

    private void callModifyUserData(HashMap<String,String> dict){
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website) + getString(R.string.backend_change_userdata),
                "POST",
                dict,
                null,
                (call, response, tag) -> {
                    //无论如何，都展示
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                },
                getApplicationContext()
        );
    }
}
