package indi.augusttheodor.aurora.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import indi.augusttheodor.aurora.R;

public class WaterMarkUtils{
    //生成全屏水印的类

    public static Drawable createWaterMark(String author_name, Activity activity){
        //创建整个屏幕的水印图片
        DisplayMetrics display=activity.getResources().getDisplayMetrics();
        int width= display.widthPixels;
        int height=display.heightPixels;
        String label=activity.getString(R.string.app_label_name)+author_name;
        Bitmap bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888); //创建画布
        Canvas canvas=new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setColor(activity.getColor(R.color.aurora_watermark));
        paint.setTextSize(45);
        canvas.rotate(15); //转15度吧
        int gap_height=height/15;
        int gap_width=45*label.length()+25; //设置间隔
        for(int x=-width;x<width+500;x+=gap_width){
            for(int y=-height;y<height+500;y+=gap_height){
                //循环绘制水印
                canvas.drawText(label,x,y,paint);
            }
        }
        canvas.save();
        canvas.restore();;
        return new BitmapDrawable(activity.getResources(),bitmap);
    }

}


