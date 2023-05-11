package indi.augusttheodor.aurora.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ArticleDetailObject { //文章详情的实体类

    public String title; //文章标题
    public String content; //详情
    public String image; //图片列表
    public String group; //所属分组id
    public String group_name; //分组名
    public String group_nick; //分组图片
    public String author; //作者id
    public String author_name; //作者名
    public String author_nick; //作者图片
    public String time; //时间
    public String a_prefer; //点赞
    public String a_up; //顶
    public String a_transmit; //转发
    public String a_star; //收藏
    public String a_comment; //评论
    public String safe_type; //安全类型
    public boolean is_prefer; //是否点赞了
    public boolean is_author;
    public boolean is_admin;
    public boolean is_top;
    public boolean is_in_group; //是否在组里
}
