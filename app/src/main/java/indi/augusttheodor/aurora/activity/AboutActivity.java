package indi.augusttheodor.aurora.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import java.net.URL;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        /*findViewById(R.id.pay4).setOnLongClickListener(v -> {
            new Thread(() -> AuroraApplication.DownloadImage(AuroraApplication.image_loader.loadImageSync("drawable://" + R.drawable.pay4),"pay4.png")).start();
            return false;
        });*/
    }

}
