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
        setDefaultFragment();
    }

    // 设置默认的Fragment
    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mArea = new AreaFragment();
        transaction.replace(R.id.id_content, mArea);
        transaction.commit();
    }


    @Override
    public void onClick(View v)
    {
        /**
         * tab页切换
         */
        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (v.getId())
        {
            case R.id.tab01:
                if (mArea == null)
                {
                    mArea = new AreaFragment();
                }
                transaction.replace(R.id.id_content, mArea);
                break;
            case R.id.tab02:
                if (mMap == null)
                {
                    mMap = new MapFragment();
                }
                transaction.replace(R.id.id_content, mMap);
                break;
            case R.id.tab03:
                if (mSetting == null)
                {
                    mSetting = new SettingFragment();
                }
                // 使用当前Fragment的布局替代id_content的控件
                transaction.replace(R.id.id_content, mSetting);
                break;
            case R.id.tab04:
               /* if (mMap == null)
                {
                    mMap = new MapFragment();
                }
                // 使用当前Fragment的布局替代id_content的控件
                transaction.replace(R.id.id_content, mMap);*/
                break;
        }
        // transaction.addToBackStack();
        // 事务提交
        transaction.commit();
    }

}
