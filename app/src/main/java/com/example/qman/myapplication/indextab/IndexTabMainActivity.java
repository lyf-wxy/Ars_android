package com.example.qman.myapplication.indextab;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;


import com.example.qman.myapplication.R;
import com.example.qman.myapplication.areatab.AreaFragment;
import com.example.qman.myapplication.utils.ActivityUtil;
//import com.example.qman.myapplication.R;

public class IndexTabMainActivity extends Activity implements OnClickListener
{
    private LinearLayout mTabSetting;
    private LinearLayout mTabArea;
    private LinearLayout mTabMap;
    private SettingFragment mSetting;
    private AreaFragment mArea;
    private MapFragment mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_indextab);

        // 初始化控件和声明事件
        mTabSetting = (LinearLayout) findViewById(R.id.tab03);
        mTabArea = (LinearLayout) findViewById(R.id.tab01);
        mTabMap = (LinearLayout) findViewById(R.id.tab02);
        mTabSetting.setOnClickListener(this);
        mTabArea.setOnClickListener(this);
        mTabMap.setOnClickListener(this);
        // 设置默认的Fragment
        mArea = new AreaFragment();
        ActivityUtil.setDefaultFragment(IndexTabMainActivity.this, mArea);
    }

    @Override
    public void onClick(View v)
    {
        /**
         * tab页切换
         */
        switch (v.getId())
        {
            case R.id.tab01:
                if (mArea == null)
                {
                    mArea = new AreaFragment();
                }
                ActivityUtil.switchContent(IndexTabMainActivity.this, mArea);
                break;
            case R.id.tab02:
                if (mMap == null)
                {
                    mMap = new MapFragment();
                }
                ActivityUtil.switchContent(IndexTabMainActivity.this, mMap);
                break;
            case R.id.tab03:

                if (mSetting == null)
                {
                    mSetting = new SettingFragment();
                }
                ActivityUtil.switchContent(IndexTabMainActivity.this, mSetting);
                break;
            case R.id.tab04:
                break;
        }
    }

}
