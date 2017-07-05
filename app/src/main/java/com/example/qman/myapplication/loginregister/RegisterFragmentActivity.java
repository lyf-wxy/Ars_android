package com.example.qman.myapplication.loginregister;

import android.app.Activity;
import android.os.Bundle;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.MD5Util;
import com.example.qman.myapplication.utils.RequestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterFragmentActivity extends Activity
{
    private Button goserviceBtn;
    private EditText username;
    private EditText password;
    private EditText repassword;
    private EditText phone;

    private String usernameInput = null;
    private String passwordInput = null;
    private String repasswordInput = null;
    private String phoneInput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        //初始化组件
        username = (EditText) findViewById(R.id.usernameEt);
        password = (EditText) findViewById(R.id.passdEt);
        repassword = (EditText) findViewById(R.id.repassdEt);
        phone = (EditText) findViewById(R.id.phoneEt);
        goserviceBtn = (Button) findViewById(R.id.goserviceBtn);

        //点击输入框时将输入框内容清空
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText(null);
            }
        });
        //点击输入框时将输入框内容清空
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setText(null);
            }
        });
        //点击输入框时将输入框内容清空
        repassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repassword.setText(null);
            }
        });
        //点击输入框时将输入框内容清空
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone.setText(null);
            }
        });
        //监听按钮事件
        goserviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goserviceBtn.setBackgroundResource(R.drawable.bt_goservice_click);

                usernameInput = username.getText().toString().trim();
                passwordInput = password.getText().toString().trim();
                repasswordInput = repassword.getText().toString().trim();
                phoneInput = phone.getText().toString().trim();
                if (TextUtils.isEmpty(usernameInput) || TextUtils.isEmpty(passwordInput) || TextUtils.isEmpty(repasswordInput) || TextUtils.isEmpty(phoneInput)) {
                    ActivityUtil.toastShow(RegisterFragmentActivity.this,"请完善所有信息");
                    return;
                } else {
                    if (!passwordInput.equals(repasswordInput)) {
                        ActivityUtil.toastShow(RegisterFragmentActivity.this, "两次输入的密码必须一致");
                        return;
                    }
                }

                //将当前页面的参数带到第二个页面
                Map<String,Object> params = new HashMap<String,Object>();
                params.put("username",usernameInput);
                params.put("paswd",MD5Util.getMD5String(passwordInput));
                params.put("phone",phoneInput);
                ActivityUtil.switchTo(RegisterFragmentActivity.this, RegisterServiceFragmentActivity.class,params);

            }
        });
    }

}
