package com.example.qman.myapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.esri.android.map.MapView;
import com.example.qman.myapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.type;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


/**
 * Created by lyf on 13/03/2017.
 */

public class Util {

    public static final Drawable getDrawable(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {

            return ContextCompat.getDrawable(context, id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }
    public static String getMapLayerUrl(String type)
    {
        String mapType = "http://10.2.3.222:6080/arcgis/rest/services/TRSQ/ImageServer";
        if(type!=null)
        {
            switch (type)
            {
                case "TRSQ":
                    mapType = "http://10.2.3.222:6080/arcgis/rest/services/TRSQ/ImageServer";
                    break;
                case "NZWJXFL":
                    mapType = "http://10.2.3.222:6080/arcgis/rest/services/NZWJXFL/ImageServer";
                    break;
                case "YMJZSJC":
                    mapType = "http://10.2.3.222:6080/arcgis/rest/services/YMJZSJC/ImageServer";
                    break;
                case "NYBCHJC":
                    mapType = "http://10.2.3.222:6080/arcgis/rest/services/NYBCHJC/ImageServer";
                    break;
                case "NZWZSJC":
                    mapType = "http://10.2.3.222:6080/arcgis/rest/services/NZWZSJC/ImageServer";
                    break;
                case "NZWGC":
                    mapType = "http://10.2.3.222:6080/arcgis/rest/services/NZWGC/ImageServer";
                    break;
                case "TRFL":
                    mapType = "http://10.2.3.222:6080/arcgis/rest/services/TRFL/ImageServer";
                    break;
                default:
                    mapType = "http://10.2.3.222:6080/arcgis/rest/services/TRSQ/ImageServer";
                    break;
            }
            return mapType;
        }
        return null;

    }
    public static String getMapLegend(String type)
    {
        String LegendType = "http://10.2.3.222:6080/arcgis/rest/services/TRSQ/ImageServer";
        switch (type)
        {
            case "TRSQ":
                LegendType = "http://10.2.3.222:6080/arcgis/rest/services/TRSQ/ImageServer";
                break;
            case "NZWJXFL":
                LegendType = "http://10.2.3.222:6080/arcgis/rest/services/NZWJXFL/ImageServer";
                break;
            case "YMJZSJC":
                LegendType = "http://10.2.3.222:6080/arcgis/rest/services/YMJZSJC/ImageServer";
                break;
            case "NYBCHJC":
                LegendType = "http://10.2.3.222:6080/arcgis/rest/services/NYBCHJC/ImageServer";
                break;
            case "NZWZSJC":
                LegendType = "http://10.2.3.222:6080/arcgis/rest/services/NZWZSJC/ImageServer";
                break;
            case "NZWGC":
                LegendType = "http://10.2.3.222:6080/arcgis/rest/services/NZWGC/ImageServer";
                break;
            case "TRFL":
                LegendType = "http://10.2.3.222:6080/arcgis/rest/services/TRFL/ImageServer";
                break;
            default:
                LegendType = "http://10.2.3.222:6080/arcgis/rest/services/TRSQ/ImageServer";
                break;
        }
        return LegendType;
    }
    public static Bitmap getViewBitmap(MapView v) {

        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = null;
        while (cacheBitmap == null) {
            cacheBitmap = v.getDrawingMapCache(0, 0, v.getWidth(),
                    v.getHeight());
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }
    public static String saveMyBitmap(String bitName,Bitmap mBitmap){

        //String FileName=this.getInnerSDCardPath() + "/" + bitName + ".png";
        //获取当前系统时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date curDate =new Date(System.currentTimeMillis());
        String DateStr = formatter.format(curDate);
        Log.d("saveMyBitmap",DateStr);

        String FileName = null;
        try{

            String FileDir = CreateFileDir("Arsandroid");
            FileName = FileDir+ "/" + DateStr+"_"+bitName + ".png";

            Log.d("saveMyBitmap",FileName);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        //ShowMessage(FileName);
        File f = new File(FileName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("在保存"+FileName+"图片时出错：" + e.toString(),"在保存"+FileName+"图片时出错：" + e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileName;
    }

    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }
    //创建文件夹及文件
    public static String CreateFileDir(String fileDir) throws IOException {

        String fileDirectory = getSDPath()+"/"+fileDir;//文件夹路径
        File filedir = new File(fileDirectory);
        if (!filedir.exists()) {
            try {
                //按照指定的路径创建文件夹
                filedir.mkdirs();
                return fileDirectory;
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return fileDirectory;
    }
    public class VolleyLoadPicture {

        private ImageLoader mImageLoader = null;
        private BitmapCache mBitmapCache;

        private ImageListener one_listener;

        public VolleyLoadPicture(Context context,final ImageView imageView){

            //one_listener = ImageLoader.getImageListener(imageView, 0, 0);

            one_listener = new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if(response.getBitmap()!=null){
                        imageView.setImageBitmap(response.getBitmap());
                        imageView.clearAnimation();
                    }else{
                        imageView.setBackgroundResource(0); //背景图为默认的一张图
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    imageView.setImageResource(0);
                }
            };

            RequestQueue mRequestQueue = Volley.newRequestQueue(context);
            mBitmapCache = new BitmapCache();
            mImageLoader = new ImageLoader(mRequestQueue, mBitmapCache);
        }

        public ImageLoader getmImageLoader() {
            return mImageLoader;
        }

        public void setmImageLoader(ImageLoader mImageLoader) {
            this.mImageLoader = mImageLoader;
        }

        public ImageListener getOne_listener() {
            return one_listener;
        }

        public void setOne_listener(ImageListener one_listener) {
            this.one_listener = one_listener;
        }

        class BitmapCache implements ImageCache {
            private LruCache<String, Bitmap> mCache;
            private int sizeValue;

            public BitmapCache() {
                int maxSize = 10 * 1024 * 1024;
                mCache = new LruCache<String, Bitmap>(maxSize) {
                    @Override
                    protected int sizeOf(String key, Bitmap value) {
                        sizeValue = value.getRowBytes() * value.getHeight();
                        return sizeValue;
                    }

                };
            }

            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
        }


    }

}
