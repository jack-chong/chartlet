package com.jack.chartlet.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * author : jack(黄冲)
 * e-mail : 907755845@qq.com
 * create : 2019-07-31
 * desc   :
 */
public class ImageUtils {

    public static Bitmap scale(Bitmap bitmap, int w, int h){
        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 设置想要的大小
        // 计算缩放比例
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
                true);
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

}
