package indi.augusttheodor.aurora.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.tools.AuroraImageLoader;
import okhttp3.Call;
import okhttp3.Response;

public class CreateSummaryActivity extends AppCompatActivity implements View.OnClickListener , HttpInterface , View.OnFocusChangeListener {

    private ImageView pic;
    private TextView name;
    private TextView desc;
    private String pic_name="";
    private int summary_type; //文章类型 0公开 1版权 2私密

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_summary);
        this.pic=findViewById(R.id.summary_pic);
        this.name=findViewById(R.id.summary_name);
        this.desc=findViewById(R.id.summary_desc);
        this.pic.setOnClickListener(this);
        findViewById(R.id.create).setOnClickListener(this);
        findViewById(R.id.summary_type).setOnFocusChangeListener(this);
        ((RadioButton)findViewById(R.id.summary_public)).setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.summary_pic:
                ImagePicker picker=ImagePicker.getInstance();
                picker.setImageLoader(new AuroraImageLoader());
                picker.setCrop(true);
                picker.setShowCamera(false);
                picker.setMultiMode(false);
                picker.setFocusWidth(900);
                picker.setFocusHeight(900);
                startActivityForResult(new Intent(this, ImageGridActivity.class), 1001); //选照片
                break;
            case R.id.create:
                HashMap<String,String> dict=new HashMap<>();
                dict.put("name",this.name.getText().toString());
                dict.put("desc",this.desc.getText().toString());
                dict.put("pic",this.pic_name);
                dict.put("type",this.summary_type+"");
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_create_summary),
                        "POST",
                        dict,
                        "SET_SUMMARY",
                        this,
                        this
                );
                break;
            case R.id.finish:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //选择头像和背景的回调
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) { //上传回调，插入图片和上传（不整这些虚的了，就直接来吧^ ^）
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            for(ImageItem i:images){
                this.pic.setImageURI(i.uri);
                AuroraApplication.httpPackage.asyncImageRequest(
                        getString(R.string.backend_website) + getString(R.string.backend_upload_image),
                        i,
                        "UPLOAD_IMG",
                        (call, response, tag) -> {
                            if(response.code()==200){
                                this.pic_name=response.body().string();
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
            case "SET_SUMMARY":
                Looper.prepare();
                Toast.makeText(this, response.body().string(), Toast.LENGTH_SHORT).show();
                if(response.code()==200){this.finish();}
                Looper.loop();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.summary_public:
                this.summary_type=0;
                break;
            case R.id.summary_private:
                this.summary_type=1;
                break;
        }
    }
}