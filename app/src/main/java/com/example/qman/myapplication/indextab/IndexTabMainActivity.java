package com.example.qman.myapplication.indextab;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.Toast;

import android.widget.TextView;



import com.example.qman.myapplication.R;
import com.example.qman.myapplication.areatab.AreaFragment;

import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.TitleActivity;
//import com.example.qman.myapplication.R;

public class IndexTabMainActivity extends TitleActivity implements OnClickListener
{
    private LinearLayout mTabSetting;
    private LinearLayout mTabArea;
    private LinearLayout mTabMap;
    private LinearLayout mTabOther;
    private SettingFragment mSetting;
    private AreaFragment mArea;
    private MapFragment mMap;

    private BlankFragment mOther;

    private TextView title;
    private Button toolbar_search;
    private Button toolbar_add;


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
        mTabOther = (LinearLayout) findViewById(R.id.tab04);
        mTabSetting.setOnClickListener(this);
        mTabArea.setOnClickListener(this);
        mTabMap.setOnClickListener(this);
        mTabOther.setOnClickListener(this);
        // 设置默认的Fragment
        mArea = new AreaFragment();


        toolbar_add = (Button) findViewById(R.id.toolbar_add);
        toolbar_search = (Button) findViewById(R.id.toolbar_search);
        title = (TextView) findViewById(R.id.toolbar_title);

        ActivityUtil.setAllVisibilitys(title, toolbar_search, toolbar_add);
        ActivityUtil.setTitle(IndexTabMainActivity.this,R.id.toolbar_title,"区域");
        ActivityUtil.setDefaultFragment(IndexTabMainActivity.this, mArea);
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
        switch (v.getId())
        {
            case R.id.tab01:
                if (mArea == null)
                {
                    mArea = new AreaFragment();
                }

                ActivityUtil.setAllVisibilitys(title, toolbar_search, toolbar_add);
                //需要从AreaItemFragment开始将回退栈清空
                ActivityUtil.switchContentReplace(IndexTabMainActivity.this, mArea);
                break;
            case R.id.tab02:
                if (mMap == null)
                {
                    mMap = new MapFragment();
                }

                ActivityUtil.setOnlyVisibilitys(title, toolbar_search, toolbar_add);
                ActivityUtil.switchContentReplace(IndexTabMainActivity.this, mMap );
                break;
            case R.id.tab03:

                if (mSetting == null)
                {
                    mSetting = new SettingFragment();
                }

                ActivityUtil.setOnlyVisibilitys(title, toolbar_search, toolbar_add);
                ActivityUtil.switchContentReplace(IndexTabMainActivity.this, mSetting);
                break;
            case R.id.tab04:
                if (mOther == null)
                {
                    mOther = new BlankFragment();
                }

                ActivityUtil.setOnlyVisibilitys(title, toolbar_search, toolbar_add);
                //ActivityUtil.switchContentReplace(IndexTabMainActivity.this, mOther);
                break;
        }
    }

}
