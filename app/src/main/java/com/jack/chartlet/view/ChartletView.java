package com.jack.chartlet.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.OverScroller;

import com.jack.chartlet.R;
import com.jack.chartlet.bean.ChartletBean;
import com.jack.chartlet.bean.JRectF;
import com.jack.chartlet.utils.AutoSizeUtils;
import com.jack.chartlet.utils.CommonUtils;
import com.jack.chartlet.utils.DeviceInfo;
import com.jack.chartlet.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * author : jack(黄冲)
 * e-mail : 907755845@qq.com
 * create : 2019-07-31
 * desc   :
 */
public class ChartletView extends BaseScrollerView {

    //底部图片宽度
    public static int IMAGE_WIDTH = AutoSizeUtils.getScaleWidth(120);
    public static int IMAGE_HEIGHT = AutoSizeUtils.getScaleWidth(100);

    //贴图高度
    private float cChartletHeight = 60;

    //贴图宽度
    private float cChartletWidth = 100;

    //贴图矩形圆角半径
    private float cChartletRound = 10;

    //贴图Padding
    private int cChartletPadding = 10;

    //拉伸背景框离贴图的边距
    private int cChartletStretchMargin = 5;

    //吸附白线的偏移量, 小于这个值就会触发吸附效果
    private int cCanAdsorbOffset = 10;

    //贴图往上新增一行所需的偏移量, 超过这个值会新增一行
    private int cUpNewLineOffset = 30;

    //贴图最小创建宽度
    private float cChartletMinCreateWidth = 0.4f * cChartletWidth;

    //拉伸框宽度
    private int cStretchFrameWidth = 40;

    //拉伸框高度
    private int cStretchFrameHeight = 60;

    //拉伸框圆角
    private float cStretchFrameRound = 5;

    //文字大小
    private float cTextSize = 28;

    //左右自动滚动的偏移量
    private float cAutoScrollHorizontalOffset = 100;

    //上下自动滚动的偏移量
    private float cAutoScrollVerticalOffset = -10;

    //图片个数
    private int mImageCount;


    private RectF mStretchFrameBack = new RectF();
    private RectF mStretchFrameLeft = new RectF();
    private RectF mStretchFrameRight = new RectF();

    private JRectF mLongClickRect;

    private ChartletBean mOldStretchBean, mOldLongClickBean;


    private List<List<ChartletBean>> mData = new ArrayList<>();

    private Canvas mCanvas;
    private Paint mChartletBackPaint;
    private Paint mStretchBackPaint;


    /**
     * isActionStretchLeft : 是否拖拽左边拉伸框
     * isActionStretchRight : 是否拖拽右边拉伸框
     * isDrag : 是否是长按拖动状态
     * isCover : 长按拖动时是否和其他贴图重合
     * isAutoScrollV : 是否上下自动滚动状态
     * isAutoScrollH : 是否左右自动滚动状态
     * isShowTopLine : 是否显示顶部黄线
     */
    private boolean isActionStretchLeft, isActionStretchRight, isDrag, isCover, isAutoScrollV, isAutoScrollH, isShowTopLine;


    private Bitmap mLeftArrow;
    private Bitmap mRightArrow;
    private OnChartletChangeListener mChangeListener;
    private OnLongDragListener mDragListener;
    private TextPaint mTextPaint;
    private Runnable mRunnableH, mRunnableV;

    private float mDownX, mDownY;

    //当前View 顶部相当于手机屏幕的位置
    private int mTop;
    private Paint mLinePaint;

