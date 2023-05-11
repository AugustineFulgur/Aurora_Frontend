package indi.augusttheodor.aurora.activity;

import static android.app.NotificationChannel.DEFAULT_CHANNEL_ID;
import static android.app.PendingIntent.FLAG_ONE_SHOT;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.data.ArticleDetailObject;
import indi.augusttheodor.aurora.data.VersionObject;
import indi.augusttheodor.aurora.tools.HeartBeat;
import indi.augusttheodor.aurora.tools.SettingObject;
import indi.augusttheodor.aurora.inter.HttpInterface;
import okhttp3.Call;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements HttpInterface { //欢迎界面

    Intent intent; //跳转到的页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //这里设置容器视图为R中的activity_main layout文件
        ((TextView)findViewById(R.id.app_name)).setText(getString(R.string.app_name)+" | "+getString(R.string.app_version));
        HashMap<String,String> version_dict=new HashMap<>();
        version_dict.put("version",getString(R.string.app_version));
        AuroraApplication.httpPackage.asyncRequest( //检查版本
                getString(R.string.backend_website)+getString(R.string.backend_version_check),
                "GET",
                version_dict,
                "VERSION_CHECK",
                this,
                this
        );
        //校验session是否有效，无效显示toast，然后还是跳转到登陆
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_login),
                "GET",
                null,
                "LOGIN",
                this,
                this
        );
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch(tag){
            case "LOGIN":
                if(response.code()==200){
                    HashMap<String,String> map=AuroraApplication.httpPackage.getJsonKV(response.body().string());
                    AuroraApplication.userID=map.get("id"); //登陆成功，获取id
                    AuroraApplication.httpPackage.asyncRequest(
                            getString(R.string.backend_website) + getString(R.string.backend_short_user)+AuroraApplication.userID,
                            "GET",
                            null,
                            "SHORT_USER",
                            (HttpInterface) getApplication(),
                            this
                    );
                    this.intent=new Intent(getApplicationContext(),NavigatorActivity.class); //跳转到APP内部
                }
                else{
                    SettingObject.setSession("");
                    SettingObject.apply(); //登陆失效，清除session
                    this.intent=new Intent(getApplicationContext(),LoginActivity.class); //session过期或不存在跳转到登录界面
                }
                Looper.prepare(); //子线程默认不开启Looper，开启Looper以使用Handler
                new Handler().postDelayed(() -> {
                    startActivity(intent); //三秒之后跳转到导航
                    this.finish(); //销毁本窗口
                },1500);
                Looper.loop();
                break;
            case "VERSION_CHECK":
                if(response.code()==200){
                    VersionObject version=AuroraApplication.httpPackage.gson.fromJson(response.body().string(), new TypeToken<VersionObject>() {}.getType());
                    Uri url= Uri.parse(getString(R.string.backend_app).replace("{0}",version.version.replace(".","_"))); //更新url
                    switch (version.code){
                        case 200: //无需更新
                            break;
                        case 201: //强制更新，弹个窗
                            AlertDialog.Builder builder=new AlertDialog.Builder(this);
                            TextView textView=new TextView(this);
                            textView.setText(getString(R.string.version_force_update_content));
                            builder.setTitle(getString(R.string.version_force_update));
                            builder.setView(textView);
                            builder.setNegativeButton(R.string.chancel, (dialog, which) -> {
                                AuroraApplication.exit();
                            });
                            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                                Intent intent201= new Intent();
                                intent201.setAction("android.intent.action.VIEW");
                                intent201.setData(url);
                                startActivity(intent201); //弹个窗去浏览器更新了
                            });
                            break;
                        case 202: //非强制更新，写个通知
                            NotificationManager notice=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            NotificationChannel channel = new NotificationChannel("UPDATE", "UPDATE", NotificationManager.IMPORTANCE_DEFAULT);
                            channel.enableLights(false);
                            channel.enableVibration(false);
                            notice.createNotificationChannel(channel);
                            Intent intent202=new Intent();
                            intent202.setAction("android.intent.action.VIEW");
                            intent202.setData(url);
                            PendingIntent pending;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) { //Android12的分别处理
                                pending = PendingIntent.getActivity(this, 0, intent202, PendingIntent.FLAG_IMMUTABLE);
                            } else {
                                pending = PendingIntent.getActivity(this, 0, intent202, PendingIntent.FLAG_ONE_SHOT);
                            } //毕竟是通知，跨应用的，需要加强版
                            Notification.Builder notice_builder=new Notification.Builder(this,"UPDATE");
                            notice_builder.setContentTitle(getString(R.string.version_update_title));
                            notice_builder.setContentText(getString(R.string.version_update_content).replace("{0}",version.version).replace("{1}",getString(R.string.app_version)));
                            notice_builder.setSmallIcon(R.mipmap.aurora_icon);
                            notice_builder.setContentIntent(pending);
                            notice.notify(1,notice_builder.build());
                            break;
                    }
                }else{
                    Looper.prepare();
                    Toast.makeText(this,getString(R.string.hint_version_wrong),Toast.LENGTH_SHORT).show();
                    AuroraApplication.exit();
                    Looper.loop();
                }
                break;
        }
    }

}