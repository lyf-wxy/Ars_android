package com.example.qman.myapplication.utils;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Qman on 2017/2/15.
 */

public class RequestUtil extends AppCompatActivity {
    private static JSONObject jsonObject = null;//利用json字符串生成json对象
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * 向后台发请求，异步，不需要等待
     *
     * @param json
     * @param url
     * @param callback
     * @return
     */
    public static void request(String json, String url, Callback callback) {
        //把请求的内容字符串转换为json
        jsonObject = null;
        try {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(Variables.serviceIP + url)
                    .post(body)
                    .build();
            okHttpClient.newCall(request).enqueue(callback);//callback是请求后的回调接口
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Exception", e.getMessage());
        }
    }

    /**
     * 向后台发请求，同步，需要等待
     *
     * @param json
     * @param url
     */
    public static String request(String json, String url) {
        String str = "";
        jsonObject = null;
        //把请求的内容字符串转换为json
        try {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(Variables.serviceIP + url)
                    .post(body)
                    .build();
            Response response = null;
            response = okHttpClient.newCall(request).execute();

            str = response.body().string();
                /*try {
                    jsonObject = new JSONObject(str);
                    String result = jsonObject.getString("result");//解析json查询结果
                    if (result.equals("success")) {
                        return true;
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Exception", e.getMessage());
        }
        return str;
    }

    public static JSONObject getJsonObject() {
        return jsonObject;
    }
}
