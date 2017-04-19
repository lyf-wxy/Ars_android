package com.example.qman.myapplication.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.TextSymbol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lyf on 21/02/2017.
 */

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    public boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    //flag for WIFi
    boolean isWifiEnabled = false;


    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    static String address = "";
    static Point mPoint;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
//  private static final long MIN_TIME_BW_UPDATES = 1000 ; // 1 sec

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            isWifiEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled && !isWifiEnabled) {
                // no network provider is enabled
                Log.d("allproviderenable", "allproviderenable");
            } else {

                this.canGetLocation = true;


                if (mContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d("GPSTracker", "getLocation denied");
                    return null;
                }

                if (isNetworkEnabled) {


                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("isNetworkEnabled", "Network");
                    if (locationManager != null) {

                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                else if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("isGPSEnabled", "isGPSEnabled");
                    if (locationManager != null) {
                        //Log.d("isGPSEnabled", "isGPSEnabled");
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            //Log.d("isGPSEnabled", "isGPSEnabled");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                        }
                    }

                }


                else if (isWifiEnabled) {

                    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("isWifiEnabled", "isWifiEnabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                        }
                    }

                }
                if (String.valueOf(latitude).equalsIgnoreCase("0.0") || String.valueOf(longitude).equalsIgnoreCase("0.0")) {
                    //showSettingsAlert();
                    canGetLocation = false;
                    Log.d("canGetLocation", "false");
                }


            }

        } catch (Exception e) {
            Log.d("Exception11", e.getMessage());
        }
        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Please turn ON GPS.");

        // On pressing Settings button
        alertDialog.setNeutralButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    /*
       通过经纬度获取地名
        */
    public static String getPositionNamebyLatLon(final Point point,final Activity activity,final GraphicsLayer graphicsLayerPosition, final MapView mMapView)
    {

        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        String locationStr = String.valueOf(point.getY())+","+String.valueOf(point.getX());

        //Toast.makeText(getActivity(), locationStr, Toast.LENGTH_SHORT).show();

        final Request request = new Request.Builder()
                .url("http://api.map.baidu.com/geocoder/v2/?location="+locationStr+"&output=json&pois=0&ak="+Variables.BaiduAK+"&mcode="+ Variables.Mcode)
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String str = response.body().string();
                try
                {
                    JSONObject jsonObject = new JSONObject(str);
                    address = jsonObject.getJSONObject("result").getString("formatted_address");


                    addAddressSymbol2Map(activity,address,point,graphicsLayerPosition,mMapView);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });

        return address;
    }
     /*
    将地名信息添加在地图上
     */

    public  static void addAddressSymbol2Map(final Activity activity, final String message, final Point point, final GraphicsLayer graphicsLayerPosition, final MapView mMapView){
        activity.runOnUiThread(new Runnable() {
            public void run() {

                final TextSymbol positionTextSys = new TextSymbol(10,message, Color.RED);
                positionTextSys.setFontFamily("DroidSansFallback.ttf");
                //图层的创建
                Point positionProj = (Point) GeometryEngine.project(point, SpatialReference.create(4326),mMapView.getSpatialReference());
                Graphic graphicPoint = new Graphic(positionProj,positionTextSys);

                graphicsLayerPosition.addGraphic(graphicPoint);
                Log.d("addAddressSymbol2Map",message);
                //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*
   通过地名获取经纬度
    */
    public static Point getLatLonbyPositionName(String address,final Activity activity,final MapView mMapView)
    {
        //mPoint = new Point();
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request

        final Request request = new Request.Builder()
                .url("http://api.map.baidu.com/geocoder/v2/?address="+address+"&output=json&pois=0&ak="+Variables.BaiduAK+"&mcode="+ Variables.Mcode)
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //mPoint.setXY(0,0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String str = response.body().string();
                try
                {
                    JSONObject jsonObject = new JSONObject(str);

                    double lon = jsonObject.getJSONObject("result").getJSONObject("location").getDouble("lng");
                    double lat = jsonObject.getJSONObject("result").getJSONObject("location").getDouble("lat");
                    Log.d("point",String.valueOf(lon)+","+String.valueOf(lat));
                    //mPoint.setXY(lon,lat);
                    MapCenter2LatLon(activity,mMapView,lat,lon);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });
        return mPoint;
    }
    /*
     将map缩放到指定经纬度
     */

    public  static void MapCenter2LatLon(final Activity activity, final MapView mapView,final double lat,final double lon){
        activity.runOnUiThread(new Runnable() {
            public void run() {

                mapView.centerAt(lat,lon,true);
            }
        });
    }
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}

