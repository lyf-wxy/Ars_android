package com.example.qman.myapplication.indextab;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.map.MosaicRule;
import com.esri.core.map.RasterFunction;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.areatab.AreaItemInfoFragment;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.GPSTracker;
import com.example.qman.myapplication.utils.ListViewUtil;
import com.example.qman.myapplication.utils.Variables;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Callback;
import okhttp3.Response;

import static android.R.attr.data;
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

    private List<HashMap<String,Object>> list = null;//
    String codeidStr="";


    GraphicsLayer AreaGraphicLayer;
    ProgressDialog progress;
    List<HashMap<String, Object>> mdata;//每个地块的详细信息
    public static String TAG = "MapFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        codeidStr = ActivityUtil.getParam(getActivity(),"locno");

        AreaGraphicLayer= new GraphicsLayer();

        mMapView = (MapView) view.findViewById(R.id.mapofMapfragment);

        mPosition = (FloatingActionButton) view.findViewById(R.id.position);
        //mSearch = (FloatingActionButton)view.findViewById(R.id.search);
        mShowall = (FloatingActionButton) view.findViewById(R.id.showAll);
        mSearchview = (SearchView) view.findViewById(R.id.searchView);

        final MarkerSymbol positionSymbol = new PictureMarkerSymbol(ContextCompat.getDrawable(getActivity(),R.drawable.positionsymbol));
        graphicsLayerPosition = new GraphicsLayer();

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
        // load map
        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void onStatusChanged(Object source, STATUS status) {
                if (source == mMapView && status == STATUS.INITIALIZED) {
                    try{
                        if(!codeidStr.equals("")){//加载地块

//                            mMapView.removeAll();
                            mMapView.addLayer(AreaGraphicLayer);

                            mdata = ListViewUtil.getData();
                            LoadGraphic();

                        }

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        });
//        mArcGISImageServiceLayer = new ArcGISImageServiceLayer("http://10.2.3.222:6080/arcgis/rest/services/TRSQ/ImageServer",null);
//
//        MosaicRule mMosaicRule = new MosaicRule();
//        mMosaicRule.setWhere("OBJECTID<14");
//        mArcGISImageServiceLayer.setMosaicRule(mMosaicRule);

//        RasterFunction renderingRule = new RasterFunction();
//        String rasterFunctionTemplate = "14";
//        renderingRule.setFunctionName(rasterFunctionTemplate);
//        mArcGISImageServiceLayer.setRenderingRule(renderingRule);

//        mMapView.addLayer(mArcGISImageServiceLayer);
//
//        mMapView.addLayer(graphicsLayerPosition);

        return view;
    }
    public void LoadGraphic()
    {
        AsyncLoadAreaTask ayncQuery = new AsyncLoadAreaTask();
        ayncQuery.execute();
    }
    /**
     *
     * Query Task executes asynchronously.
     *
     */
    private class AsyncLoadAreaTask extends AsyncTask<String, Void, Geometry[]> {

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getActivity(), "", "Please wait....query task is executing");

        }

        /**
         * First member in string array is the query URL; second member is the where
         * clause.
         */
        @Override
        protected Geometry[] doInBackground(String... queryArray) {

            Geometry[] GeosforExtent = new Geometry[mdata.size()];

            for(int i=0;i<mdata.size();i++)
            {
                String geometry=mdata.get(i).get("geometry").toString();
                String fieldname = mdata.get(i).get("ordername").toString();



                if(geometry.length()==6)//行政区划选择产生
                {
                    //进行行政区域
                    String targetLayer = Variables.targetServerURL.concat("/0");

                    String url = targetLayer;
                    QueryParameters qParameters = new QueryParameters();
                    String whereClause = "CODE="+geometry;
                    SpatialReference sr = SpatialReference.create(102100);
                    //qParameters.setGeometry(new Envelope(-20147112.9593773, 557305.257274575, -6569564.7196889, 11753184.6153385));
                    qParameters.setOutSpatialReference(sr);
                    qParameters.setReturnGeometry(true);
                    qParameters.setWhere(whereClause);

                    QueryTask qTask = new QueryTask(url);

                    try {
                        FeatureResult results = qTask.execute(qParameters);
                        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.RED);
                        simpleFillSymbol.setAlpha(100);

                        if (results != null) {

                            for (Object element : results) {
                                if (element instanceof Feature) {
                                    Feature feature = (Feature) element;
                                    // turn feature into graphic
                                    //Graphic graphic = new Graphic(feature.getGeometry(), feature.getSymbol(), feature.getAttributes());
                                    Graphic graphic = new Graphic(feature.getGeometry(), simpleFillSymbol, feature.getAttributes());
                                    GeosforExtent[i] = feature.getGeometry();

                                    // add graphic to layer
                                    AreaGraphicLayer.addGraphic(graphic);
                                    //添加地块名称
                                    Envelope envofField = new Envelope();
                                    feature.getGeometry().queryEnvelope(envofField);
                                    TextSymbol lablefield = new TextSymbol(15,fieldname, Color.WHITE);
                                    lablefield.setFontFamily("DroidSansFallback.ttf");
                                    Graphic lablefieldGra = new Graphic(envofField.getCenter(),lablefield);
                                    AreaGraphicLayer.addGraphic(lablefieldGra);

                                    break;
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{//手动绘制产生
                    try{

                        //Log.d(TAG,geometryStr[0]);
                        JsonFactory factory = new JsonFactory();
                        JsonParser jsonParser = factory.createJsonParser(geometry);
                        MapGeometry mapGeometry = GeometryEngine.jsonToGeometry(jsonParser);
                        Geometry areageometry = mapGeometry.getGeometry();
                        GeosforExtent[i] = areageometry;

                        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.YELLOW);
                        simpleFillSymbol.setAlpha(100);
                        simpleFillSymbol.setOutline(new SimpleLineSymbol(Color.BLACK, 4));
                        Graphic graphic = new Graphic(areageometry, (simpleFillSymbol));


                        AreaGraphicLayer.addGraphic(graphic);
                        //添加地块名称
                        Envelope envofField = new Envelope();
                        areageometry.queryEnvelope(envofField);
                        TextSymbol lablefield = new TextSymbol(15,fieldname, Color.WHITE);
                        lablefield.setFontFamily("DroidSansFallback.ttf");
                        Graphic lablefieldGra = new Graphic(envofField.getCenter(),lablefield);
                        AreaGraphicLayer.addGraphic(lablefieldGra);

                        //Log.d(TAG,"added");


                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }


            }

            return GeosforExtent;

        }

        @Override
        protected void onPostExecute(Geometry[] GeosforExtent) {


            Geometry geometryUnion = GeometryEngine.union(GeosforExtent, mMapView.getSpatialReference());

            Envelope env = new Envelope();
            geometryUnion.queryEnvelope(env);

            Log.d(TAG, env.toString());
            mMapView.setExtent(env);

            progress.dismiss();

        }
    }

    /**
     *
     * Query Task executes asynchronously.
     *
     */
    /*
    private class AsyncQueryTask extends AsyncTask<String, Void, FeatureResult> {
        String mfieldname="";

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getActivity(), "", "Please wait....query task is executing");

        }

        @Override
        protected FeatureResult doInBackground(String... queryArray) {
            if (queryArray == null || queryArray.length <= 1)
                return null;

            mfieldname = queryArray[2];//地块名称
            String url = queryArray[0];
            QueryParameters qParameters = new QueryParameters();
            String whereClause = queryArray[1];
            SpatialReference sr = SpatialReference.create(102100);
            //qParameters.setGeometry(new Envelope(-20147112.9593773, 557305.257274575, -6569564.7196889, 11753184.6153385));
            qParameters.setOutSpatialReference(sr);
            qParameters.setReturnGeometry(true);
            qParameters.setWhere(whereClause);

            QueryTask qTask = new QueryTask(url);

            try {
                FeatureResult results = qTask.execute(qParameters);
                return results;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(FeatureResult results) {

//            mMapView.removeAll();
//

            SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.RED);
            simpleFillSymbol.setAlpha(100);
            SimpleRenderer sr = new SimpleRenderer(simpleFillSymbol);
//            AreaGraphicLayer.setRenderer(sr);
//            mMapView.addLayer(AreaGraphicLayer);


            String message = "No result comes back";
            if (results != null) {
                int i = 0;
                Geometry[] GeosforExtent = new Geometry[(int) results.featureCount()];

                for (Object element : results) {
                    if (element instanceof Feature) {
                        Feature feature = (Feature) element;
                        // turn feature into graphic
                        //Graphic graphic = new Graphic(feature.getGeometry(), feature.getSymbol(), feature.getAttributes());
                        Graphic graphic = new Graphic(feature.getGeometry(), simpleFillSymbol, feature.getAttributes());
                        GeosforExtent[i] = feature.getGeometry();

                        // add graphic to layer
                        AreaGraphicLayer.addGraphic(graphic);
                        //添加地块名称
                        Envelope envofField = new Envelope();
                        feature.getGeometry().queryEnvelope(envofField);
                        TextSymbol lablefield = new TextSymbol(10,mfieldname, Color.RED);
                        Graphic lablefieldGra = new Graphic(envofField.getCenter(),lablefield);
                        AreaGraphicLayer.addGraphic(lablefieldGra);

                        i++;
                    }
                }
                // update message with results
                message = String.valueOf(results.featureCount()) + " results have returned from query.";

                Geometry geometryUnion = GeometryEngine.union(GeosforExtent, mMapView.getSpatialReference());

                Envelope env = new Envelope();
                geometryUnion.queryEnvelope(env);

                Log.d(TAG, env.toString());
                mMapView.setExtent(env);
            }

            progress.dismiss();

        }
    }
    class AsyncLoadGraphic extends AsyncTask<String,Void,Geometry>{
        String mfieldname="";
        @Override
        protected Geometry doInBackground(String... geometryStr)
        {

            if (geometryStr == null || geometryStr.length < 1)
            {
                return null;
            }



            try{
                mfieldname=geometryStr[1];
                //Log.d(TAG,geometryStr[0]);
                JsonFactory factory = new JsonFactory();
                JsonParser jsonParser = factory.createJsonParser(geometryStr[0]);
                MapGeometry mapGeometry = GeometryEngine.jsonToGeometry(jsonParser);
                Geometry geometry = mapGeometry.getGeometry();

                SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.YELLOW);
                simpleFillSymbol.setAlpha(100);
                simpleFillSymbol.setOutline(new SimpleLineSymbol(Color.BLACK, 4));
                Graphic graphic = new Graphic(geometry, (simpleFillSymbol));


                AreaGraphicLayer.addGraphic(graphic);
                //添加地块名称
                Envelope envofField = new Envelope();
                geometry.queryEnvelope(envofField);
                TextSymbol lablefield = new TextSymbol(10,mfieldname, Color.RED);
                Graphic lablefieldGra = new Graphic(envofField.getCenter(),lablefield);
                AreaGraphicLayer.addGraphic(lablefieldGra);

                //Log.d(TAG,"added");

                return geometry;





            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Geometry geometry)
        {
            Envelope env = new Envelope();
            geometry.queryEnvelope(env);

            mMapView.setExtent(env);
        }

    }
    */

}