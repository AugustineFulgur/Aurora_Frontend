package indi.augusttheodor.aurora.tools;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

import indi.augusttheodor.aurora.R;

public class ContentImageListener implements TextWatcher { //监听image删除的类

    public Context context;
    private ArrayList<String> images;
    public EditText content;

    public ContentImageListener(Context context, ArrayList<String> images, EditText content){
        this.context=context;
        this.images=images;
        this.content=content;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //由于使用光标删除字符串时一次一回调，删除数量多的情况下只可能是span。但是保险起见还是取出字符串比较
        if(count==0){
            return; //增加就返回
        }
        if(s.toString().substring(start,start+count).equals(context.getString(R.string.image_span))){ //删除的是span
            String st=s.toString();
            int i=0; //计数，一些算法题
            int index=0;
            while(index!=-1){
                if(index==0){
                    index=st.indexOf(context.getString(R.string.image_span));
                }else{
                    index=st.indexOf(context.getString(R.string.image_span), index+context.getString(R.string.image_span).length());
                }
                if(index==start){ //找到了
                    images.remove(i);
                    return;
                }
                i++; //没有就下一个
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) { }
}
