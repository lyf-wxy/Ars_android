package com.example.qman.myapplication.indextab;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
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

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISImageServiceLayer;
import com.esri.core.map.MosaicRule;
import com.esri.core.map.RasterFunction;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.GPSTracker;


public class MapFragment extends Fragment {
    private MapView mMapView;
    //FloatingActionButton mSearch;
    FloatingActionButton mPosition;
    FloatingActionButton mShowall;
    private ArcGISImageServiceLayer mArcGISImageServiceLayer;
    private SearchView mSearchview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapofMapfragment);

        mPosition = (FloatingActionButton) view.findViewById(R.id.position);
        //mSearch = (FloatingActionButton)view.findViewById(R.id.search);
        mShowall = (FloatingActionButton) view.findViewById(R.id.showAll);
        mSearchview = (SearchView) view.findViewById(R.id.searchView);
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
                Toast.makeText(getActivity().getApplicationContext(), latitude+","+longitude, Toast.LENGTH_LONG).show();
            }
        });

        mShowall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity().getApplicationContext(), "mShowall", Toast.LENGTH_LONG).show();
            }
        });
        //mSearchview.setIconifiedByDefault(true);

        mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Toast.makeText(getActivity().getApplicationContext(), query, Toast.LENGTH_LONG).show();

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

        return view;
    }

}