package com.example.qman.myapplication;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yangtao on 2016/12/11.
 * 从资产目录中获取json字符串
 */

public class JsonUtils {
    public static String getJsonString(Context context,String name){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AssetManager am = context.getAssets();
        try {
            InputStream inputStream = am.open(name);
            byte[] buffer = new byte[1024];
            int len =0;
            while((len = inputStream.read(buffer)) != -1){
                baos.write(buffer,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toString();
    }
}
