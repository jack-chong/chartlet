package com.jack.chartlet.bean;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.style.TtsSpan;

/**
 * author : jack(黄冲)
 * e-mail : 907755845@qq.com
 * create : 2019-07-31
 * desc   :
 */
public class ChartletBean {

    public enum ChartletType{
        IMAGE(0),
        TEXT(1),
        IMAGE_AND_TEXT(2);

        int type;
        ChartletType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    //贴图类型
    private ChartletType type;

    //贴图背景色
    private int backColor;

    //小图标
    private Bitmap bitmap;

    //小图标路径
    private String iconPath;

    //文字
    private String text;

    //所在位置
    private RectF backRectF;

    //是否显示拉伸框
    private boolean canStretch;

    //是否显示长按, 长按后隐藏并创建一个临时的
    private boolean canLongClick;


    public ChartletBean(String iconPath) {
        this.iconPath = iconPath;
        type = ChartletType.IMAGE;
        this.backColor = Color.parseColor("#A14F55");
    }

    public ChartletBean(CharSequence text) {
        this.text = text.toString();
        this.type = ChartletType.TEXT;
        this.backColor = Color.parseColor("#ff788d6c");
    }

    public ChartletBean(String iconPath, String text) {
        this.text = text;
        this.iconPath = iconPath;
        this.type = ChartletType.IMAGE_AND_TEXT;
        this.backColor = Color.parseColor("#ff788d6c");
    }


    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public RectF getBackRectF() {
        return backRectF;
    }

    public void setBackRectF(RectF backRectF) {
        this.backRectF = backRectF;
    }

    public boolean isCanStretch() {
        return canStretch;
    }

    public void setCanStretch(boolean canStretch) {
        this.canStretch = canStretch;
    }

    public boolean isCanLongClick() {
        return canLongClick;
    }

    public void setCanLongClick(boolean canLongClick) {
        this.canLongClick = canLongClick;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ChartletType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getBackColor() {
        return backColor;
    }
}
