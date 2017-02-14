package com.example.qman.myapplication.indextab;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import com.example.qman.myapplication.R;
import com.example.qman.myapplication.indextab.ContentFragment;
//import com.example.qman.myapplication.R;

public class IndexTabMainActivity extends Activity implements OnClickListener
{
    private LinearLayout mTabWeixin;
    private LinearLayout mTabFriend;
    private LinearLayout mTabMap;
    private ContentFragment mWeixin;
    private FriendFragment mFriend;
    private MapFragment mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_indextab);

        // 初始化控件和声明事件
        mTabWeixin = (LinearLayout) findViewById(R.id.tab01);
        mTabFriend = (LinearLayout) findViewById(R.id.tab02);
        mTabMap = (LinearLayout) findViewById(R.id.tab04);
        mTabWeixin.setOnClickListener(this);
        mTabFriend.setOnClickListener(this);
        mTabMap.setOnClickListener(this);
        // 设置默认的Fragment
        setDefaultFragment();
    }

    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mWeixin = new ContentFragment();
        transaction.replace(R.id.id_content, mWeixin);
        transaction.commit();
    }

    @Override
    public void onClick(View v)
    {
        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();

        switch (v.getId())
        {
            case R.id.tab01:
                if (mWeixin == null)
                {
                    mWeixin = new ContentFragment();
                }
                // 使用当前Fragment的布局替代id_content的控件
                transaction.replace(R.id.id_content, mWeixin);
                break;
            case R.id.tab02:
                if (mFriend == null)
                {
                    mFriend = new FriendFragment();
                }
                transaction.replace(R.id.id_content, mFriend);
                break;
            case R.id.tab03:
                if (mMap == null)
                {
                    mMap = new MapFragment();
                }
                // 使用当前Fragment的布局替代id_content的控件
                transaction.replace(R.id.id_content, mMap);
                break;
            case R.id.tab04:
                if (mMap == null)
                {
                    mMap = new MapFragment();
                }
                transaction.replace(R.id.id_content, mMap);
                break;
        }
        // transaction.addToBackStack();
        // 事务提交
        transaction.commit();
    }

}
