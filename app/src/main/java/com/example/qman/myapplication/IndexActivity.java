package com.example.qman.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISImageServiceLayer;
import com.esri.core.map.MosaicRule;
import com.esri.core.map.RasterFunction;

public class IndexActivity extends AppCompatActivity {

    private static final String TAG="IndexActivity";
    private MapView mMapView;
    private ArcGISImageServiceLayer mArcGISImageServiceLayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent= getIntent();
        String usernameLogin = intent.getStringExtra("usernameLogin");
//        TextView tv = new TextView(this);
//        tv.setText(usernameLogin);
//        setContentView(tv);
        setContentView(R.layout.activity_index);

        mMapView = (MapView) findViewById(R.id.map);
        mArcGISImageServiceLayer = new ArcGISImageServiceLayer(
                "http://10.2.3.222:6080/arcgis/rest/services/sde_DBO_shangxi/ImageServer",null);

        MosaicRule mMosaicRule = new MosaicRule();
        mMosaicRule.setWhere("OBJECTID<14");
        mArcGISImageServiceLayer.setMosaicRule(mMosaicRule);

        RasterFunction renderingRule = new RasterFunction();
        String rasterFunctionTemplate = "14";
        renderingRule.setFunctionName(rasterFunctionTemplate);
        mArcGISImageServiceLayer.setRenderingRule(renderingRule);

        mMapView.addLayer(mArcGISImageServiceLayer);
    }
    @Override
    protected void onPause(){
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mMapView.unpause();
    }
}
