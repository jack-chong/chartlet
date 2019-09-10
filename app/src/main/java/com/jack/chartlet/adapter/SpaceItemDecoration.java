package com.jack.chartlet.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 作者: jack(黄冲)
 * 邮箱: 907755845@qq.com
 * create on 2018/12/29 14:17
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

    private int left, top, right, bottom;

    public SpaceItemDecoration() {
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = left;
        outRect.right = right;
        outRect.bottom = bottom;
        outRect.top = top;

        if (parent.getChildAdapterPosition(view) != 0) {

        }

    }

    public SpaceItemDecoration(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
}
