package com.jack.chartlet.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jack.chartlet.R;
import com.jack.chartlet.bean.ChartletBean;
import com.jack.chartlet.utils.AutoSizeUtils;
import com.jack.chartlet.view.ChartletView;
import com.jack.chartlet.view.NavigationLineView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

/**
 * author : jack(黄冲)
 * e-mail : 907755845@qq.com
 * create : 2019-07-31
 * desc   :
 */
public class ChartletFragment extends Fragment {

    private View mContentView;
    private ChartletView mChartletView;
    private NavigationLineView mLineView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = View.inflate(getContext(), R.layout.fragment_chartle,null);
        init();
        return mContentView;
    }

    private void init() {

        List<Bitmap> imageList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            Bitmap bitmap = Bitmap.createBitmap(ChartletView.IMAGE_WIDTH, ChartletView.IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Random random = new Random();
            int a = 80;
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            ColorDrawable colorDrawable = new ColorDrawable(Color.argb(a, r, g, b));
            colorDrawable.setBounds(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
            colorDrawable.draw(canvas);

            imageList.add(bitmap);
        }
        mChartletView = (ChartletView) mContentView.findViewById(R.id.chartletView);

        mLineView = mContentView.findViewById(R.id.navigationLineView);;
        mLineView.setImageList(imageList);
        mChartletView.setOnChartletChangeListener(list -> {
            mLineView.notificationChartletChange(list);
        });

        mLineView.post(() -> {
            int measuredWidth = mLineView.getMeasuredWidth();
            mChartletView.setMinimumWidth(measuredWidth);
        });

    }



    public void addData(ChartletBean bean) {
        mChartletView.addData(bean);
    }


    public void delete() {
        mChartletView.delete();
    }
}
