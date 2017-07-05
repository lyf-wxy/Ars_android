package com.example.qman.myapplication.loginregister;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.ActivityUtil;

public class RegisterOneActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_register_one);

    }

}
