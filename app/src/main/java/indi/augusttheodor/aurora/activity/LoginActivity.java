package indi.augusttheodor.aurora.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.tools.SettingObject;
import indi.augusttheodor.aurora.inter.HttpInterface;
import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, HttpInterface {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); //这里设置容器视图为R中的activity_main layout文件
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this); //设置监听器

    }

    @Override
    public void onClick(View view) { //点击的回调函数Log.d("Click","loginClick");
        switch(view.getId()){
            case R.id.login: //按下登陆按键
                HashMap<String,String> map=new HashMap<>();
                map.put("loginname",((EditText)findViewById(R.id.login_name)).getText().toString());
                map.put("password",((EditText)findViewById((R.id.login_password))).getText().toString());
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_login),
                        "POST",
                        map,
                        "LOGIN", //TAG用于onResponse处理
                        this,
                        this
                        );
                break;
            case R.id.register: //注册的时候跳转到注册页面
                startActivity(new Intent(this,RegisterActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch(tag){
            case "LOGIN":
                if (response.code()==200) {//返回正常
                    String session=response.headers().values("Set-Cookie").get(0);
                    SettingObject.setSession(session.substring(0,session.indexOf(";")));
                    SettingObject.apply(); //存session
                    AuroraApplication.userID=AuroraApplication.httpPackage.getJsonKV(response.body().string()).get("id"); //赋值id
                    AuroraApplication.httpPackage.asyncRequest(
                            getString(R.string.backend_website) + getString(R.string.backend_short_user)+AuroraApplication.userID,
                            "GET",
                            null,
                            "SHORT_USER",
                            (HttpInterface) getApplication(),
                            this
                    );
                    startActivity(new Intent(getApplicationContext(),NavigatorActivity.class)); //跳转到APP内部
                    finish(); //销毁本窗口
                }
                else{ //返回有误，显示错误信息
                    Looper.prepare(); //因为要在主线程里显示Toast
                    Toast.makeText(getApplicationContext(),response.body().string(),Toast.LENGTH_SHORT).show(); //设置toast的内容和时间
                    Looper.loop();
                }
                break;
            default:
                break;
        }
    }
}