package indi.augusttheodor.aurora;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.orm.SugarApp;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import indi.augusttheodor.aurora.db.DbChatHeader;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.tools.AuroraImageClickableSpan;
import indi.augusttheodor.aurora.tools.AuroraImageLoader;
import indi.augusttheodor.aurora.tools.AuroraTextClickableSpan;
import indi.augusttheodor.aurora.tools.ImageClickableMovementMethod;
import indi.augusttheodor.aurora.tools.ListenerAssemble;
import indi.augusttheodor.aurora.tools.SettingObject;
import indi.augusttheodor.aurora.tools.OkHttpPackage;
import okhttp3.Call;
import okhttp3.Response;

public class AuroraApplication extends SugarApp implements HttpInterface { //储存全局变量

    public static OkHttpPackage httpPackage;
    public static String userID; //用户ID，全局数据
    public static String userNick; //用户头像
    public static DisplayImageOptions.Builder options; //主要的全局设置，未Build
    public static DisplayImageOptions short_memory_opt; //保存在缓存中的图片，定期清除
    public static DisplayImageOptions long_memory_opt; //保存在存储中的图片，比较常用
    public static DisplayImageOptions r_long_rect_opt; //圆角
    public static ImageLoader image_loader; //图片加载器
    public static Context context;

    public static String shiftString(String s){ //格式化URL
        s=s.replaceAll("\\\\", Matcher.quoteReplacement(File.separator));
        s=s.replaceAll("/",Matcher.quoteReplacement(File.separator));
        return s;
    }

    public static String autoEllipsis(String s,int max){ //自动缩写
        if(s.length()<max){
            return s;
        }else{
            return s.substring(0,max)+"...";
        }
    }

    public static ArrayList<String> shiftList(String s){ //String转List
        if(s==null){
            return null;
        }
        if(s.length()<=2){
            return null;
        }
        ArrayList<String> a=new ArrayList<>();
        s=s.substring(1,s.length()-1);
        s=s.replaceAll(" ","");
        for(String i:s.split(",")){
            a.add(i);
        }
        return a;
    }

    public static String shiftTime(String s){ //格式化时间
        String m=s.replace("T"," ");
        String[] a=m.split("\\.");
        return a[0];
    }

    public static String shiftImg(String s, Context context){ //替换图片Span的内容
        return s.replace(context.getString(R.string.image_span),context.getString(R.string.image_content));
    }

    public static String shiftColor(int color){ //转换颜色的类型
        String s = "#";
        int colorStr = (color & 0xff000000) | (color & 0x00ff0000) | (color & 0x0000ff00) | (color & 0x000000ff);
        s = s + Integer.toHexString(colorStr);
        return s;
    }

