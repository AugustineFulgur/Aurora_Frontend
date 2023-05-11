package indi.augusttheodor.aurora.tools;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

public class ImageClickableMovementMethod extends LinkMovementMethod { //覆写原LinkMovementMethod使得自定义的ImageSpan也可以有点击事件

    private static ImageClickableMovementMethod sInstance;

    public static ImageClickableMovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new ImageClickableMovementMethod();
        }
        return sInstance;
    }

    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            float xLeft=layout.getPrimaryHorizontal(off);
            if(xLeft<x){
                off+=1;
            }else{
                off-=1;
            } //校正偏移量

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            AuroraImageClickableSpan[] imageSpans = buffer.getSpans(off, off, AuroraImageClickableSpan.class); //添加对AuroraImageClickableSpan的处理

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }

                return true;
            } else if (imageSpans.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    imageSpans[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(imageSpans[0]),
                            buffer.getSpanEnd(imageSpans[0]));
                }

                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }

        return false;
    }
}