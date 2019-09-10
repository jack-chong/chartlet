package com.jack.chartlet.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.jack.chartlet.bean.ChartletBean;
import com.jack.chartlet.utils.AutoSizeUtils;
import com.jack.chartlet.utils.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jack(黄冲)
 * e-mail : 907755845@qq.com
 * create : 2019-08-02
 * desc   :
 */
public class NavigationLineView extends View {

    //线宽度
    private int cLine = 3;

    //线之间的间隔
    private float cCap = 7;

    private List<Bitmap> mList = new ArrayList<>();
    private List<List<ChartletBean>> mData;
    private Paint mLinePaint;

    public NavigationLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        AutoSizeUtils.autoSize(this);
        init();
    }

    private void init() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.YELLOW);
        mLinePaint.setStrokeWidth(cLine);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(ChartletView.IMAGE_WIDTH * mList.size() + DeviceInfo.sScreenWidth, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mList.size(); i++) {
            canvas.drawBitmap(mList.get(i), getStartX(i), 0, null);
        }


        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        if (mData == null || mData.isEmpty()) return;


        for (int i = mData.size() - 1; i >= 0; i--) {
            for (int j = 0; j < mData.get(i).size(); j++) {
                ChartletBean bean = mData.get(i).get(j);

                if (bean.getBackRectF() == null) continue;
                float sx = bean.getBackRectF().left;
                float sy = getStartY(mData.size() - i);
                float ex = bean.getBackRectF().right;
                float ey = getStartY(mData.size() - i);
                canvas.drawLine(sx, sy, ex, ey, mLinePaint);
            }
        }
    }

    private float getStartY(int index){
        return  ChartletView.IMAGE_HEIGHT - index * cCap + cLine;
    }

    private int getLeftBorder(){
        return DeviceInfo.sScreenWidth / 2;
    }

    private int getStartX(int index){
        return getLeftBorder() + ChartletView.IMAGE_WIDTH * index;
    }

    public void setImageList(List<Bitmap> imageList) {
        mList.addAll(imageList);

    }

    //贴图出现变化, 更新黄线
    public void notificationChartletChange(List<List<ChartletBean>> list) {
        mData = list;
        invalidate();
    }
}
