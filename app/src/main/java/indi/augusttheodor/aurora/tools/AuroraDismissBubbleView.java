package indi.augusttheodor.aurora.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import indi.augusttheodor.aurora.R;

public class AuroraDismissBubbleView extends View {

    private String mText;

    public boolean mDismiss=false;
    private int[] mExplosionDrawables = {R.drawable.explosion_one, R.drawable.explosion_two
            , R.drawable.explosion_three, R.drawable.explosion_four, R.drawable.explosion_five};
    private Bitmap[] mExplosionBitmaps;
    private int mCurExplosionIndex;

    public AuroraDismissBubbleView(Context context) {
        super(context);
    }

    public AuroraDismissBubbleView(Context context, AttributeSet set){
        super(context,set);
        mExplosionBitmaps = new Bitmap[mExplosionDrawables.length];
        for (int i = 0; i < mExplosionDrawables.length; i++) {
            //将气泡爆炸的drawable转为bitmap
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mExplosionDrawables[i]);
            mExplosionBitmaps[i] = bitmap;
        } //动画相关
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("OD", "onDraw: ");
        if (!TextUtils.isEmpty(mText)) {
            /*
            mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
            canvas.drawText(mText, mBubbleCenterX - mTextRect.width() / 2, mBubbleCenterY + mTextRect.height() / 2, mTextPaint);
            */
        }

        if(mDismiss){
            Rect rect=new Rect();
            getDrawingRect(rect);
            Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
            canvas.drawBitmap(mExplosionBitmaps[mCurExplosionIndex],null,rect,paint);
        }
    }

    public void dismiss(){
        this.mDismiss=true;
        ValueAnimator anim = ValueAnimator.ofInt(0, mExplosionDrawables.length);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(500);
        anim.addUpdateListener(animation -> {
            mCurExplosionIndex = (int) animation.getAnimatedValue();
            postInvalidate();
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束后改变状态
                setVisibility(INVISIBLE); //自动消失
            }
        });
        anim.start();
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

}
