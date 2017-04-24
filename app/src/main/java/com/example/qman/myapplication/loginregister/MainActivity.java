package com.example.qman.myapplication.loginregister;

import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.TextUtils;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.esri.android.runtime.ArcGISRuntime;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.MD5Util;
import com.example.qman.myapplication.utils.RequestUtil;
import com.example.qman.myapplication.utils.Util;
import com.example.qman.myapplication.utils.Variables;
import com.example.qman.myapplication.indextab.IndexTabMainActivity;
import com.example.qman.myapplication.utils.ActivityUtil;



import android.provider.MediaStore.Audio;

import static android.view.View.Y;
import static com.example.qman.myapplication.utils.Util.CreateFileDir;

/*
登录页
*/
public class MainActivity extends AppCompatActivity {
    JSONObject jsonObject = null;//利用json字符串生成json对象
    private EditText username = null;
    private EditText password = null;
    private Button login;
    private TextView register;
    private String usernameInput = null;
    private String passwordInput = null;
    int counter = 3;

    String mNotiRing = "ars.ogg";
    String mNotiRingPath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        try
//        {
//            String FileDir = CreateFileDir("Arsandroid");
//            mNotiRingPath = FileDir+ "/" + mNotiRing;
//
//            Util.CreateFile(getApplicationContext(),mNotiRingPath);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//
//        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,Variables.api_key);
//
//
//
//        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(R.layout.notification_custom_builder,R.id.notification_icon,R.id.notification_title,R.id.notification_text);
//
//        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
//        cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
//        cBuilder.setStatusbarIcon(R.drawable.earth);
//        cBuilder.setLayoutDrawable(R.drawable.earth);
//
//
//        long[] vibrates = { 0, 1000, 1000, 1000 };
//        cBuilder.setNotificationVibrate(vibrates);
//
//        cBuilder.setNotificationSound(mNotiRingPath);
//        Log.d("music",mNotiRingPath);
//
//        PushManager.setNotificationBuilder(this, 1, cBuilder);

        //初始化组件
        initView();
        //登陆按钮事件
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
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username",usernameInput);
                        jsonObject.put("password", MD5Util.getMD5String(passwordInput));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestUtil.request(jsonObject.toString(),"AndroidService/loginService",callback);
                }

            }
        });
        //去注册点击事件
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.switchTo(MainActivity.this,RegisterFragmentActivity.class);
            }
        });
    }

    /**
     * 初始化组件
     */
    private void initView() {
        username = (EditText) findViewById(R.id.usernameEt);
        password = (EditText) findViewById(R.id.pwdEt);
        login = (Button) findViewById(R.id.loginBtn);
        register = (TextView) findViewById(R.id.registerBtn);
    }

    //请求后的回调接口
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            ActivityUtil.toastShow(MainActivity.this,e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String str = response.body().string();
            try {
                jsonObject = new JSONObject(str);
                String result =  jsonObject.getString("result");//解析json查询结果
                if(result.equals("success")){
                    //登陆成功，带参数跳转到首页
                    Map<String,Object> params = new HashMap<String,Object>();
                    params.put("id",jsonObject.getString("id"));
                    params.put("username",jsonObject.getString("username"));
                    params.put("paswd",jsonObject.getString("paswd"));
                    params.put("beginDate",jsonObject.getString("beginDate"));
                    params.put("endDate",jsonObject.getString("endDate"));
                    params.put("producttype",jsonObject.getString("producttype"));
                    if(jsonObject.getString("locno") != null)
                        params.put("locno",jsonObject.getString("locno"));
                    ActivityUtil.switchTo(MainActivity.this, IndexTabMainActivity.class,params);
                } else {
                    ActivityUtil.toastShow(MainActivity.this,"用户名或密码错误，还剩" + counter + "次机会");
                    counter--;
                    if (counter == 0) {
                        //尝试次数超过三次，进行账户处理
                        ActivityUtil.toastShow(MainActivity.this,"尝试次数超过3次，该账户锁定");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}