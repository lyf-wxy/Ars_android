package com.example.qman.myapplication.indextab;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISImageServiceLayer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.map.MosaicRule;
import com.esri.core.map.RasterFunction;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.GPSTracker;
import com.example.qman.myapplication.utils.Variables;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Callback;
import okhttp3.Response;

import static android.R.id.list;
import static android.R.id.message;
import static android.R.string.ok;


public class MapFragment extends Fragment {
    private MapView mMapView;
    //FloatingActionButton mSearch;
    FloatingActionButton mPosition;
    FloatingActionButton mShowall;
    private ArcGISImageServiceLayer mArcGISImageServiceLayer;
    private SearchView mSearchview;
    private GraphicsLayer graphicsLayerPosition = new GraphicsLayer();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapofMapfragment);

        mPosition = (FloatingActionButton) view.findViewById(R.id.position);
        //mSearch = (FloatingActionButton)view.findViewById(R.id.search);
        mShowall = (FloatingActionButton) view.findViewById(R.id.showAll);
        mSearchview = (SearchView) view.findViewById(R.id.searchView);


        final MarkerSymbol positionSymbol = new PictureMarkerSymbol(getActivity().getDrawable(R.drawable.positionsymbol));


        ActivityUtil.setTitle(getActivity(),R.id.toolbar_title,"地图");

//        mSearch.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v)
//            {
//
//                Toast.makeText(getActivity().getApplicationContext(),"mSearch",Toast.LENGTH_LONG).show();
//            }
//        });

        mPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showLocation(mlocation);

                GPSTracker gps = new GPSTracker(getActivity().getApplicationContext());
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                Point position = new Point(longitude,latitude);
                Point positionProj = (Point)GeometryEngine.project(position, SpatialReference.create(4326),mMapView.getSpatialReference());
                //图层的创建
                Graphic graphicPoint = new Graphic(positionProj,positionSymbol);
                graphicsLayerPosition.removeAll();
                graphicsLayerPosition.addGraphic(graphicPoint);

                mMapView.centerAt(latitude,longitude,true);

                getPositionNamebyLatLon(position);

                //Toast.makeText(getActivity().getApplicationContext(), latitude+","+longitude, Toast.LENGTH_LONG).show();
            }
        });

        mShowall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Envelope env = mArcGISImageServiceLayer.getFullExtent();

                Envelope env_proj = (Envelope) GeometryEngine.project(env, SpatialReference.create(4326),mMapView.getSpatialReference());

                mMapView.setExtent(env_proj);


                //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(mMapView.getSpatialReference()), Toast.LENGTH_LONG).show();

                //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(env.getXMax())+","+String.valueOf(env.getXMin())+String.valueOf(env.getYMax())+","+String.valueOf(env.getYMin()), Toast.LENGTH_LONG).show();
            }
        });
        //mSearchview.setIconifiedByDefault(true);

        mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                getLatLonbyPositionName(query);

               // Toast.makeText(getActivity().getApplicationContext(), query, Toast.LENGTH_LONG).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
//        mSearchview.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                Toast.makeText(getActivity().getApplicationContext(), "close", Toast.LENGTH_LONG).show();
//                return false;
//            }
//        });

        mArcGISImageServiceLayer = new ArcGISImageServiceLayer("http://10.2.3.222:6080/arcgis/rest/services/sde_DBO_shangxi/ImageServer",null);

        MosaicRule mMosaicRule = new MosaicRule();
        mMosaicRule.setWhere("OBJECTID<14");
        mArcGISImageServiceLayer.setMosaicRule(mMosaicRule);

        RasterFunction renderingRule = new RasterFunction();
        String rasterFunctionTemplate = "14";
        renderingRule.setFunctionName(rasterFunctionTemplate);
        mArcGISImageServiceLayer.setRenderingRule(renderingRule);

        mMapView.addLayer(mArcGISImageServiceLayer);

        mMapView.addLayer(graphicsLayerPosition);

        return view;
    }
    /*
    通过经纬度获取地名
     */
    public void getPositionNamebyLatLon(final Point point)
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
                    String address = jsonObject.getJSONObject("result").getString("formatted_address");

                    addAddressSymbol2Map(getActivity(),address,point);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });

    }
    /*
    将地名信息添加在地图上
     */

    public  void addAddressSymbol2Map(final Activity activity, final String message,final Point point){
        activity.runOnUiThread(new Runnable() {
            public void run() {

                final TextSymbol positionTextSys = new TextSymbol(10,message, Color.RED);
                positionTextSys.setFontFamily("DroidSansFallback.ttf");
                //图层的创建
                Point positionProj = (Point)GeometryEngine.project(point, SpatialReference.create(4326),mMapView.getSpatialReference());
                Graphic graphicPoint = new Graphic(positionProj,positionTextSys);

                graphicsLayerPosition.addGraphic(graphicPoint);

                //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*
    通过地名获取经纬度
     */
    public void getLatLonbyPositionName(String address)
    {
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

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String str = response.body().string();
                try
                {
                    JSONObject jsonObject = new JSONObject(str);

                    double lon = jsonObject.getJSONObject("result").getJSONObject("location").getDouble("lng");
                    double lat = jsonObject.getJSONObject("result").getJSONObject("location").getDouble("lat");

                    MapCenter2LatLon(getActivity(),mMapView,lat,lon);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });
    }
    /*
     将map缩放到指定经纬度
     */

    public  void MapCenter2LatLon(final Activity activity, final MapView mapView,final double lat,final double lon){
        activity.runOnUiThread(new Runnable() {
            public void run() {

                mapView.centerAt(lat,lon,true);
            }
        });
    }
}