package indi.augusttheodor.aurora.tools;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.lzy.imagepicker.loader.ImageLoader;
import java.io.File;
import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;

public class AuroraImageLoader implements ImageLoader {


    @Override
    public void displayImage(Activity activity, Uri uri, ImageView imageView, int width, int height) {
        AuroraApplication.image_loader.displayImage(String.valueOf(uri),imageView,AuroraApplication.short_memory_opt);
    }

    @Override
    public void clearMemoryCache() {

    }
}
