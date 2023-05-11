package indi.augusttheodor.aurora.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.reflect.TypeToken;
import com.orm.SugarRecord;
import com.orm.query.Select;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import indi.augusttheodor.aurora.AuroraApplication;
import indi.augusttheodor.aurora.R;
import indi.augusttheodor.aurora.adapter.NoticeMessageAdapter;
import indi.augusttheodor.aurora.db.DbChatHeader;
import indi.augusttheodor.aurora.db.DbChatMessage;
import indi.augusttheodor.aurora.inter.HttpInterface;
import indi.augusttheodor.aurora.inter.AdapterItems;
import indi.augusttheodor.aurora.tools.HeartBeat;
import okhttp3.Call;
import okhttp3.Response;

public class ChatMessageActivity extends AppCompatActivity implements View.OnClickListener , HttpInterface , SwipeRefreshLayout.OnRefreshListener , PopupMenu.OnMenuItemClickListener {

    public String user_id;
    public String user_name;
    private Iterator<DbChatMessage> message_list;
    private NoticeMessageAdapter adapter;
    private AdapterItems<DbChatMessage> items;
    private List<DbChatHeader> header_record;
    private SwipeRefreshLayout swipe;
    private RecyclerView recycler;
    private int p=0;
    private DbChatHeader header;

    @Override
    @SuppressLint({"WrongConstant", "ResourceAsColor"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        //获取传值
        Intent intent=getIntent();
        this.user_id=intent.getStringExtra("user_id");
        this.user_name=intent.getStringExtra("user_name");
        this.items=new AdapterItems<>();
        this.header_record=SugarRecord.find(DbChatHeader.class,"authorId = ? and bankId = ?",user_id,AuroraApplication.userID);
        this.header=header_record.get(0);
        this.header.unread=0; //清空未读消息
        this.header.save();
        this.adapter=new NoticeMessageAdapter(this.items,this,this.header);
        this.recycler= findViewById(R.id.recycle);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager); //设置RecyclerView的布局为流布局
        recycler.setAdapter(this.adapter);
        this.swipe=findViewById(R.id.swipe);
        this.swipe.setColorSchemeColors(R.color.aurora_theme);
        this.swipe.setOnRefreshListener(this);
        ViewGroup.LayoutParams p=this.swipe.getLayoutParams();
        p.height=(int)(AuroraApplication.dp2px(this,(int)(AuroraApplication.getDpHeight(this)-125)));
        this.swipe.setLayoutParams(p);
        //重设聊天框的高度
        List<DbChatMessage> h=Select.from(DbChatMessage.class).list();
        this.message_list=Select.from(DbChatMessage.class).where("chatId = ? and bankId = ?", new String[]{user_id,header.bank_id}).orderBy("time DESC").iterator(); //我直接迭代器，呵呵
        onNext();
        ((TextView)findViewById(R.id.user_name)).setText(user_name);
        findViewById(R.id.set_message).setOnClickListener(this);
        findViewById(R.id.option).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_message:
                findViewById(R.id.set_message).setEnabled(false); //先禁用下，等到成功之后启用
                HashMap<String,String> dict=new HashMap<>();
                dict.put("content",((TextView)findViewById(R.id.set_message_content)).getText().toString());
                dict.put("image","");
                dict.put("is_encrypt","false");
                AuroraApplication.httpPackage.asyncRequest(
                        getString(R.string.backend_website)+getString(R.string.backend_chat_set_message).replace("0",user_id),
                        "POST",
                        dict,
                        "SET_MESSAGE",
                        this,
                        this
                );
                break;
            case R.id.option:
                PopupMenu menu=new PopupMenu(this,findViewById(R.id.option));
                menu.getMenuInflater().inflate(R.menu.chat_option_menu,menu.getMenu());
                menu.setOnMenuItemClickListener(this);
                menu.show();
                break;
        }
    }

    public static void waitForHeader(Intent jump, Context context){ //等待Header取得之后再跳转
        String user_id=jump.getStringExtra("user_id");
        DbChatHeader ho;
        if(SugarRecord.find(DbChatHeader.class,"authorId = ? and bankId = ?",user_id,AuroraApplication.userID).size()==0) { //没有对应头 //封装他大爷
            //无头信息，写入
            ho = new DbChatHeader();
            ho.bank_id=AuroraApplication.userID;
            ho.author_id = user_id;
            ho.last_message = "";
            ho.last_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date());
            ho.timestamp = new Date().getTime();
            ho.unread = 0;
        }else{
            ho=SugarRecord.find(DbChatHeader.class,"authorId = ? and bankId = ?",user_id,AuroraApplication.userID).get(0);
        }
        AuroraApplication.httpPackage.asyncRequest(
                context.getString(R.string.backend_website) + context.getString(R.string.backend_short_user) + user_id,
                "GET",
                null,
                "SHORT_USER",
                (call, response, tag) -> { //重新获取用户名与nick
                    String s = response.body().string();
                    HeartBeat.ShortUserObject user = AuroraApplication.httpPackage.gson.fromJson(s, new TypeToken<HeartBeat.ShortUserObject>() {
                    }.getType());
                    ho.author_name = user.author_name;
                    ho.author_nick = user.author_nick;
                    ho.save();
                    context.startActivity(jump);
                },
                context
        );
    }

    @Override
    public void onResponse(Call call, Response response, String tag) throws IOException {
        switch (tag){
            case "SET_MESSAGE":
                runOnUiThread(() -> {
                    findViewById(R.id.set_message).setEnabled(true);
                    ((TextView)findViewById(R.id.set_message_content)).setText("");
                });
                if(response.code()==200){
                    DbChatMessage message=new DbChatMessage();
                    message.author_id=AuroraApplication.userID;
                    message.chat_id=user_id; //应该是 to_user 不知道我写的什么玩意
                    message.content=((TextView)findViewById(R.id.set_message_content)).getText().toString();
                    message.img="";
                    message.time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date());
                    message.timestamp= String.valueOf(new Date().getTime());
                    message.bank_id=AuroraApplication.userID;
                    message.save();
                    header.last_message=((TextView)findViewById(R.id.set_message_content)).getText().toString();
                    header.last_time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date());
                    header.save();
                    this.items.add(message);
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                    InputMethodManager manager = ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.hint_message_success), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }else if(response.code()==403){
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
        }
    }

    public void onNext() {
        for(int i=0;i<10;i++){
            if(this.message_list.hasNext()){
                this.items.add(0,this.message_list.next()); //插入最前
            }else{
                return;
            }
        }
    }

    @Override
    public void onRefresh() {
        onNext();
        this.adapter.notifyDataSetChanged();
        this.swipe.setRefreshing(false);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.user_detail:
                Intent user_activity=new Intent(this,UserDetailActivity.class);
                user_activity.putExtra("user_id",user_id);
                startActivity(user_activity);
                break;
            case R.id.delete_record:
                //删除聊天记录
                this.header_record.get(0).delete();
                for (Iterator<DbChatMessage> it = this.message_list; it.hasNext(); ) {
                    DbChatMessage m = it.next();
                    m.delete();
                }
                Toast.makeText(this, R.string.hint_set_success, Toast.LENGTH_SHORT).show();
                this.finish();
                break;
        }
        return false;
    }
}
