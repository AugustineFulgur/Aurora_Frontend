package indi.augusttheodor.aurora.activity;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;

public class ImageActivity extends AppCompatActivity {

    public static Drawable drawable=null;
    public String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_dialog);
        this.uri=getIntent().getStringExtra("uri");
        ((PhotoView)findViewById(R.id.content)).enable();
        findViewById(R.id.save).setOnLongClickListener(v -> {
            //长按保存
            new Thread(() ->{
                Bitmap bitmap=AuroraApplication.image_loader.loadImageSync(getString(R.string.backend_image)+this.uri);
                AuroraApplication.DownloadImage(bitmap,this.uri);
            }).start();
            return false;
        });
        Glide.with(this).load(ImageActivity.drawable).into((ImageView)findViewById(R.id.content));
    }

}
