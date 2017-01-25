package com.example.qman.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private OkHttpClient okHttpClient = new OkHttpClient();
    JSONObject jsonObject = null;//利用json字符串生成json对象
    private EditText username = null;
    private EditText password = null;
    private Button login;
    private TextView register;
    private TextView tv;
    private String usernameInput = null;
    private String passwordInput = null;
    int counter = 3;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameInput = username.getText().toString().trim();
                passwordInput = password.getText().toString().trim();


                if (TextUtils.isEmpty(usernameInput) || TextUtils.isEmpty(passwordInput)) {
                    Toast.makeText(MainActivity.this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    //判断是否登陆成功
                    String json = "{'username':'" + usernameInput + "'," + "'password':'" + passwordInput + "'}";
                    //把请求的内容字符串转换为json
                    RequestBody body = RequestBody.create(JSON, json);
                    Request request = new Request.Builder()
                            .url("http://10.2.3.182:8080/AndroidService/loginService")
                            .post(body)
                            .build();
                    okHttpClient.newCall(request).enqueue(callback);//callback是请求后的回调接口

                }

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(MainActivity.this, RegisterActivity.class);
                startActivity(intent2);//红色部分为要打开的心窗口的类名
            }
        });
    }

    /**
     * 初始化组件
     */
    private void initView() {
        tv = (TextView) findViewById(R.id.textView1);
        username = (EditText) findViewById(R.id.usernameEt);
        password = (EditText) findViewById(R.id.pwdEt);
        login = (Button) findViewById(R.id.loginBtn);
        register = (TextView) findViewById(R.id.registerBtn);
    }

    public void register(View view) {
    }

    //显示调用结果
    private void setResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                //tv.setText(msg);
            }
        });
    }

    //请求后的回调接口
    private Callback callback = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {

            setResult(e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String str = response.body().string();
            try {
                jsonObject = new JSONObject(str);
                String result =  jsonObject.getString("result");//解析json查询结果
                if(result.equals("success")){
                    //登陆成功，跳转到首页
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, IndexActivity.class);
                    intent.putExtra("usernameLogin",usernameInput);
                    startActivity(intent);//红色部分为要打开的心窗口的类名
                } else {
                    setResult("用户名或密码错误，还剩" + counter + "次机会");
                    counter--;
                    if (counter == 0) {
                        //尝试次数超过三次，进行账户处理
                        setResult("尝试次数超过3次，该账户锁定");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}