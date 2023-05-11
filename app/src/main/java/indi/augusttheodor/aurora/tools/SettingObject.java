package indi.augusttheodor.aurora.tools;

import android.content.SharedPreferences;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;

public class SettingObject { //设置的一个不算bean的bean- -

    private static String string_dark_theme="Aurora_Dark_Theme";
    private static String string_session="Aurora_Session";
    private static String string_notice_number="Aurora_Notice_Number"; //回复等通知的保留条数
    private static String string_sum_hint="Aurora_Sum_Hint"; //通知条数（右上角红点）
    private static String string_message_hint="Aurora_Message_Hint";
    private static String string_reply_hint="Aurora_Reply_Hint";
    private static String string_notice_hint="Aurora_Notice_Hint";
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

    public static void apply(){
        editor.apply(); //一连设置多个内容时一次提交即可，所以分开来
    }

    //Aurora_Dark_Theme
    public static void setDark_theme(boolean b){
        editor.putBoolean(SettingObject.string_dark_theme,b);
    }
    public static Boolean getDark_theme(){
        return pref.getBoolean(SettingObject.string_dark_theme,true); //默认非暗色主题
    }

    //Aurora_Session
    public static void setSession(String s){
        editor.putString(SettingObject.string_session,s);
    }
    public static String getSession(){
        return pref.getString(SettingObject.string_session,"");
    }

    //Aurora_Notice_Number //通知和回复的保留条数是同一个
    public static void setStringNoticeNumber(Integer i){
        editor.putInt(SettingObject.string_notice_number,i);
    }

    public static int getStringNoticeNumber(){
        return pref.getInt(SettingObject.string_notice_number,50); //默认五十条记录
    }

    //Aurora_Sum_Hint
    public static void setSumHint(int s){
        editor.putInt(SettingObject.string_sum_hint,s);
    }

    public static int getSumHint(){
        return pref.getInt(SettingObject.string_sum_hint,0);
    }

    //Aurora_Reply_Hint
    public static void setReplyHint(int s){
        editor.putInt(SettingObject.string_reply_hint,s);
    }

    public static int getReplyHint(){
        return pref.getInt(SettingObject.string_reply_hint,0);
    }

    //Aurora_Message_Hint
    public static void setMessageHint(int s){
        editor.putInt(SettingObject.string_message_hint,s);
    }

    public static int getMessageHint(){
        return pref.getInt(SettingObject.string_message_hint,0);
    }

    //Aurora_Notice_Hint
    public static void setNoticeHint(int s){
        editor.putInt(SettingObject.string_notice_hint,s);
    }

    public static int getNoticeHint(){
        return pref.getInt(SettingObject.string_notice_hint,0);
    }

}
