package com.example.qman.myapplication.indextab;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private SettingFragment mSetting;
    private SettingUserFragment mSettingUser;
    private AreaFragment mArea;
    private MapFragment mMap;

    private TextView title;
    private Button toolbar_search;
    private Button toolbar_add;
    private Button toolbar_draw;

    private ImageButton tab01_btn;
    private ImageButton tab02_btn;
    private ImageButton tab03_btn;

    private TextView tab01_word;
    private TextView tab02_word;
    private TextView tab03_word;

    private  ColorStateList csl_unselect;
    private  ColorStateList csl_select;
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
        //mSettingUser = new SettingUserFragment();

        toolbar_add = (Button) findViewById(R.id.toolbar_add);
        toolbar_search = (Button) findViewById(R.id.toolbar_search);
        title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_draw = (Button) findViewById(R.id.toolbar_draw);

        tab01_btn = (ImageButton) findViewById(R.id.tab01_btn);
        tab02_btn = (ImageButton) findViewById(R.id.tab02_btn);
        tab03_btn = (ImageButton) findViewById(R.id.tab03_btn);

        tab01_word = (TextView) findViewById(R.id.tab01_word);
        tab02_word = (TextView) findViewById(R.id.tab02_word);
        tab03_word = (TextView) findViewById(R.id.tab03_word);
//        ActivityUtil.setOnlyVisibilitys(title, toolbar_search, toolbar_add,toolbar_draw);
//        ActivityUtil.setTitle(IndexTabMainActivity.this,R.id.toolbar_title,"区域");
        ActivityUtil.setDefaultFragment(IndexTabMainActivity.this, mArea);
        tab01_btn.setBackgroundResource(R.drawable.area_select);
        tab02_btn.setBackgroundResource(R.drawable.map);
        tab03_btn.setBackgroundResource(R.drawable.setting);

        Resources resource = (Resources) getBaseContext().getResources();
        csl_unselect = (ColorStateList) resource.getColorStateList(R.color.deepgray);
        csl_select = (ColorStateList) resource.getColorStateList(R.color.white);
        if (csl_unselect != null) {
            tab01_word.setTextColor(csl_select);
            tab02_word.setTextColor(csl_unselect);
            tab03_word.setTextColor(csl_unselect);
        }
  /*      TextView tv = (TextView) findViewById(R.id.tab03_word);
        tv.setTextColor(this.getResources().getColor(R.color.white));
        ImageView iv = (ImageView)findViewById(R.id.tab03_btn);
        iv.setBackgroundResource(R.drawable.setting_select);*/
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

                //ActivityUtil.setAllVisibilitys(title, toolbar_search, toolbar_add,toolbar_draw);
                //需要从AreaItemFragment开始将回退栈清空
               tab01_btn.setBackgroundResource(R.drawable.area_select);
               tab02_btn.setBackgroundResource(R.drawable.map);
               tab03_btn.setBackgroundResource(R.drawable.setting);

                tab01_word.setTextColor(csl_select);
                tab02_word.setTextColor(csl_unselect);
                tab03_word.setTextColor(csl_unselect);
                ActivityUtil.switchContentReplace(IndexTabMainActivity.this, mArea);
                break;
            case R.id.tab02:
                if (mMap == null)
                {
                    mMap = new MapFragment();
                }

                //ActivityUtil.setOnlyVisibilitys(title, toolbar_search, toolbar_add,toolbar_draw);
                tab01_btn.setBackgroundResource(R.drawable.area);
               tab02_btn.setBackgroundResource(R.drawable.map_select);
               tab03_btn.setBackgroundResource(R.drawable.setting);

                tab01_word.setTextColor(csl_unselect);
                tab02_word.setTextColor(csl_select);
                tab03_word.setTextColor(csl_unselect);
                ActivityUtil.switchContentReplace(IndexTabMainActivity.this, mMap );
                break;
            case R.id.tab03:

                if (mSettingUser == null)
                {
                    mSettingUser = new SettingUserFragment();
                }

                //ActivityUtil.setOnlyVisibilitys(title, toolbar_search, toolbar_add,toolbar_draw);
                tab01_btn.setBackgroundResource(R.drawable.area);
                tab02_btn.setBackgroundResource(R.drawable.map);
                tab03_btn.setBackgroundResource(R.drawable.setting_select);

                tab01_word.setTextColor(csl_unselect);
                tab02_word.setTextColor(csl_unselect);
                tab03_word.setTextColor(csl_select);
                ActivityUtil.switchContentReplace(IndexTabMainActivity.this, mSettingUser);
                break;
        }
    }

}
