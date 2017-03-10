package com.example.qman.myapplication.utils;

import com.example.qman.myapplication.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by Qman on 2017/3/7.
 */



/**
 * @author gao_chun
 * 自定义标题栏
 */
public class TitleActivity extends Activity{// implements OnClickListener
    private Toolbar toolbar;                             //定义toolbar
    TextView mToolbarTitle;
    private boolean isMenuShuffle = false;               //判断是否显示toolbar上的随机菜单
    /**
     * 初始化Toolbar，并设置Toolbar中的菜单与标题，并与DrawerLayout.DrawerListener相关联，设置动态图标
     */
   /* public void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }
    public void initToolbar(String title){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarTitle(title);
    }

    public void setToolbarTitle(String title) {
        if (toolbar != null){
            mToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            mToolbarTitle.setVisibility(View.VISIBLE);
            mToolbarTitle.setText(title);
        }

    }

    public String getToolbarTitle(){
        String ret = null;
        if (toolbar != null && mToolbarTitle != null) {
            ret = mToolbarTitle.getText().toString();
        }
        return ret;
    }*/
}