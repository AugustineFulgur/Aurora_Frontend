package indi.augusttheodor.aurora.db;

import com.orm.SugarRecord;

public class DbNoticeLog extends SugarRecord {
    //消息通知的表

    public String log;
    public Long timestamp;

    public DbNoticeLog(){}

}
