package com.example.qman.myapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
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
import com.example.qman.myapplication.R;

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
    class VolleyLoadPicture {

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
