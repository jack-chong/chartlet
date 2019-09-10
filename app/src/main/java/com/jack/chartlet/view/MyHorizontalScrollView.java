package com.jack.chartlet.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * author : jack(黄冲)
 * e-mail : 907755845@qq.com
 * create : 2019-07-31
 * desc   :
 */
public class MyHorizontalScrollView extends HorizontalScrollView {

    private Paint mPaint;
    private boolean isBanEvent;

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isBanEvent){
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(10);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawLine(getWidth() / 2 + getScrollX(), 0, getWidth() / 2 + getScrollX(), canvas.getHeight(), mPaint);
    }


    public void scrollToPosition(int x, int y) {
        ObjectAnimator xTranslate = ObjectAnimator.ofInt(this, "scrollX", x);
        ObjectAnimator yTranslate = ObjectAnimator.ofInt(this, "scrollY", y);
        AnimatorSet animators = new AnimatorSet();
        animators.setDuration(200L);
        animators.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isBanEvent = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isBanEvent = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isBanEvent = false;
            }
        });
        animators.playTogether(xTranslate, yTranslate);
        animators.start();
    }
}
