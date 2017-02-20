package com.example.qman.myapplication.loginregister;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.ActivityUtil;

public class RegisterFragmentActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        //默认进入FragmentOne，注册的第一步
        ActivityUtil.switchToFragment(RegisterFragmentActivity.this,new FragmentOne(),R.id.id_content);

    }

}
