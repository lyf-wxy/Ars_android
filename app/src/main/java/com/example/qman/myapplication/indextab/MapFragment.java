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
import android.support.v4.content.ContextCompat;
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
    private GraphicsLayer graphicsLayerPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapofMapfragment);

        mPosition = (FloatingActionButton) view.findViewById(R.id.position);
        //mSearch = (FloatingActionButton)view.findViewById(R.id.search);
        mShowall = (FloatingActionButton) view.findViewById(R.id.showAll);
        mSearchview = (SearchView) view.findViewById(R.id.searchView);


        final MarkerSymbol positionSymbol = new PictureMarkerSymbol(ContextCompat.getDrawable(getActivity(),R.drawable.positionsymbol));
        graphicsLayerPosition = new GraphicsLayer();

        ActivityUtil.setTitle(getActivity(),R.id.toolbar_title,"地图");
        ActivityUtil.setOnlyVisibilitys(getActivity(),R.id.toolbar_title, R.id.toolbar_search, R.id.toolbar_add,R.id.toolbar_draw);
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

                Log.d("latitude+longitude",String.valueOf(latitude)+","+String.valueOf(longitude));

                Point position = new Point(longitude,latitude);
                Point positionProj = (Point)GeometryEngine.project(position, SpatialReference.create(4326),mMapView.getSpatialReference());
                //图层的创建
                Graphic graphicPoint = new Graphic(positionProj,positionSymbol);

                graphicsLayerPosition.removeAll();
                graphicsLayerPosition.addGraphic(graphicPoint);

                mMapView.centerAndZoom(latitude,longitude,16);

                GPSTracker.getPositionNamebyLatLon(position,getActivity(),graphicsLayerPosition,mMapView);


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

                GPSTracker.getLatLonbyPositionName(query,getActivity(),mMapView);
                //Log.d("point",String.valueOf(point.getY())+","+String.valueOf(point.getX()));
                //GPSTracker.MapCenter2LatLon(getActivity(),mMapView,point);
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

        mArcGISImageServiceLayer = new ArcGISImageServiceLayer("http://10.2.3.222:6080/arcgis/rest/services/TRSQ/ImageServer",null);

        MosaicRule mMosaicRule = new MosaicRule();
        mMosaicRule.setWhere("OBJECTID<14");
        mArcGISImageServiceLayer.setMosaicRule(mMosaicRule);

//        RasterFunction renderingRule = new RasterFunction();
//        String rasterFunctionTemplate = "14";
//        renderingRule.setFunctionName(rasterFunctionTemplate);
//        mArcGISImageServiceLayer.setRenderingRule(renderingRule);

        mMapView.addLayer(mArcGISImageServiceLayer);

        mMapView.addLayer(graphicsLayerPosition);

        return view;
    }

}