    public static int getDpHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return (int) (dm.heightPixels / dm.density);// 屏幕高度(dp)
    }

    public static int dp2px(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void DownloadImage(Bitmap bitmap,String name){
        //保存图片到相册
        try{
            File f=context.getExternalFilesDir(Environment.DIRECTORY_PICTURES); //获取图片路径
            File pic=new File(f.getPath()+"/"+name);
            Bitmap.CompressFormat format;
            FileOutputStream st=new FileOutputStream(pic);
            format= Bitmap.CompressFormat.PNG; //统一保存为PNG图片
            bitmap.compress(format,100,st); //保存图片
            st.flush();
            st.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(),pic.getAbsolutePath(),name,null); //写入相册
            Looper.prepare();
            Toast.makeText(context,context.getString(R.string.content_download_success_hint),Toast.LENGTH_SHORT).show(); //保存完毕
            Looper.loop();
        } catch (FileNotFoundException e) {
            Looper.prepare();
            Toast.makeText(context,context.getString(R.string.content_pic404_hint),Toast.LENGTH_SHORT).show(); //保存完毕
            Looper.loop();
        } catch (IOException e) {
            Looper.prepare();
            Toast.makeText(context,context.getString(R.string.content_io_fail_hint),Toast.LENGTH_SHORT).show(); //保存完毕
            Looper.loop();
        }
    }

    public static void setCommentSpan(String img,TextView container,String content,Context context,Activity activity){ //专为评论区打造^ ^
        container.setMovementMethod(ImageClickableMovementMethod.getInstance()); //设置超链接生效、设置ImageSpan点击事件生效
        Pattern pt=Pattern.compile("\\[(.*:.*):(.{36})\\]");
        Matcher matcher=pt.matcher(content);
        List<String> name=new ArrayList<>();
        List<String> id=new ArrayList<>(); //存span数据
        List<Integer> start=new ArrayList<>();
        List<Integer> end=new ArrayList<>();
        while(matcher.find()){
            name.add(matcher.group(1));
            id.add(matcher.group(2)); //查找到对应的组
            start.add(matcher.start(1)-1);
            end.add(matcher.end(1)+1);
            content=content.replace(content.substring(matcher.start(2)-1,matcher.end(2)),""); //删除后面的部分
        }
        SpannableString ss=new SpannableString(content);
        for(int i=0;i<name.size();i++){
            ss.setSpan(new AuroraTextClickableSpan(id.get(i),context,name.get(i)),start.get(i),end.get(i), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //@部分完毕
        if(img!=null){
            new Thread(() -> { //图片处理
                AuroraImageClickableSpan span1=null;
                if(img.contains(".gif")){
                    //为GIF图片
                    GifDrawable drawable = null;
                    try {
                        drawable=Glide.with(context).asGif().load(context.getString(R.string.backend_image)+img).submit().get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    drawable.setBounds(0, 30, Math.min(container.getWidth(), drawable.getIntrinsicWidth()), container.getWidth()<drawable.getIntrinsicWidth()?drawable.getIntrinsicHeight()*container.getWidth()/drawable.getIntrinsicWidth(): drawable.getIntrinsicHeight()+30);
                    span1 =new AuroraImageClickableSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE,img,context);
                }else{
                    Drawable drawable=null;
                    try {
                        drawable=Glide.with(context).asDrawable().load(context.getString(R.string.backend_image)+img).submit().get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    drawable.setBounds(0, 30, Math.min(container.getWidth(), drawable.getIntrinsicWidth()), container.getWidth()<drawable.getIntrinsicWidth()?drawable.getIntrinsicHeight()*container.getWidth()/drawable.getIntrinsicWidth(): drawable.getIntrinsicHeight()+30);
                    span1 =new AuroraImageClickableSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE,img,context);
                }
                ss.setSpan(span1,ss.length()-1,ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                activity.runOnUiThread(()->container.setText(ss));
            }).start();
        }else{
            container.setText(ss);
        }
    }

    public static void setImgSpan(String img,TextView container, String content, Context context, Activity activity) { //写@
        container.setMovementMethod(ImageClickableMovementMethod.getInstance()); //设置超链接生效、设置ImageSpan点击事件生效
        Pattern pt=Pattern.compile("\\[(.*:.*):(.{36})\\]");
        Matcher matcher=pt.matcher(content);
        List<String> name=new ArrayList<>();
        List<String> id=new ArrayList<>(); //存span数据
        List<Integer> start=new ArrayList<>();
        List<Integer> end=new ArrayList<>();
        while(matcher.find()){
            name.add(matcher.group(1));
            id.add(matcher.group(2)); //查找到对应的组
            start.add(matcher.start(1)-1);
            end.add(matcher.end(1)+1);
            content=content.replace(content.substring(matcher.start(2)-1,matcher.end(2)),""); //删除后面的部分
        }
        SpannableString ss=new SpannableString(content);
        for(int i=0;i<name.size();i++){
            ss.setSpan(new AuroraTextClickableSpan(id.get(i),context,name.get(i)),start.get(i),end.get(i), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        //@部分完毕
        List<String> image=AuroraApplication.shiftList(img);
        if(image==null){
            container.setText(ss); //无图片，直接返回
        }else{
            String finalContent = content;
            new Thread(() -> {
                int index=0;
                for(int i=0;i<image.size();i++){
                    String uri=image.get(i);
                    AuroraImageClickableSpan span1=null;
                    if(uri.contains(".gif")){
                        //为GIF图片
                        GifDrawable drawable = null;
                        try {
                            drawable=Glide.with(context).asGif().load(context.getString(R.string.backend_image)+image.get(i)).submit().get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(drawable==null){continue;}
                        drawable.setBounds(0, 30, Math.min(container.getWidth(), drawable.getIntrinsicWidth()), container.getWidth()<drawable.getIntrinsicWidth()?drawable.getIntrinsicHeight()*container.getWidth()/drawable.getIntrinsicWidth(): drawable.getIntrinsicHeight()+30);
                        span1 =new AuroraImageClickableSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE,uri,context);
                    }else{
                        Drawable drawable=null;
                        try {
                            drawable=Glide.with(context).asDrawable().load(context.getString(R.string.backend_image)+image.get(i)).submit().get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(drawable==null){continue;}
                        drawable.setBounds(0, 30, Math.min(container.getWidth(), drawable.getIntrinsicWidth()), container.getWidth()<drawable.getIntrinsicWidth()?drawable.getIntrinsicHeight()*container.getWidth()/drawable.getIntrinsicWidth(): drawable.getIntrinsicHeight()+30);
                        span1 =new AuroraImageClickableSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE,uri,context);
                    }
                    if(index==0){
                        index= finalContent.indexOf(context.getString(R.string.image_span));
                    }else{
                        index= finalContent.indexOf(context.getString(R.string.image_span), index);
                    }
                    if(index!=-1){
                        ss.setSpan(span1,index,index+context.getString(R.string.image_span).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        index+=context.getString(R.string.image_span).length();
                    }
                }
                //img部分完毕，但是由于是异步的动作（需要加载），后续在线程里完成
                activity.runOnUiThread(() -> container.setText(ss));
            }).start();
        }
    }

    @Override
    public void onCreate(){ //初始化设置
        super.onCreate();
        String process_name=getProcessName();
        CrashReport.initCrashReport(getApplicationContext(), getString(R.string.bugly_id), false); //开启Bugly
        //起先我不明白，为什么一个switch可以解决的事情非要无限if else，直到我想起了equals- -
        if(process_name.equals("indi.augusttheodor.aurora")){ //主进程，初始化配置读取
            //初始化
            AuroraApplication.context=getApplicationContext();
            AuroraApplication.httpPackage=new OkHttpPackage(); //初始化HTTP封装包
            AuroraApplication.image_loader=ImageLoader.getInstance(); //这是单例吧？
            //初始化文件等的读取器
            SettingObject.pref=this.getSharedPreferences(getResources().getString(R.string.file_setting_preference),MODE_PRIVATE);
            SettingObject.editor=SettingObject.pref.edit(); //设置文件的读取器
            AppCompatDelegate.setDefaultNightMode(SettingObject.getDark_theme()?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);//设置颜色主题
            //初始化监听器集合
            new ListenerAssemble();
            initImageLoader();
        }

    }

    public static ImagePicker pickerPic(int number){ //自选数量
        ImagePicker picker=ImagePicker.getInstance();
        picker.setImageLoader(new AuroraImageLoader());
        picker.setCrop(true);
        picker.setShowCamera(false);
        picker.setSelectLimit(number);
        return picker;
    }

    private void initImageLoader(){
        AuroraApplication.options=new DisplayImageOptions.Builder()
                .cacheInMemory(true) //保存在内存中
                .showImageOnLoading(R.mipmap.history) //加载中显示图片
                .showImageOnFail(R.mipmap.history); //加载失败显示图片;
        AuroraApplication.r_long_rect_opt=new DisplayImageOptions.Builder()
                .cacheInMemory(true) //保存在内存中
                .showImageOnLoading(R.mipmap.history) //加载中显示图片
                .showImageOnFail(R.mipmap.history)
                .displayer(new RoundedBitmapDisplayer(40))
                .cacheInMemory(false)
                .cacheOnDisk(true) //长存储
                .build();
        AuroraApplication.short_memory_opt=new DisplayImageOptions.Builder()
                .cacheInMemory(true) //保存在内存中
                .showImageOnLoading(R.mipmap.history) //加载中显示图片
                .showImageOnFail(R.mipmap.history)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .build();
        AuroraApplication.long_memory_opt=new DisplayImageOptions.Builder()
                .cacheInMemory(true) //保存在内存中
                .showImageOnLoading(R.mipmap.history) //加载中显示图片
                .showImageOnFail(R.mipmap.history)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration conf=new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(10) //线程池加载数量
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //图片加密
                .build(); //全局设置
        ImageLoader.getInstance().init(conf);
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "SHORT_USER":
                String s=response.body().string();
                DbChatHeader ho=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<DbChatHeader>(){}.getType());
                AuroraApplication.userNick=ho.author_nick;
        }
    }

    public static String urlToPath(Uri uri){ //Uri获取路径
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] projection = {MediaStore.Images.ImageColumns.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static void exit(){ //结束自身进程
        //先让app进入后台
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        //调用系统API结束进程
        android.os.Process.killProcess(android.os.Process.myPid());
        //结束整个虚拟机进程，注意如果在manifest里用android:process给app指定了不止一个进程，则只会结束当前进程
        System.exit(0);
    }

    public static class Constants {
        //存常量
        public static final String Group="分组";
        public static final String Article="帖子";
        public static final String User="用户";
        public static final String Comment="评论";
        public static final String Transmit="转发";
        public static final String Trend="动态";
        public static final String Summary="收藏";

        public static final String Type_Public="0"; //公开
        public static final String Type_Protected="1"; //版权
        public static final String Type_Private="2"; //私密
    }

}
