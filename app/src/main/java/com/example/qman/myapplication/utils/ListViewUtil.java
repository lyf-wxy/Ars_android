package com.example.qman.myapplication.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

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

public class ListViewUtil extends AppCompatActivity {
    private static JSONObject jsonObject = null;//利用json字符串生成json对象
    private static List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient okHttpClient = new OkHttpClient();
    /**
     * 将控件选择的行政区域加到listView中
     * @param ordername
     * @param sdpath
     * @param geometry
     * @return
     */
    public static HashMap<String,Object> addData(String ordername,String sdpath,String geometry){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("ordername", ordername);
        map.put("sdpath", sdpath);
        map.put("geometry", geometry);
        data.add(map);
        return  map;
    }
    /**
     * 初始化listView数据
     * @return
     */
    public static List<HashMap<String, Object>> initSplitData(String userid, String codeidStr){
        data.clear();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("codeid", codeidStr);
            jsonObject.put("userid", userid);
            String str = RequestUtil.request(jsonObject.toString(),"AndroidService/cityInfoService");
            JSONObject jsonObjectResult = new JSONObject(str);
            String result = jsonObjectResult.getString("result");//解析json查询结果
            if (result.equals("success")) {
                String dataStr = jsonObjectResult.getString("data");
                JSONArray areaLists = new JSONArray(dataStr);
                if (areaLists.length()>0) {
                    for (int i=0;i<areaLists.length();i++) {
                        JSONArray aArea = new JSONArray(areaLists.get(i).toString());

                        addData(aArea.get(0).toString(),aArea.get(1).toString(),aArea.get(2).toString());
                       /* HashMap<String, Object> listm = new HashMap<String, Object>();
                        listm.put("ordername", aArea.get(0).toString());
                        listm.put("sdpath", aArea.get(1).toString());
                        listm.put("geometry", aArea.get(2).toString());*/

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //String codeIdJson = "{'codeid':'" + codeidStr + "'}";
        //把请求的内容字符串转换为json
        /*RequestBody body = RequestBody.create(JSON, codeIdJson);
        Request request = new Request.Builder()
                .url(Variables.serviceIP+"AndroidService/cityInfoService")
                .post(body)
                .build();
    //    okHttpClient.newCall(request).enqueue(callbackCityInfo);//callback是请求后的回调接口
        //由原来的异步访问服务器方式修改为同步访问服务器方式
        //修改后，该方法需要在新线程中调用
       try {
            Response response = null;
            response = okHttpClient.newCall(request).execute();
           String str = response.body().string();
           try {
               jsonObject = new JSONObject(str);
               String result =  jsonObject.getString("result");//解析json查询结果
               if(result.equals("success")){
                   String dataStr = jsonObject.getString("data");
                   JSONArray areaLists = new JSONArray(dataStr);
                   if (areaLists.length()>0) {
                       for (int i=0;i<areaLists.length();i++) {
                           JSONArray aArea = new JSONArray(areaLists.get(i).toString());
                           HashMap<String, Object> listm = new HashMap<String, Object>();
                           listm.put("ordername", aArea.get(0).toString());
                           listm.put("sdpath", aArea.get(1).toString());
                           listm.put("geometry", aArea.get(2).toString());
                           data.add(listm);
                       }
                   }
               } else {
                   //setResult("注册失败");
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return data;
    }

    //请求后的回调接口
    private static Callback callbackCityInfo = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            // setResult(e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String str = response.body().string();
            try {
                jsonObject = new JSONObject(str);
                String result =  jsonObject.getString("result");//解析json查询结果
                if(result.equals("success")){
                    String dataStr = jsonObject.getString("data");
                    JSONArray areaLists = new JSONArray(dataStr);
                    if (areaLists.length()>0) {
                        for (int i=0;i<areaLists.length();i++) {
                            JSONArray aArea = new JSONArray(areaLists.get(i).toString());
                            HashMap<String, Object> listm = new HashMap<String, Object>();
                            listm.put("province", aArea.get(0).toString());
                            listm.put("city", aArea.get(1).toString());
                            listm.put("area", aArea.get(2).toString());
                            data.add(listm);
                        }
                    }
                } else {
                    //setResult("注册失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
