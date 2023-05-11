package indi.augusttheodor.aurora.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.data.AdminJoinTypeObject;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import me.shihao.library.XRadioGroup;
import okhttp3.Call;
import okhttp3.Response;

public class AdminJoinTypeActivity extends AppCompatActivity implements View.OnClickListener , HttpInterface {

    private static final String CONDITION_NO_CONDITION="1"; //直接加入
    private static final String CONDITION_FOLLOWED="2"; //需要被关注数超过
    private static final String CONDITION_APPLY="3"; //申请加入
    private static final String CONDITION_ANSWER="4"; //口令

    private XRadioGroup type_group;
    private String group_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_join_type);
        Intent intent=getIntent();
        this.group_id=intent.getStringExtra("group_id");
        this.type_group=findViewById(R.id.join_type);
        findViewById(R.id.submit).setOnClickListener(this);
        AuroraApplication.httpPackage.asyncRequest(
                getString(R.string.backend_website)+getString(R.string.backend_admin_manage_join_type).replace("0",group_id),
                "GET",
                null,
                "SEEK_JOIN_TYPE",
                this,
                this
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                HashMap<String,String> dict=new HashMap<>();
                switch (this.type_group.getCheckedRadioButtonId()){
                    case R.id.no_condition:
                        dict.put("type",CONDITION_NO_CONDITION);
                        dict.put("content","");
                        break;
                    case R.id.followed:
                        dict.put("type",CONDITION_FOLLOWED);
                        dict.put("content",((TextView)findViewById(R.id.followed_content)).getText().toString());
                        break;
                    case R.id.apply:
                        dict.put("type",CONDITION_APPLY);
                        dict.put("content","");
                        break;
                    case R.id.answer:
                        dict.put("type",CONDITION_ANSWER);
                        dict.put("content",((TextView)findViewById(R.id.answer_content)).getText().toString());
                        break;
                }
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_admin_manage_join_type).replace("0",group_id),
                        "POST",
                        dict,
                        "MANAGE_JOIN_TYPE",
                        this,
                        this
                );
                break;
        }

    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            //这种情况坚持switch的我真是个靓仔- -
            case "MANAGE_JOIN_TYPE":
                OkHttpPackage.showToastResponse(this,response);
                break;
            case "SEEK_JOIN_TYPE":
                if(response.code()==200){
                    AdminJoinTypeObject to=AuroraApplication.httpPackage.gson.fromJson(response.body().string(),new TypeToken<AdminJoinTypeObject>(){}.getType());
                    switch (to.type){
                        case CONDITION_NO_CONDITION:
                            ((RadioButton)findViewById(R.id.no_condition)).setChecked(true);
                            break;
                        case CONDITION_FOLLOWED:
                            ((RadioButton)findViewById(R.id.followed)).setChecked(true);
                            ((EditText)findViewById(R.id.followed_content)).setText(to.content);
                            break;
                        case CONDITION_APPLY:
                            ((RadioButton)findViewById(R.id.apply)).setChecked(true);
                            break;
                        case CONDITION_ANSWER:
                            ((RadioButton)findViewById(R.id.answer)).setChecked(true);
                            ((EditText)findViewById(R.id.answer_content)).setText(to.content);
                            break;
                    }
                }else{
                    OkHttpPackage.showToastResponse(this,response);
                }
                break;
        }
    }
}