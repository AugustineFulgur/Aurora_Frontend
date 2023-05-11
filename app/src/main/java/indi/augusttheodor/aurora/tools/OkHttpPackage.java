package indi.augusttheodor.aurora.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.bean.ImageItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.inter.HttpInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class OkHttpPackage {

    public Gson gson;

    public OkHttpPackage(){
        this.gson=new Gson();
    }

    public OkHttpClient createClient(){
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(SSLVerify.getSSLDoubleFactory(AuroraApplication.context), new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                })//获取SSLSocketFactory
                .hostnameVerifier(new TrustCA())//添加hostName验证器
                .build();
    }

    public HashMap<String,String> getJsonKV(String jsonBody){ //传入String获取键值对字典
        return this.gson.fromJson(jsonBody,new TypeToken<HashMap<String,String>>(){}.getType());
    }

    public void asyncRequest(String url, String method, HashMap<String,String> dict, String tag, HttpInterface inter, Context context){ //我本来想再传入一个参数来在这统一做一下请求失败显示，算了
        Request request=null;
        if(method=="GET"){
            String final_url=url;
            if(dict!=null){
                final_url+="?";
                for(String key : dict.keySet()){
                    final_url+=key;
                    final_url+="=";
                    final_url+=dict.get(key);
                    final_url+="&";
                }
                final_url=final_url.substring(0,final_url.length()-1); //切出最后多余的&
            }
            request=new Request.Builder().url(final_url).get().header("cookie", SettingObject.getSession()).build();//构造请求
        }
        else if(method=="POST"){
            MultipartBody.Builder builder=new MultipartBody.Builder().setType(MultipartBody.FORM);
            if(dict!=null){
                for(String key : dict.keySet()){
                    builder.addFormDataPart(key,dict.get(key));
                }
            }
            //构造body
            request=new Request.Builder().url(url).post(builder.build()).header("cookie", SettingObject.getSession()).build();//构造请求
        }
        //请求会默认携带session
        this.call(context,inter,request,tag);
    }

    public WebSocket socketRequest(String url, WebSocketListener inter){ //建立socket，由于这个回调没有强制重载，不需要覆写Interface
        return createClient().newWebSocket(new Request.Builder().url(url).header("cookie", SettingObject.getSession()).build(), inter);
    }

    public void asyncImageRequest(String url, ImageItem image, String tag, HttpInterface inter, Context context){ //压缩并上传图片
        File img=new File(AuroraApplication.urlToPath(image.uri));
        String true_name=image.name == null ?new File(String.valueOf(image.uri)).getName():image.name;
        Luban.with(context).load(AuroraApplication.urlToPath(image.uri)).ignoreBy(100)
                .filter(path -> !(TextUtils.isEmpty(img.getPath())||true_name.toLowerCase().endsWith(".gif")))
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File f) {
                        RequestBody file= RequestBody.create(f,MediaType.parse("image/png"));
                        MultipartBody body=new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("file", true_name, file)
                                .build();
                        Request request=new Request.Builder().url(url).header("cookie", SettingObject.getSession()).post(body).build();
                        call(context,inter,request,tag);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context,context.getString(R.string.hint_compress_fail),Toast.LENGTH_SHORT).show();
                    }
                }).launch();
    }

    private void call(Context context,HttpInterface inter,Request request,String tag){
        Call call=createClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FAILURE", e.getMessage());
                Looper.prepare();
                Toast.makeText(context.getApplicationContext(),"网络有些问题哦？",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                inter.onResponse(call,response,tag); //根据传入的tag决定响应的处理
            }
        });

    }

    public static void showToastResponse(Context context,Response response) throws IOException { //展示返回内容
        Looper.prepare();
        Toast.makeText(context, response.body().string(), Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

}



