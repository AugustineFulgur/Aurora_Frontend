package indi.augusttheodor.aurora.db;

import com.orm.SugarRecord;

import java.util.Date;

public class DbChatHeader extends SugarRecord {

    public String last_time;
    public String last_message;
    public String author_id; //被私聊者的id，这必得重构
    public String bank_id; //这个私聊属于的帐号，这是用于多账号的设置
    public String author_name;
    public String author_nick;
    public long timestamp; //时间戳
    public int unread;

    public DbChatHeader() { } //Sugar用的

}
