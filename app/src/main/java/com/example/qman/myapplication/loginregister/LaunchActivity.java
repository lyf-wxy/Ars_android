package com.example.qman.myapplication.loginregister;

import android.app.Notification;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.example.qman.myapplication.R;

import com.example.qman.myapplication.utils.Util;
import com.example.qman.myapplication.utils.Variables;

import java.io.IOException;

import static com.example.qman.myapplication.utils.Util.CreateFileDir;


/**
 * Created by lyf on 18/04/2017.
 */

public class LaunchActivity extends AppCompatActivity {
    private static final int SHOW_TIME_MIN = 3000;

    String mNotiRing = "ars.ogg";
    String mNotiRingPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_activity);

        new InitPushNotification().execute();
    }

    class InitPushNotification extends AsyncTask<Void,Void,Integer>{
        @Override
        protected Integer doInBackground(Void... params) {
            int result=0;
            long startTime = System.currentTimeMillis();
            /*
        *创建通知铃声
        */
            try
            {
                String FileDir = CreateFileDir("Arsandroid");
                mNotiRingPath = FileDir+ "/" + mNotiRing;

                Util.CreateFile(getApplicationContext(),mNotiRingPath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
             /*
        设置通知栏
         */
            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Variables.api_key);

//        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
//                resource.getIdentifier(
//                        "notification_custom_builder", "layout", pkgName),
//                resource.getIdentifier("notification_icon", "id", pkgName),
//                resource.getIdentifier("notification_title", "id", pkgName),
//                resource.getIdentifier("notification_text", "id", pkgName));
            //cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
            //cBuilder.setLayoutDrawable(resource.getIdentifier("earth", "drawable", pkgName));
            //cBuilder.setNotificationSound(Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6").toString());
//
            CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(R.layout.notification_custom_builder,R.id.notification_icon,R.id.notification_title,R.id.notification_text);

            cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
            cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
            cBuilder.setStatusbarIcon(R.drawable.earth);
            cBuilder.setLayoutDrawable(R.drawable.earth);


//        cBuilder.setNotificationSound(Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6").toString());
            long[] vibrates = { 0, 1000, 1000, 1000 };
            cBuilder.setNotificationVibrate(vibrates);

            cBuilder.setNotificationSound(mNotiRingPath);
            Log.d("music",mNotiRingPath);
            // 推送高级设置，通知栏样式设置为下面的ID
            PushManager.setNotificationBuilder(getApplicationContext(), 1, cBuilder);

            long loadingTime = System.currentTimeMillis() - startTime;

            if (loadingTime < SHOW_TIME_MIN) {
                try {
                    Thread.sleep(SHOW_TIME_MIN - loadingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return result;

        }
        @Override
        protected void onPostExecute(Integer s) {
            //跳转至 MainActivity
            Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
            startActivity(intent);
            //结束当前的 Activity
            LaunchActivity.this.finish();
        }
    }
}
