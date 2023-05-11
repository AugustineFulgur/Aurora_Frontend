package indi.augusttheodor.aurora.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;

import androidx.annotation.NonNull;

import indi.augusttheodor.aurora.activity.ImageActivity;

public class AuroraImageClickableSpan extends ImageSpan {

    public static int ALIGN_CENTER_HORIZONTAL=3;

    public String uri;
    public Context context;
    public Drawable drawable;

    public AuroraImageClickableSpan(@NonNull Drawable drawable,int i,String uri,Context context) {
        super(drawable,i);
        this.context=context;
        this.uri=uri;
    }

    public void onClick(@NonNull View widget){
        Intent image_activity=new Intent(context, ImageActivity.class);
        ImageActivity.drawable=getDrawable();
        image_activity.putExtra("uri",uri);
        context.startActivity(image_activity);
    }

}
