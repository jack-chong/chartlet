package com.jack.chartlet.bean;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * author : jack(黄冲)
 * e-mail : 907755845@qq.com
 * create : 2019-08-03
 * desc   :
 */
public class JRectF extends RectF {

    public RectF drawRect;

    public float drawLeft;
    public float drawTop;
    public float drawRight;
    public float drawBottom;

    public JRectF() {
    }

    public JRectF(float left, float top, float right, float bottom) {
        super(left, top, right, bottom);
    }

    public JRectF(RectF r) {
        super(r);
    }

    public JRectF(Rect r) {
        super(r);
    }


}
