package indi.augusttheodor.aurora.tools;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.activity.ChatMessageActivity;
import indi.augusttheodor.aurora.activity.ArticleActivity;
import indi.augusttheodor.aurora.activity.GroupActivity;
import indi.augusttheodor.aurora.activity.SummaryDetailActivity;
import indi.augusttheodor.aurora.activity.UserDetailActivity;
import indi.augusttheodor.aurora.inter.RecyclerViewListenerInterface;

public class ListenerAssemble { //侦听器集合

    public static JumpToArticle jumpToArticle;
    public static JumpToGroup jumpToTopic;
    public static JumpToUser jumpToUser;
    public static JumpToChat jumpToChat;
    public static JumpToSummary jumpToSummary;

    public ListenerAssemble(){ //这个函数在Application中运行
        ListenerAssemble.jumpToArticle=new JumpToArticle();
        ListenerAssemble.jumpToTopic=new JumpToGroup();
        ListenerAssemble.jumpToUser=new JumpToUser();
        ListenerAssemble.jumpToChat=new JumpToChat();
        ListenerAssemble.jumpToSummary=new JumpToSummary();
    }

    public class JumpToArticle implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent jump=new Intent(view.getContext().getApplicationContext(), ArticleActivity.class);
            jump.putExtra("article_id",view.findViewById(R.id.trend_link).getTag().toString());
            view.getContext().startActivity((jump));
        }
    }

    public class JumpToSummary implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent jump=new Intent(view.getContext().getApplicationContext(), SummaryDetailActivity.class);
            jump.putExtra("summary_id",view.findViewById(R.id.summary_link).getTag().toString());
            view.getContext().startActivity((jump));
        }
    }

    public class JumpToGroup implements  View.OnClickListener{ //很想把它和上面封装在一起，可惜，不能带更多参数了

        @Override
        public void onClick(View view) {
            Intent jump=new Intent(view.getContext().getApplicationContext(), GroupActivity.class);
            jump.putExtra("group_id",view.findViewById(R.id.group_link).getTag().toString());
            view.getContext().startActivity((jump));
        }
    }

    public class JumpToUser implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent jump=new Intent(view.getContext().getApplicationContext(), UserDetailActivity.class);
            jump.putExtra("user_id",view.findViewById(R.id.user_link).getTag().toString());
            view.getContext().startActivity((jump));
        }
    }

    public class JumpToChat implements  View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent jump=new Intent(view.getContext().getApplicationContext(), ChatMessageActivity.class);
            jump.putExtra("user_id",view.findViewById(R.id.chat_link).getTag().toString());
            jump.putExtra("user_name",((TextView)view.findViewById(R.id.name)).getText().toString());
            view.getContext().startActivity((jump));
        }
    }


    public static class RecyclerViewToBottom extends RecyclerView.OnScrollListener{ //抄的，懒得想了，这事跟写算法题没啥区别感觉- -

        private RecyclerViewListenerInterface i;

        public RecyclerViewToBottom(RecyclerViewListenerInterface i){
            this.i=i;
        }

        private int findMax(int[] lastPositions) {
            int max = lastPositions[0];
            for (int value : lastPositions) {
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //当前RecyclerView显示出来的最后一个的item的position
            int lastPosition = -1;

            //当前状态为停止滑动状态SCROLL_STATE_IDLE时
            if(newState == RecyclerView.SCROLL_STATE_IDLE){
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if(layoutManager instanceof GridLayoutManager){
                    //通过LayoutManager找到当前显示的最后的item的position
                    lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                }else if(layoutManager instanceof LinearLayoutManager){
                    lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                }else if(layoutManager instanceof StaggeredGridLayoutManager){
                    //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
                    //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
                    int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                    ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
                    lastPosition = findMax(lastPositions);
                }

                //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
                //如果相等则说明已经滑动到最后了
                //滑动到最下面，发起请求
                if(lastPosition == recyclerView.getLayoutManager().getItemCount()-1){
                    i.onNext();
                }

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        }

    }
}
