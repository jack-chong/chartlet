package com.jack.chartlet;


import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.jack.chartlet.bean.ChartletBean;
import com.jack.chartlet.fragment.ChartletFragment;
import com.jack.chartlet.fragment.SelectChartletFragment;

public class MainActivity extends AppCompatActivity {

    private SelectChartletFragment mSelectFragment;
    private ChartletFragment mChartletFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mChartletFragment = new ChartletFragment();
        mSelectFragment = new SelectChartletFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout, mChartletFragment)
                .add(R.id.frameLayout, mSelectFragment)
                .hide(mSelectFragment)
                .commitAllowingStateLoss();

    }

    public void click(View view){
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mChartletFragment)
                .show(mSelectFragment)
                .commitAllowingStateLoss();
    }


    public void addText(View view){
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入文字")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    mChartletFragment.addData(new ChartletBean(et.getText()));
                }).setNegativeButton("取消",null).show();
    }

    public void textAndImage(View view){
        mChartletFragment.addData(new ChartletBean("emoji_gesture_0001.png", "的地方水电费第三方但是"));
    }

    public void delete(View view){
        mChartletFragment.delete();
    }


    public void confirm(View view){
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mSelectFragment)
                .show(mChartletFragment)
                .commitAllowingStateLoss();

        String selectivePath = mSelectFragment.getSelectivePath();
        if (selectivePath != null){
            mChartletFragment.addData(new ChartletBean(selectivePath));
        }

    }
}
