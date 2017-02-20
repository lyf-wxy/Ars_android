package com.example.qman.myapplication.indextab;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISImageServiceLayer;
import com.esri.core.map.MosaicRule;
import com.esri.core.map.RasterFunction;
import com.example.qman.myapplication.R;

public class MapFragment extends Fragment
{
    private MapView mMapView;
    private ArcGISImageServiceLayer mArcGISImageServiceLayer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.map_fragment,container,false);
        mMapView = (MapView)view.findViewById(R.id.map);

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