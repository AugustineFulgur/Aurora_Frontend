package indi.augusttheodor.aurora.db;

import com.orm.SugarRecord;

public class DbNoticeReply extends SugarRecord {
    //回复提醒的表
    public String reply; //提醒string
    public Long timestamp;

    public DbNoticeReply(){}

}
