package indi.augusttheodor.aurora.activity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.HashMap;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.inter.HttpInterface;
import okhttp3.Call;
import okhttp3.Response;

public class CreateTransmitActivity extends AppCompatActivity implements View.OnClickListener, HttpInterface { //只有文章能转发

    private String article_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=this.getIntent().getExtras();
        if(bundle!=null){
            this.article_id=bundle.getString("article_id");
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.hint_set_fail), Toast.LENGTH_SHORT).show();
            finish(); //显示报错并返回
        }
        setContentView(R.layout.activity_create_transmit);
        findViewById(R.id.set_transmit).setOnClickListener(this);
        findViewById(R.id.finish).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_transmit:
                String transmit= ((TextView)findViewById(R.id.transmit_content)).getText().toString();
                HashMap dict=new HashMap<String,String>();
                dict.put("transmit",transmit);
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_article_set_transmit).replace("0",this.article_id),
                        "POST",
                        dict,
                        "SET_TRANSMIT",
                        this,
                        this
                );
                break;
            case R.id.finish:
                this.finish(); //退出
                break;
        }
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "SET_TRANSMIT":
                if (response.code() == 200) {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_transmit_success), Toast.LENGTH_SHORT).show();
                    finish();
                    Looper.loop();
                }else{
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_set_fail), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
        }
    }
}
