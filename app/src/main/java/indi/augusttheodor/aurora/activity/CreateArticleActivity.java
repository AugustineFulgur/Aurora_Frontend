package indi.augusttheodor.aurora.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import indi.augusttheodor.aurora.tools.ContentImageListener;
import okhttp3.Call;
import okhttp3.Response;

public class CreateArticleActivity extends AppCompatActivity implements View.OnClickListener, HttpInterface , RadioGroup.OnCheckedChangeListener {

    private String group_id;
    private String group_name;
    private EditText content;
    private ArrayList<String> content_images; //图片集合，如果最后放弃编辑要全部删除
    private int article_type; //文章类型 0公开 1版权 2私密

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);
        Bundle bundle=this.getIntent().getExtras();
        if(bundle!=null){
            this.group_id=bundle.getString("group_id");
            this.group_name=bundle.getString("group_name");
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.hint_set_fail), Toast.LENGTH_SHORT).show();
            finish(); //显示报错并返回
        }
        this.content=findViewById(R.id.article_content);
        this.content_images=new ArrayList<>();
        this.content.addTextChangedListener(new ContentImageListener(this,content_images,this.content)); //改变时监听
        findViewById(R.id.pic).setOnClickListener(this);
        findViewById(R.id.set_article).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);
        ((RadioGroup)findViewById(R.id.article_type)).setOnCheckedChangeListener(this);
        ((TextView)findViewById(R.id.group_name)).setText(group_name);
        ((RadioButton)findViewById(R.id.article_public)).setChecked(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pic:
                ImagePicker picker=ImagePicker.getInstance();
                picker.setImageLoader(new AuroraImageLoader());
                picker.setCrop(false);
                picker.setShowCamera(false);
                picker.setSelectLimit(9);
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, 1009);
                break;
            case R.id.set_article:
                HashMap dict=new HashMap<String,String>();
                dict.put("title",((TextView)findViewById(R.id.article_title)).getText().toString());
                dict.put("content",((TextView)findViewById(R.id.article_content)).getText().toString());
                dict.put("image",this.content_images.toString());
                dict.put("type",this.article_type+"");
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_group_set_article).replace("0",group_id),
                        "POST",
                        dict,
                        "SET_ARTICLE",
                        this,
                        this
                );
                break;
            case R.id.exit:
                this.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 1009) { //上传回调，插入图片和上传
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                for(ImageItem i:images){
                    if(i==null){return;}
                    AuroraApplication.httpPackage.asyncImageRequest(
                            getString(R.string.backend_website)+getString(R.string.backend_upload_image),
                            i,
                            "UPLOAD_IMG",
                            this,
                            this
                    );
                }
            }
        }
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "UPLOAD_IMG": //上传图片成功之后回调，在客户端显示为Span
                if(response.code()==200){
                    String url=response.body().string();
                    this.content_images.add(url); //添加进入列表
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_upload_success), Toast.LENGTH_SHORT).show();
                    SpannableString ss=new SpannableString(getString(R.string.image_span));
                    //读取图片以及更新
                    new Thread(() -> {
                        Bitmap bitmap=AuroraApplication.image_loader.loadImageSync(getString(R.string.backend_image)+url); //下载图片
                        runOnUiThread(() -> {
                            Drawable drawable=new BitmapDrawable(getResources(),bitmap);
                            drawable.setBounds(0, 30, Math.min(content.getWidth(), drawable.getIntrinsicWidth()), content.getWidth()<drawable.getIntrinsicWidth()?drawable.getIntrinsicHeight()*content.getWidth()/drawable.getIntrinsicWidth(): drawable.getIntrinsicHeight()+30);
                            ImageSpan span=new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE);
                            int index=content.getSelectionStart(); //获取光标位置
                            ss.setSpan(span,0,getString(R.string.image_span).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            content.getText().insert(index,ss); //插入到光标
                            content.setSelection(content.getText().length());
                        });
                    }).start();

                }else{
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_upload_fail), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                break;
            case "SET_ARTICLE":
                if(response.code()==200){
                    this.finish();
                    //发送成功，返回
                }
                Looper.prepare();
                Toast.makeText(getApplicationContext(),response.body().string(), Toast.LENGTH_SHORT).show();
                Looper.loop();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.article_public:
                this.article_type=0;
                break;
            case R.id.article_protected:
                this.article_type=1;
                break;
            case R.id.article_private:
                this.article_type=2;
                break;
        }
    }
}
