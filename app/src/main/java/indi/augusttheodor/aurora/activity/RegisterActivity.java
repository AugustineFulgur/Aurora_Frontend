package indi.augusttheodor.aurora.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.tools.AuroraNoBlankFilter;
import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, HttpInterface {

    public CheckBox check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewById(R.id.register).setOnClickListener(this); //设置监听器
        this.check=findViewById(R.id.agreement);
        this.check.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.register:
                if(!((TextView)findViewById(R.id.register_password)).getText().toString().equals(((TextView)findViewById(R.id.register_repeat_password)).getText().toString())){
                    Toast.makeText(this,getString(R.string.hint_password_repeat_fail),Toast.LENGTH_SHORT).show();
                }
                if(!this.check.isChecked()){
                    Toast.makeText(getApplicationContext(),getString(R.string.hint_register_agreement),Toast.LENGTH_SHORT).show();
                    //未同意用户协议不行
                    break;
                }
                HashMap<String,String> map=new HashMap<>();
                map.put("loginname",((EditText)findViewById(R.id.register_name)).getText().toString());
                map.put("nickname",((EditText)findViewById(R.id.register_nickname)).getText().toString());
                map.put("password",((EditText)findViewById(R.id.register_password)).getText().toString());
                map.put("invitation",((EditText)findViewById(R.id.register_invitation)).getText().toString());
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_register),
                        "POST",
                        map,
                        "REGISTER", //TAG用于onResponse处理
                        this,
                        this
                );
                break;
            case R.id.agreement:
                if(!this.check.isChecked()){return;} //取消的时候不展示
                ScrollView container=new ScrollView(this);
                TextView text=new TextView(this);
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website) + getString(R.string.backend_agreement),
                        "GET",
                        null,
                        null,
                        (call, response, tag) -> {
                            if(response.code()==200){
                                text.setText(response.body().string());
                                text.setTextSize(15);
                                container.addView(text);
                                LinearLayout layout=new LinearLayout(this); //也不知道该说什么，者还不如多写个布局文件，真的
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.addView(container);
                                LinearLayout.LayoutParams para=((LinearLayout.LayoutParams)container.getLayoutParams());
                                int dp=AuroraApplication.dp2px(this,10);
                                para.setMargins(dp,dp,dp,dp);
                                container.setLayoutParams(para);
                                builder.setView(layout);
                                builder.setTitle(getString(R.string.agreement_title));
                                builder.setNegativeButton(R.string.chancel,((dialog, which) -> this.check.setChecked(false)));
                                builder.setPositiveButton(R.string.ok,((dialog, which) -> {
                                    ((CheckBox)findViewById(R.id.agreement)).setChecked(true);
                                }));
                                Looper.prepare();
                                builder.show();
                            }else{
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(),response.body().string(),Toast.LENGTH_SHORT).show(); //返回报错
                            }
                            Looper.loop();
                        },
                        this
                );
                break;
        }
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        if(response.code()!=200){
            Looper.prepare();
            Toast.makeText(getApplicationContext(),response.body().string(),Toast.LENGTH_SHORT).show(); //返回报错
            Looper.loop();
        }
        else{
            Looper.prepare();
            Toast.makeText(getApplicationContext(),getString(R.string.hint_register_success),Toast.LENGTH_SHORT).show();
            this.finish();
            Looper.loop();
        }
    }
}