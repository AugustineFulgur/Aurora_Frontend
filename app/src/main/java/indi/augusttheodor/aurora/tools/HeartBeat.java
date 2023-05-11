package indi.augusttheodor.aurora.tools;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.orm.SugarRecord;
import com.orm.query.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;

import indi.augusttheodor.aurora.activity.NavigatorActivity;
import indi.augusttheodor.aurora.activity.NoticeActivity;
import indi.augusttheodor.aurora.data.NoticeLogViewHolder;
import indi.augusttheodor.aurora.data.NoticeReplyViewHolder;
import indi.augusttheodor.aurora.db.DbChatHeader;
import indi.augusttheodor.aurora.db.DbChatMessage;
import indi.augusttheodor.aurora.db.DbNoticeLog;
import indi.augusttheodor.aurora.db.DbNoticeReply;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class HeartBeat extends WebSocketListener {

    public static Context context; //这个是封装包的问题，随便用个context就行，hh
    public static HeartBeat singleton; //is a 单例

    public static int q_reply=0;
    public static int q_message=0;
    public static int q_notice=0; //三种消息的数量
    public static int sum=0;

    public static  WebSocket webSocket; //socket链接

    public static List<NoticeReplyViewHolder.NoticeReplyObject> reply_set;
    public static View.OnClickListener v=v->{
        //notice\new_notice专用onclick
        HeartBeat.sum=0; //清空
        SettingObject.setSumHint(0); //清空
        SettingObject.apply();
        sendSumBoardCasted(v.getContext());
        v.getContext().startActivity(new Intent(v.getContext(), NoticeActivity.class));
    };

    public static void changeMessage(int num){
        HeartBeat.q_message+=num;
        SettingObject.setMessageHint(HeartBeat.q_message);
        Intent intent=new Intent("HEARTBEAT");
        intent.putExtra("message",HeartBeat.q_message);
        context.sendBroadcast(intent); //更新私信计数
    }

    @SuppressLint("WrongConstant")
    public static synchronized HeartBeat getInstance(Context context){
        if(singleton==null){
            HeartBeat.q_reply=SettingObject.getReplyHint();
            HeartBeat.q_message=SettingObject.getMessageHint();
            HeartBeat.q_notice=SettingObject.getNoticeHint(); //读取设置
            HeartBeat.sum=HeartBeat.q_reply+HeartBeat.q_notice+HeartBeat.q_message;
            sendSumBoardCasted(AuroraApplication.context);
            HeartBeat.singleton=new HeartBeat();
            HeartBeat.context=context;
            HeartBeat.reply_set=new ArrayList<>();
            singleton.run();
        }
        return singleton;
    }

    private void run(){
        HeartBeat.webSocket=AuroraApplication.httpPackage.socketRequest(
                context.getString(R.string.backend_socket),
                this
        ); //建立socket
        new Thread() {
            @Override
            public void run() {
                super.run();
                while(true){
                    try {
                        webSocket.send("HEARTBEAT"); //发送心跳包，hh
                        Thread.sleep(500);//休眠
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {

    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        Log.d("socket","onMessage bytes="+bytes);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        if(text.equals("")){
            //一般这个出问题属于登录失效了
            Looper.prepare();
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.hint_sign_status_fail), Toast.LENGTH_SHORT).show();
            Looper.loop();
            SettingObject.setSession(""); //清空session
            SettingObject.apply();
            context.startActivity(new Intent(context.getApplicationContext(), NavigatorActivity.class)); //跳转到登录界面
        }else{ //处理心跳包中的所有数据
            HeartBeatObject o=AuroraApplication.httpPackage.gson.fromJson(text, new TypeToken<HeartBeatObject>() {}.getType());
            //回复的信息
            List<NoticeReplyViewHolder.NoticeReplyObject> reply_object=AuroraApplication.httpPackage.gson.fromJson(o.comment, new TypeToken<List<NoticeReplyViewHolder.NoticeReplyObject>>() {}.getType());
            for(NoticeReplyViewHolder.NoticeReplyObject ro:reply_object){
                DbNoticeReply nr=new DbNoticeReply();
                nr.reply=AuroraApplication.httpPackage.gson.toJson(ro);
                nr.timestamp=new Date().getTime();
                nr.save();
                HeartBeat.q_reply+=1;
                HeartBeat.sum+=1;
            }
            //私信的信息
            List<DbChatMessage> message_object=AuroraApplication.httpPackage.gson.fromJson(o.message,new TypeToken<List<DbChatMessage>>(){}.getType());
            for(DbChatMessage mo:message_object){
                mo.time=AuroraApplication.shiftTime(mo.time); //修整一下
                if(SugarRecord.find(DbChatHeader.class,"authorId = ? and bankId = ?",mo.author_id,AuroraApplication.userID).size()==0) { //没有对应头
                    //无头信息，写入
                    DbChatHeader ho = new DbChatHeader();
                    ho.author_id=mo.author_id;
                    ho.last_message=mo.content;
                    ho.last_time=mo.time;
                    ho.timestamp=new Date().getTime();
                    ho.unread=1;
                    ho.bank_id=AuroraApplication.userID; //所属帐号为本机
                    ho.save();
                    AuroraApplication.httpPackage.asyncRequest(
                            context.getString(R.string.backend_website) + context.getString(R.string.backend_short_user)+mo.author_id,
                            "GET",
                            null,
                            "SHORT_USER",
                            (call, response, tag) -> {
                                String s=response.body().string();
                                ShortUserObject user=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<ShortUserObject>(){ }.getType());
                                ho.author_name=user.author_name;
                                ho.author_nick=user.author_nick;
                                ho.save();
                            },
                            context
                    );
                }else{
                    DbChatHeader ho= Select.from(DbChatHeader.class).where("authorId = ? and bankId = ?",new String[]{mo.author_id,AuroraApplication.userID}).list().get(0);
                    ho.last_message=mo.content;
                    ho.last_time=mo.time; //更新信息和时间
                    ho.unread=ho.unread+1;
                    ho.save();
                }
                mo.bank_id=AuroraApplication.userID;
                mo.timestamp= String.valueOf(new Date().getTime());
                mo.save();
                HeartBeat.q_message+=1;
                HeartBeat.sum+=1;
            }
            //通知的信息
            List<NoticeLogViewHolder.NoticeLogObject> notice_object=AuroraApplication.httpPackage.gson.fromJson(o.notice,new TypeToken<List<NoticeLogViewHolder.NoticeLogObject>>(){}.getType());
            for(NoticeLogViewHolder.NoticeLogObject lo:notice_object){
                DbNoticeLog nl=new DbNoticeLog();
                nl.log=AuroraApplication.httpPackage.gson.toJson(lo);
                nl.timestamp=new Date().getTime();
                nl.save();
                HeartBeat.q_notice+=1;
                HeartBeat.sum+=1;
            }
            //在这里刷新显示
            SettingObject.setSumHint(HeartBeat.sum);
            SettingObject.setReplyHint(HeartBeat.q_reply);
            SettingObject.setMessageHint(HeartBeat.q_message);
            SettingObject.setNoticeHint(HeartBeat.q_notice);
            SettingObject.apply(); //提交修改
            sendSumBoardCasted(AuroraApplication.context);
        }

    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        Log.d("socket","onFailure t="+t);
    }

    public static boolean createHeaderPref(String author_id,String content,String time,int read){ //写入一个新的Header并创建对应的文件
        if(SugarRecord.find(DbChatHeader.class,"authorId = ? and bankId = ?",author_id,AuroraApplication.userID).size()==0) { //没有对应头
            //无头信息，写入
            DbChatHeader ho = new DbChatHeader();
            ho.author_id=author_id;
            ho.last_message=content;
            ho.last_time=time;
            ho.timestamp=new Date().getTime();
            ho.unread=0;
            ho.bank_id=AuroraApplication.userID; //所属帐号为本机
            AuroraApplication.httpPackage.asyncRequest(
                    context.getString(R.string.backend_website) + context.getString(R.string.backend_short_user)+author_id,
                    "GET",
                    null,
                    "SHORT_USER",
                    (call, response, tag) -> {
                        String s=response.body().string();
                        ShortUserObject user=AuroraApplication.httpPackage.gson.fromJson(s,new TypeToken<ShortUserObject>(){ }.getType());
                        List<DbChatHeader> headers=SugarRecord.find(DbChatHeader.class,"authorId = ? ",user.author_id);
                        DbChatHeader ho2= headers.get(0);
                        ho.author_name=user.author_name;
                        Log.d("USER_SHORT", user.author_name);
                        ho.author_nick=user.author_nick;
                        ho.save();
                    },
                    context
            );
            return true;
        }
        return false;
    }

    public static void sendSumBoardCasted(Context context){
        Intent intent=new Intent("HEARTBEAT");
        intent.putExtra("sum",HeartBeat.sum);
        context.sendBroadcast(intent);
    }

    private class HeartBeatObject{ //心跳包总类的Object
        public String comment;
        public String message;
        public String notice;
    }

    public class ShortUserObject {
        public String author_nick;
        public String author_id;
        public String author_name;
    }

}
