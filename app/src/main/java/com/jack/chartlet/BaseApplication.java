package com.jack.chartlet;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.jack.chartlet.utils.DeviceInfo;

/**
 * author : jack(黄冲)
 * e-mail : 907755845@qq.com
 * create : 2019-07-31
 * desc   :
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initDevice();
    }


    private void initDevice() {
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        DeviceInfo.sScreenWidth = outMetrics.widthPixels;
        DeviceInfo.sScreenHeight = outMetrics.heightPixels;
        DeviceInfo.sAutoScaleX = DeviceInfo.sScreenWidth * 1.0f / DeviceInfo.UI_WIDTH;
        DeviceInfo.sAutoScaleY = DeviceInfo.sScreenHeight * 1.0f / DeviceInfo.UI_HEIGHT;
    }

}
