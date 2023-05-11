package indi.augusttheodor.aurora.db;

import com.orm.SugarRecord;

import java.util.Date;

public class DbChatMessage extends SugarRecord {

    public String timestamp;
    public String author_id;
    public String chat_id; //如果是私聊就是被私聊者的id
    public String bank_id;
    public String content;
    public String img;
    public String time;

    public DbChatMessage(){}

}