    public ChartletView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        AutoSizeUtils.autoSize(this);
        init();
    }


    private void init() {

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(cTextSize);

        mChartletBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChartletBackPaint.setAntiAlias(true);

        mStretchBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStretchBackPaint.setColor(Color.WHITE);
        mStretchBackPaint.setAntiAlias(true);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.YELLOW);

        mLeftArrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_left).copy(Bitmap.Config.ARGB_8888, true);
        mRightArrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_rigth).copy(Bitmap.Config.ARGB_8888, true);
        mRightArrow = ImageUtils.rotateBitmap(mRightArrow, 180);

        mLeftArrow = ImageUtils.scale(mLeftArrow, cStretchFrameWidth, cStretchFrameHeight);
        mRightArrow = ImageUtils.scale(mRightArrow, cStretchFrameWidth, cStretchFrameHeight);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(IMAGE_WIDTH * mImageCount + DeviceInfo.sScreenWidth, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w * h > 0 && w > DeviceInfo.sScreenWidth){
            int[] ints = new int[2];
            getLocationInWindow(ints);
            mTop = ints[1];

            int colCount = h / (int)(cChartletHeight + cChartletPadding);
            for (int i = 0; i < colCount; i++) {
                mData.add(new ArrayList<>());
            }
        }
    }

    @Override
    protected void onDrawContent(Canvas canvas) {
        canvas.save();
        canvas.translate(0, mOffsetY);
        drawContent(canvas);
        canvas.restore();
    }

    
    private void drawContent(Canvas canvas){
        mCanvas = canvas;
        ChartletBean longClickBean = null;
        ChartletBean stretchBean = null;

        if (isDrag && mOffsetY == 0 && isShowTopLine){
            canvas.drawLine(0, 1, getWidth(), 1, mLinePaint);
            canvas.drawLine(0, 3, getWidth(), 3, mLinePaint);
        }

        for (int i = 0; i < mData.size(); i++) {
            for (int j = 0; j < mData.get(i).size(); j++) {
                ChartletBean bean = mData.get(i).get(j);

                if (bean.getBitmap() == null) {
                    bean.setBitmap(CommonUtils.getAssetsBitmap(getContext(), bean.getIconPath()));
                }

                if (bean.getBackRectF() == null) {
                    int scrollX = getScrollOffsetX();
                    RectF rectF = new RectF();
                    rectF.left = getLeftBorder() + scrollX;
                    rectF.top = i * (cChartletHeight + cChartletPadding) + cChartletPadding;
                    rectF.right = rectF.left + cChartletWidth;
                    rectF.bottom = rectF.top + cChartletHeight;
                    bean.setBackRectF(rectF);
                }
                if (bean.isCanLongClick()) {
                    //判断是否为长按的贴图, 为长按则临时保存一个值, 放到最后在画, 这样才能长按贴图在其他贴图的图层的顶部
                    longClickBean = bean;
                    continue;
                }

                bean.getBackRectF().top = i * (cChartletHeight + cChartletPadding) + cChartletPadding;
                bean.getBackRectF().bottom = bean.getBackRectF().top + cChartletHeight;


                if (bean.isCanStretch()){
                    //判断是否为拉伸的贴图, 为拉伸则临时保存一个值, 放到最后在画, 这样才能长按贴图在其他贴图的上层
                    stretchBean = bean;
                    float left = stretchBean.getBackRectF().left - cChartletStretchMargin;
                    float top = stretchBean.getBackRectF().top - cChartletStretchMargin;
                    float right = stretchBean.getBackRectF().right + cChartletStretchMargin;
                    float bottom = stretchBean.getBackRectF().bottom + cChartletStretchMargin;
                    mStretchFrameBack.set(left, top, right, bottom);
                }
                drawBody(bean);
            }
        }



        if (stretchBean != null){
            float left = mStretchFrameBack.left;
            float top = mStretchFrameBack.top;
            float right = mStretchFrameBack.right;
            float bottom = mStretchFrameBack.bottom;

            if (Math.abs(left + cChartletStretchMargin - getLeftBorder() - getScrollOffsetX()) < cCanAdsorbOffset){
                left = getLeftBorder() + getScrollOffsetX();
            }
            if (Math.abs(right - cChartletStretchMargin - getLeftBorder() - getScrollOffsetX()) < cCanAdsorbOffset){
                right = getLeftBorder() + getScrollOffsetX();
            }

            canvas.drawRoundRect(left, top, right, bottom, cChartletRound, cChartletRound, mStretchBackPaint);
            drawBody(stretchBean);

            mStretchFrameLeft.left = left - cStretchFrameWidth;
            mStretchFrameLeft.top = stretchBean.getBackRectF().top;
            mStretchFrameLeft.right = left;
            mStretchFrameLeft.bottom = stretchBean.getBackRectF().bottom;

            canvas.drawRoundRect(mStretchFrameLeft, cStretchFrameRound, cStretchFrameRound, mStretchBackPaint);
            canvas.drawBitmap(mLeftArrow, mStretchFrameLeft.left, mStretchFrameLeft.top, null);

            mStretchFrameRight.left = right;
            mStretchFrameRight.top = stretchBean.getBackRectF().top;
            mStretchFrameRight.right = right + cStretchFrameWidth;
            mStretchFrameRight.bottom = stretchBean.getBackRectF().bottom;

            canvas.drawRoundRect(mStretchFrameRight, cStretchFrameRound, cStretchFrameRound, mStretchBackPaint);
            canvas.drawBitmap(mRightArrow, mStretchFrameRight.left, mStretchFrameRight.top, null);
        }


        if (longClickBean != null){
            Bitmap bitmap = longClickBean.getBitmap();
            mChartletBackPaint.setColor(longClickBean.getBackColor());
            mChartletBackPaint.setAlpha(150);
            mStretchBackPaint.setAlpha(150);

            if (isCover){
                mChartletBackPaint.setAlpha(255);
                mChartletBackPaint.setColor(0xaa666666);
            }


            if (mLongClickRect == null){
                mLongClickRect = new JRectF(longClickBean.getBackRectF());
                mLongClickRect.top -= cChartletPadding;
                mLongClickRect.bottom -= cChartletPadding;
            }



            float colGap = cChartletHeight + cChartletPadding;
            int col = getDragCol();

            float height = mLongClickRect.height();
            float width = mLongClickRect.width();

            mLongClickRect.drawLeft = mLongClickRect.left;
            mLongClickRect.drawTop = col * colGap;


            if (mLongClickRect.drawTop < -mOffsetY){
                int i = -((int) mOffsetY) / ((int) colGap);
                mLongClickRect.drawTop = i * colGap;
            }

            if (mLongClickRect.drawTop > -mOffsetY + getHeight() - colGap){
                ///下拉自动滚动位置判断, colGap / 2 为超过行高的一半就下去
                //想要完全露出来才下去  int i = (int)(-mOffsetY + getHeight() - (colGap)) / ((int) colGap);
                //想直接下去  int i = (int)(-mOffsetY + getHeight()) / ((int) colGap);
                int i = (int)(-mOffsetY + getHeight() - (colGap / 2)) / ((int) colGap);
                mLongClickRect.drawTop = i * colGap;
            }


            mLongClickRect.drawRight = mLongClickRect.right;
            mLongClickRect.drawBottom = mLongClickRect.drawTop + height;


            float l = mLongClickRect.left;
            float t =  mLongClickRect.drawTop;
            float r = mLongClickRect.right;
            float b = mLongClickRect.drawBottom;

            if (Math.abs(l - getLeftBorder() - getScrollOffsetX()) < cCanAdsorbOffset){
                l = getLeftBorder() + getScrollOffsetX();
                r = l + width;
            }
            if (Math.abs(r - getLeftBorder() - getScrollOffsetX()) < cCanAdsorbOffset){
                r = getLeftBorder() + getScrollOffsetX();
                l = r - width;
            }

            canvas.drawRoundRect(l, t, r, b, cChartletRound, cChartletRound, mChartletBackPaint);

            mChartletBackPaint.setAlpha(255);
            mStretchBackPaint.setAlpha(255);


            int left = (int) l + cChartletPadding;
            int top = (int) t + cChartletPadding;
            int bottom = (int) b - cChartletPadding;
            int centerX = (int) mLongClickRect.height() - cChartletPadding + left;
            centerX = (int) Math.min(centerX, mLongClickRect.right);
            int centerY = (bottom + top) / 2;
            Rect dst = new Rect(left, top, centerX, bottom);


            switch (longClickBean.getType()) {
                case IMAGE: {
                    Rect src = new Rect(0 ,0 , bitmap.getWidth(), bitmap.getHeight());
                    canvas.drawBitmap(bitmap, src, dst, null);
                }
                break;
                case TEXT: {
                    String text = longClickBean.getText() == null ? "" : longClickBean.getText();
                    Rect rect = new Rect();
                    rect.left = (int) l;
                    rect.top = (int) t;
                    rect.right = (int) r;
                    rect.bottom = (int) b;
                    canvas.save();
                    canvas.clipRect(rect);
                    canvas.drawText(text, left, textCoordCenter(centerY, mTextPaint), mTextPaint);
                    canvas.restore();
                }
                break;
                case IMAGE_AND_TEXT:
                    Rect src = new Rect(0 ,0 , bitmap.getWidth(), bitmap.getHeight());
                    canvas.drawBitmap(bitmap, src, dst, null);
                    String text = longClickBean.getText() == null ? "" : longClickBean.getText();
                    Rect rect = new Rect();
                    rect.left = (int) l;
                    rect.top = (int) t;
                    rect.right = (int) r;
                    rect.bottom = (int) b;
                    canvas.save();
                    canvas.clipRect(rect);
                    canvas.drawText(text, centerX, textCoordCenter(centerY, mTextPaint), mTextPaint);
                    canvas.restore();
                    break;
            }

        }
    }


    private void drawBody(ChartletBean bean){
        RectF rectF = new RectF(bean.getBackRectF());
        if (Math.abs(rectF.left - getLeftBorder() - getScrollOffsetX()) < cCanAdsorbOffset && (bean == mOldStretchBean || bean == mOldLongClickBean)){
            rectF.left = getLeftBorder() + getScrollOffsetX();
        }
        if (Math.abs(rectF.right - getLeftBorder() - getScrollOffsetX()) < cCanAdsorbOffset && (bean == mOldStretchBean || bean == mOldLongClickBean)){
            rectF.right = getLeftBorder() + getScrollOffsetX();
        }

        mChartletBackPaint.setColor(bean.getBackColor());
        mCanvas.drawRoundRect(rectF, cChartletRound, cChartletRound, mChartletBackPaint);

        int left = (int) rectF.left + cChartletPadding;
        Log.i("jack", String.valueOf(rectF.left));
        int top = (int) rectF.top + cChartletPadding;
        int bottom = (int) rectF.bottom - cChartletPadding;
        int centerX = (int) rectF.height() - cChartletPadding * 2 + left;
        int centerY = (bottom + top) / 2;
        centerX = (int) Math.min(centerX, rectF.right);
        Rect dst = new Rect(left, top, centerX, bottom);

        switch (bean.getType()) {
            case IMAGE: {
                Rect src = new Rect(0 ,0 , bean.getBitmap().getWidth(), bean.getBitmap().getHeight());
                mCanvas.drawBitmap(bean.getBitmap(), src, dst, null);
            }
            break;
            case TEXT: {
                String text = bean.getText() == null ? "" : bean.getText();
                Rect rect = new Rect();
                rect.left = (int) rectF.left;
                rect.top = (int) rectF.top;
                rect.right = (int) rectF.right;
                rect.bottom = (int) rectF.bottom;
                mCanvas.save();
                mCanvas.clipRect(rect);
                mCanvas.drawText(text, left, textCoordCenter(centerY, mTextPaint), mTextPaint);
                mCanvas.restore();
            }
            break;
            case IMAGE_AND_TEXT:
                Rect src = new Rect(0 ,0 , bean.getBitmap().getWidth(), bean.getBitmap().getHeight());
                mCanvas.drawBitmap(bean.getBitmap(), src, dst, null);
                String text = bean.getText() == null ? "" : bean.getText();
                Rect rect = new Rect();
                rect.left = (int) rectF.left;
                rect.top = (int) rectF.top;
                rect.right = (int) rectF.right;
                rect.bottom = (int) rectF.bottom;
                mCanvas.save();
                mCanvas.clipRect(rect);
                mCanvas.drawText(text, centerX, textCoordCenter(centerY, mTextPaint), mTextPaint);
                mCanvas.restore();
                break;
        }

    }

    @Override
    public void down(MotionEvent event) {
        super.down(event);

        mDownX = event.getRawX();
        mDownY = event.getRawY();

        float x = event.getX() - mOffsetX;
        float y = event.getY() - mOffsetY;

        isActionStretchRight = false;
        isActionStretchLeft = false;

        if (mStretchFrameLeft.contains(x, y)){
            //按在左侧拉伸框上
            getParent().requestDisallowInterceptTouchEvent(true);
            isActionStretchLeft = true;
        }
        if (mStretchFrameRight.contains(x, y)){
            //按在右侧拉伸框上
            getParent().requestDisallowInterceptTouchEvent(true);
            isActionStretchRight = true;
        }
    }


    @Override
    public void move(MotionEvent event) {
        if (!isActionStretchLeft && !isActionStretchRight && !isDrag){
            super.move(event);
        }

        //用move减去down得到每次move时的偏移量
        float dx = event.getRawX() - mDownX;
        float dy = event.getRawY() - mDownY;

        mMoveXOffsetCount += Math.abs(dx);
        mMoveYOffsetCount += Math.abs(dy);
        mDownX = event.getRawX();
        mDownY = event.getRawY();

        if (mMoveXOffsetCount < CLICK_OFFSET && mMoveYOffsetCount < CLICK_OFFSET){
            return;
        }
        onDrag(event, dx, dy);

        removeLongClick();



        if (isActionStretchLeft && mOldStretchBean != null){
            //左边拉伸框正在移动, 累加偏移量, 不能小于左边界不能大于右边界
            mOldStretchBean.getBackRectF().left += dx;
            if (mOldStretchBean.getBackRectF().left < getLeftBorder()){
                mOldStretchBean.getBackRectF().left = getLeftBorder();
            }
            if (mOldStretchBean.getBackRectF().left > mOldStretchBean.getBackRectF().right){
                mOldStretchBean.getBackRectF().left = mOldStretchBean.getBackRectF().right;
            }
            int col = getCol((int) mOldStretchBean.getBackRectF().top);
            for (int i = 0; i < mData.get(col).size(); i++) {
                ChartletBean bean = mData.get(col).get(i);
                if (bean != mOldStretchBean){

                    //判断左边有没有其他贴图, 如果有, 那么最多只能拉伸到它的右边
                    if (mOldStretchBean.getBackRectF().left - bean.getBackRectF().right < 0 && mOldStretchBean.getBackRectF().left > bean.getBackRectF().left) {
                        mOldStretchBean.getBackRectF().left = bean.getBackRectF().right;
                        break;
                    }
                }
            }
            if (mDragListener != null){
                mDragListener.onStretchProcess(mOldStretchBean, mOldStretchBean.getBackRectF().left, mOldStretchBean.getBackRectF().right, mOldStretchBean.getBackRectF().width());
            }
        }


        if (isActionStretchRight && mOldStretchBean != null){
            //左边拉伸框正在移动, 累加偏移量, 不能小于左边界不能大于右边界
            mOldStretchBean.getBackRectF().right += dx;

            if (mOldStretchBean.getBackRectF().right > getRightBorder()){
                mOldStretchBean.getBackRectF().right = getRightBorder();
            }
            if (mOldStretchBean.getBackRectF().right < mOldStretchBean.getBackRectF().left){
                mOldStretchBean.getBackRectF().right = mOldStretchBean.getBackRectF().left;
            }
            int col = getCol((int) mOldStretchBean.getBackRectF().top);
            for (int i = 0; i < mData.get(col).size(); i++) {
                ChartletBean bean = mData.get(col).get(i);
                if (bean != mOldStretchBean){
                    //判断右边有没有其他贴图, 如果有, 那么最多只能拉伸到它的左边
                    if (mOldStretchBean.getBackRectF().right - bean.getBackRectF().left > 0 && mOldStretchBean.getBackRectF().right < bean.getBackRectF().right) {
                        mOldStretchBean.getBackRectF().right = bean.getBackRectF().left;
                        break;
                    }
                }
            }
            if (mDragListener != null){
                mDragListener.onStretchProcess(mOldStretchBean, mOldStretchBean.getBackRectF().left, mOldStretchBean.getBackRectF().right, mOldStretchBean.getBackRectF().width());
            }
        }

        if (isDrag && mLongClickRect != null){
            //正在常长按拖拽

            //是否可以显示黄线
            isShowTopLine = event.getY() < -cAutoScrollVerticalOffset;

            float width = mLongClickRect.width();
            float height = mLongClickRect.height();
            mLongClickRect.left += dx;
            mLongClickRect.top += dy;


            if (mLongClickRect.left < getLeftBorder()){
                mLongClickRect.left = getLeftBorder();

            }
            if (mLongClickRect.left > getRightBorder() - width){
                mLongClickRect.left = getRightBorder() - width;
            }

            if (mLongClickRect.top > getBottomBorder() - height){
                mLongClickRect.top = getBottomBorder() - height;
            }

            //判断当前手指的坐标, 如果当前手指的坐标满足以下条件, 则执行
            if (event.getRawY() < mTop - cAutoScrollVerticalOffset){
                //手指到了顶部, 开始往上自动滚动
                prepareScrollTop((int) event.getRawY());
            }else if (event.getRawY() > mTop + cAutoScrollVerticalOffset + getHeight()){
                //手指到了底部, 开始往下自动滚动
                prepareScrollBottom((int) event.getRawY());
            }else {
                //不是顶部或者底部就取消
                stopScrollV();
            }

            if (event.getRawX() > DeviceInfo.sScreenWidth - cAutoScrollHorizontalOffset){
                //手指到了右侧, 开始往右自动滚动
                prepareScrollRight((int) event.getRawX());
            }else if (event.getRawX() < cAutoScrollHorizontalOffset){
                //手指到了左侧, 开始往左自动滚动
                prepareScrollLeft((int) event.getRawX());
            }else{
                //不是左部或者右就取消
                stopScrollH();
            }

            mLongClickRect.right = mLongClickRect.left + width;
            mLongClickRect.bottom = mLongClickRect.top + height;

            int dragCol = getDragCol();

            //判断是否和其他贴图重合
            isCover = false;
            for (ChartletBean bean : mData.get(dragCol)) {
                if (intersectScaleX(mLongClickRect, bean.getBackRectF()) > 0 && bean != mOldLongClickBean) {
                    isCover = true;
                    break;
                }
            }
        }

    }

    private int getLeftBorder(){
        return DeviceInfo.sScreenWidth / 2;
    }

    private int getRightBorder(){
        return getWidth() - DeviceInfo.sScreenWidth / 2;
    }

    private int getTopBorder(){
        return cChartletPadding;
    }

    private int getBottomBorder(){
        return (int) (mData.size() * (cChartletHeight + cChartletPadding) + cChartletPadding);
    }

    private float getColGap(){
        return cChartletHeight + cChartletPadding;
    }


    private int getDragCol(){
        float colGap = cChartletHeight + cChartletPadding;
        int dragCol = mLongClickRect.top % colGap < colGap / 2 ? (int) (mLongClickRect.top / colGap) : (int) (mLongClickRect.top / colGap) + 1;
        return dragCol < 0 ? 0 : dragCol >= mData.size() ? dragCol = mData.size() - 1 : dragCol;
    }

    private float findHeightByCol(int dragCol){
        float colGap = cChartletHeight + cChartletPadding;
        return dragCol * colGap + cChartletPadding;
    }

    private int getCol(int top){
        float colGap = cChartletHeight + cChartletPadding;
        return top % colGap < colGap / 2 ? (int)(top / colGap) : (int)(top / colGap) + 1;
    }

    public float textCoordCenter(float y, TextPaint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return y - (fontMetrics.ascent - fontMetrics.descent) / 2.5f;
    }

    private void prepareScrollRight(int rax){
        if (!isAutoScrollH && !isAutoScrollV){
            isAutoScrollH = true;
            startScrollRight(rax);
        }
    }

    private void prepareScrollLeft(int rax){
        if (!isAutoScrollH && !isAutoScrollV){
            isAutoScrollH = true;
            startScrollLeft(rax);
        }
    }

    //执行向上滚动, 给个变量标识是因为move里面多多次触发, 但是指向指向一次这个
    private void prepareScrollTop(int ray){
        if (!isAutoScrollV){
            stopScrollH();
            isAutoScrollV = true;
            //递归指向
            startScrollTop(ray);
        }
    }

    private void prepareScrollBottom(int ray){
        if (!isAutoScrollV){
            stopScrollH();
            isAutoScrollV = true;
            startScrollBottom(ray);
        }
    }

    //递归调用, 循环移动, 其他方向的同理
    private void startScrollTop(int ray){
        mRunnableV = () -> {

            if (mLongClickRect != null && mOffsetY != 0) {
                //向上滚动视图, 并且所按下的贴图会改变位置网上
                mOffsetY += getColGap();
                float height = mLongClickRect.height();
                mLongClickRect.top -= height;
                mLongClickRect.bottom = mLongClickRect.top + height;
                if (mOffsetY > 0){
                    mOffsetY = 0;
                }

                if (ray > mTop - cAutoScrollVerticalOffset){
                    //如果当前手指不在顶部, 则停止移动
                    stopScrollV();
                }
                invalidate();
                //递归调用
                startScrollTop(ray);
            }
        };

        mHandler.postDelayed(mRunnableV, 400);
    }

    //递归调用, 循环移动
    private void startScrollBottom(int ray){
        mRunnableV = () -> {
            if (mLongClickRect != null && Math.abs(mOffsetY) != mMaxValY) {
                mOffsetY -= getColGap();
                float height = mLongClickRect.height();
                mLongClickRect.top += height;
                mLongClickRect.bottom = mLongClickRect.top + height;
                if (-mOffsetY > mMaxValY){
                    mOffsetY = -mMaxValY;
                }

                if (ray < mTop + cAutoScrollVerticalOffset + getHeight()){
                    stopScrollV();
                }
                invalidate();
                startScrollBottom(ray);
            }
        };

        mHandler.postDelayed(mRunnableV, 400);
    }

    //递归调用, 循环移动
    private void startScrollRight(int rax){
        mRunnableH = () -> {
            if (mLongClickRect != null && getScrollOffsetX() < getRightBorder() - DeviceInfo.sScreenWidth) {
                int dx = 3;
                ((MyHorizontalScrollView) getParent().getParent()).scrollBy(dx, 0);
                float width = mLongClickRect.width();
                mLongClickRect.left += dx;
                mLongClickRect.right = mLongClickRect.left + width;

                if (mLongClickRect.left < getLeftBorder()){
                    mLongClickRect.left = getLeftBorder();
                    mLongClickRect.right = mLongClickRect.left + width;
                }
                if (mLongClickRect.right > getRightBorder()){
                    mLongClickRect.right = getRightBorder();
                    mLongClickRect.left = mLongClickRect.right - width;
                }

                if (rax < DeviceInfo.sScreenWidth - cAutoScrollHorizontalOffset){
                    stopScrollH();
                    return;
                }
                invalidate();
                startScrollRight(rax);
            }
        };
        mHandler.postDelayed(mRunnableH, 16);
    }
    //递归调用, 循环移动
    private void startScrollLeft(int rax){
        mRunnableH = () -> {

            if (mLongClickRect != null && getScrollOffsetX() > getLeftBorder()){
                int dx = -3;
                ((MyHorizontalScrollView) getParent().getParent()).scrollBy(dx, 0);
                float width = mLongClickRect.width();
                mLongClickRect.left += dx;
                mLongClickRect.right = mLongClickRect.left + width;

                if (mLongClickRect.left < getLeftBorder()){
                    mLongClickRect.left = getLeftBorder();
                    mLongClickRect.right = mLongClickRect.left + width;
                }
                if (mLongClickRect.right > getRightBorder()){
                    mLongClickRect.right = getRightBorder();
                    mLongClickRect.left = mLongClickRect.right - width;
                }

                if (rax > cAutoScrollHorizontalOffset){
                    stopScrollH();
                    return;
                }
                invalidate();
                startScrollLeft(rax);
            }

        };
        mHandler.postDelayed(mRunnableH, 16);
    }


    public void stopScrollV(){
        mHandler.removeCallbacks(mRunnableV);
        isAutoScrollV = false;
    }

    public void stopScrollH(){
        mHandler.removeCallbacks(mRunnableH);
        isAutoScrollH = false;
    }

    @Override
    public void onClick(MotionEvent event) {
        super.onClick(event);

        //点击时触发
        float x = event.getX() - mOffsetX;
        float y = event.getY() - mOffsetY;


        for (int i = 0; i < mData.size(); i++) {
            for (int j = 0; j < mData.get(i).size(); j++) {
                ChartletBean bean = mData.get(i).get(j);
                if (bean.getBackRectF().contains(x, y)) {
                    if (mOldStretchBean != null){
                        //取消上一个拉伸框
                        mOldStretchBean.setCanStretch(false);
                    }
                    if (mOldStretchBean == bean){
                        //如果点击的是自己, 则取消
                        if (mDragListener != null){
                            mDragListener.onClick(mOldStretchBean, false);
                        }
                        mOldStretchBean = null;
                        return;
                    }
                    //开启拉伸框
                    bean.setCanStretch(true);
                    mOldStretchBean = bean;
                    if (mDragListener != null){
                        mDragListener.onClick(mOldStretchBean, false);
                    }
                    return;
                }
            }

        }

        //能走到这来, 说明没点击中任何贴图, 则点击外部关闭拉伸框
        resetStretch();
    }

    @Override
    public void onLongClick(float x, float y) {

        //长按时触发
        x -= mOffsetX;
        y -= mOffsetY;

        for (int i = 0; i < mData.size(); i++) {
            for (int j = 0; j < mData.get(i).size(); j++) {
                ChartletBean bean = mData.get(i).get(j);
                //如果当前触摸的坐标和贴图匹配上则长按长按拖拽生效
                if (bean.getBackRectF().contains(x, y)) {

                    //拦截事件,  标记为长按, 并且清除拉伸框的选中效果
                    getParent().requestDisallowInterceptTouchEvent(true);
                    bean.setCanLongClick(true);
                    mOldLongClickBean = bean;
                    isDrag = true;
                    resetStretch();
                    if (mDragListener != null){
                        mDragListener.onLongClick(mData.get(i).get(j), i, j);
                    }
                    return;
                }
            }

        }
    }

    @Override
    public void onCancel() {

        //事件被父控件拦截时触发
        resetLongClick();
        if (isActionStretchLeft || isActionStretchRight){
            if (mDragListener != null){
                mDragListener.onStretchFinish(mOldStretchBean, mOldStretchBean.getBackRectF().left, mOldStretchBean.getBackRectF().right, mOldStretchBean.getBackRectF().width());
            }
            isActionStretchLeft = false;
            isActionStretchRight = false;
        }
        mChangeListener.change(mData);
    }

    @Override
    public void up(MotionEvent event) {
        super.up(event);

        if (isActionStretchLeft || isActionStretchRight){
            //取消拉伸标识
            if (mDragListener != null){
                mDragListener.onStretchFinish(mOldStretchBean, mOldStretchBean.getBackRectF().left, mOldStretchBean.getBackRectF().right, mOldStretchBean.getBackRectF().width());
            }
            if (isActionStretchLeft){
                scrollToPosition((int) (mOldStretchBean.getBackRectF().left - getLeftBorder()), 0);
            }
            if (isActionStretchRight){
                scrollToPosition((int) (mOldStretchBean.getBackRectF().right - getLeftBorder()), 0);
            }

            isActionStretchLeft = false;
            isActionStretchRight = false;
        }



        if (isDrag && !isCover){

            //长按松手时触发, 先找到当前松手的贴图位置, 几行几列, 找到这个贴图的左测和右侧的贴图, 计算空间, 如果空间不够则还原贴图. 如果空间足够就移动贴图到这个位置

            //1. 先移除掉长按的贴图
            int col = 0, row = 0;
            wai : for (int i = 0; i < mData.size(); i++) {
                for (int j = 0; j < mData.get(i).size(); j++) {
                    ChartletBean bean = mData.get(i).get(j);
                    if (bean == mOldLongClickBean) {
                        col = i;
                        row = j;
                        mData.get(i).remove(j);
                        break wai;
                    }
                }
            }

            //判断松手时, 手的位置,如果在最上则为新增一行
            if ( mLongClickRect.top < -cUpNewLineOffset){
                List<ChartletBean> list = new ArrayList<>();
                mOldLongClickBean.setBackRectF(mLongClickRect);
                list.add(mOldLongClickBean);
                mData.add(0, list);
                mOffsetY = 0;
                setMaxVal(0, getBottomBorder() - getHeight());
                if (mDragListener != null){
                    mDragListener.onDragFinish(mOldLongClickBean, 0);
                }
            }else{
                //如果不为新增, 则判断松手位置的列, 添加到对应的列中
                int dragCol = getDragCol();
                List<ChartletBean> list = mData.get(dragCol);

                boolean canAdd = true;
                for (int i = 0; i < list.size(); i++) {
                    ChartletBean bean = list.get(i);
                    if (intersectScaleX(bean.getBackRectF(), mLongClickRect) > 0) {
                        //该位置有其他贴图
                        canAdd = false;
                        break;
                    }
                }
                if (canAdd){
                    //添加到对应位置
                    float colGap = cChartletHeight + cChartletPadding;
                    mLongClickRect.top = dragCol * colGap + cChartletPadding;
                    mLongClickRect.bottom = mLongClickRect.top + cChartletHeight;
                    mOldLongClickBean.setBackRectF(mLongClickRect);
                    list.add(mOldLongClickBean);
                    if (mDragListener != null){
                        mDragListener.onDragFinish(mOldLongClickBean, dragCol);
                    }
                }else {
                   //移动失败, 还原位置
                    mData.get(col).add(row, mOldLongClickBean);
                }
            }


            stopScrollH();
            stopScrollV();

        }

        resetLongClick();

        mChangeListener.change(mData);
    }



    private void resetLongClick(){
        if (mOldLongClickBean != null){
            mOldLongClickBean.setCanLongClick(false);
            mOldLongClickBean.setCanStretch(false);
            mOldLongClickBean = null;
            mLongClickRect = null;
            isDrag = false;
        }
    }

    private void resetStretch(){
        if (mOldStretchBean != null){
            mOldStretchBean.setCanStretch(false);
            mOldStretchBean = null;
        }
        isActionStretchLeft = false;
        isActionStretchRight = false;
    }



    private static double intersectScaleX(RectF rect1, RectF rect2) {
        float width = Math.min(rect1.right - rect2.left, rect2.right - rect1.left);
        if (width <= 0) return 0;
        if (width >= rect1.width()) {
            return 1;
        }
        return (width * 1.0f / rect1.width());
    }

    public void addData(ChartletBean bean) {
        searchEmptyAndAdd(bean);
        setMaxVal(0, getBottomBorder() - getHeight());
        invalidate();
    }

    private int getScrollOffsetX(){
        return ((MyHorizontalScrollView) getParent().getParent()).getScrollX();
    }

    private void searchEmptyAndAdd(ChartletBean chartletBean){

        int scrollX = getScrollOffsetX();


        RectF rectF = new RectF();
        rectF.left = scrollX + getLeftBorder();
        rectF.top = findHeightByCol(0);
        rectF.right = rectF.left + cChartletWidth;
        rectF.bottom = rectF.top + cChartletHeight;
        int maxScrollX = getWidth() - DeviceInfo.sScreenWidth;
        int createWidth =  maxScrollX - scrollX;
        if (createWidth < cChartletWidth){
            rectF.left = scrollX + getLeftBorder() - (cChartletWidth - createWidth);
            rectF.right = rectF.left + cChartletWidth;
        }
        chartletBean.setBackRectF(rectF);

        //创建时排序,  如果要改为从上至下, 把for改为正序遍历, 并把mData.add(0, list);改为 mData.add(list);
        for (int i = mData.size() - 1; i >= 0; i--) {

            rectF.top = findHeightByCol(i);
            rectF.bottom = rectF.top + cChartletHeight;

            boolean canAdd = true;


            for (int j = 0; j < mData.get(i).size(); j++) {
                ChartletBean bean = mData.get(i).get(j);
                if (intersectScaleX(bean.getBackRectF(), rectF) > 0) {
                    if (bean.getBackRectF().left > rectF.left + cChartletMinCreateWidth) {
                        rectF.right = bean.getBackRectF().left;
                        canAdd = true;
                        continue;
                    }
                    canAdd = false;
                    break;
                }
            }
            if (canAdd){
                mData.get(i).add(chartletBean);
                scrollToPosition((int) (rectF.left - getLeftBorder()), 0);
                setScrollY(getColGap() * i);
                if (mOldStretchBean != null){
                    mOldStretchBean.setCanStretch(false);
                }
                mOldStretchBean = chartletBean;
                mOldStretchBean.setCanStretch(true);
                mChangeListener.change(mData);
                return;
            }
        }
        List<ChartletBean> list = new ArrayList<>();
        list.add(chartletBean);

        //改为  mData.add(list); 为正序
        scrollToPosition((int) (rectF.left - getLeftBorder()), 0);
        mData.add(0, list);
        setScrollY(0f);
        if (mOldStretchBean != null){
            mOldStretchBean.setCanStretch(false);
        }
        mOldStretchBean = chartletBean;
        mOldStretchBean.setCanStretch(true);
        mChangeListener.change(mData);
    }


    public void setScrollY(float scrollY){
        mOffsetY = -scrollY;
        invalidate();
    }


    /**
     * 设置scrollView偏移量
     * @param scrollX 偏移量, 默认是0
     */
    public void setScrollX(int scrollX){
        ((MyHorizontalScrollView) getParent().getParent()).setScrollX(scrollX);
    }

    /**
     * time    : 2019-08-04 10:13
     * desc    : 平滑滚动
     * versions: 1.0
     */
    public void scrollToPosition(int x, int y){
        ((MyHorizontalScrollView) getParent().getParent()).scrollToPosition(x, y);
    }

    public void setImageCount(int count){
        mImageCount = count;
    }


    /**
     * time    : 2019-08-03 17:05
     * desc    : 通过对象找到所在的列
     * versions: 1.0
     */
    public int findBeanCol(ChartletBean bean){
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).contains(bean)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * time    : 2019-08-03 17:16
     * desc    : 滚动到贴图的位    置
     * versions: 1.0
     */
    public void scrollToBean(ChartletBean bean){
        setScrollY(getColGap() * findBeanCol(bean));
    }


    /**
     * time    : 2019-08-03 17:05
     * desc    : 通过对象找到所在的行
     * versions: 1.0
     */
    public int findBeanRow(ChartletBean bean){
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).contains(bean)) {
                return mData.indexOf(bean);
            }
        }
        return -1;
    }


    /**
     * time    : 2019-08-03 17:00
     * desc    : 删除选中贴图
     * versions: 1.0
     */
    public void delete() {
        if (mOldStretchBean != null){
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).contains(mOldStretchBean)) {
                    mData.get(i).remove(mOldStretchBean);
                    resetStretch();
                    invalidate();
                    if (mChangeListener != null){
                        mChangeListener.change(mData);
                    }
                    return;
                }
            }
        }
    }

    public interface OnChartletChangeListener{
        void change(List<List<ChartletBean>> list);
    }

    public interface OnLongDragListener{


        /**
         * 在点击贴图时
         * @param bean 对象
         * @param frameStatus 拉伸框状态, true = 打开拉伸框, falase = 关闭拉伸框
         */
        void onClick(ChartletBean bean, boolean frameStatus);


        /**
         * 在长按贴图时
         * @param col 列, 竖排
         * @param row 行, 横排
         * @param bean 对象
         */
        void onLongClick(ChartletBean bean, int col, int row);


        /**
         * 在拉伸贴图时
         * @param bean 对象
         * @param left 左边的坐标
         * @param right 右边的坐标
         * @param widht 宽度
         */
        void onStretchProcess(ChartletBean bean, float left, float right, float widht);


        /**
         * 在拉伸贴图完成时
         * @param bean 对象
         * @param left 左边的坐标
         * @param right 右边的坐标
         * @param widht 宽度
         */
        void onStretchFinish(ChartletBean bean, float left, float right, float widht);


        /**
         * 在长按拖拽完成时
         * @param newCol 新的列
         * @param bean 对象
         */
        void onDragFinish(ChartletBean bean, int newCol);


        /**
         * 当前X的滚动偏移量
         * @param ScrollX X偏移量
         */
        void onScrollX(int ScrollX);
    }
    public void setOnChartletChangeListener(OnChartletChangeListener listener) {
        mChangeListener = listener;
    }

    public void setOnLongDragListener(OnLongDragListener listener){
        mDragListener = listener;
    }
}
