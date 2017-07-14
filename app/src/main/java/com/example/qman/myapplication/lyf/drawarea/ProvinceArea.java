package com.example.qman.myapplication.lyf.drawarea;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISImageServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.areatab.AreaFragment;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.FormFile;
import com.example.qman.myapplication.utils.GPSTracker;
import com.example.qman.myapplication.utils.GalleryAdapter;
import com.example.qman.myapplication.utils.ListViewUtil;
import com.example.qman.myapplication.utils.RequestUtil;
import com.example.qman.myapplication.utils.SocketHttpRequester;
import com.example.qman.myapplication.utils.Util;
import com.example.qman.myapplication.utils.Variables;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.R.id.list;
import static com.example.qman.myapplication.R.string.username;


/**
 * Created by lyf on 11/07/2017.
 */

public class ProvinceArea extends Fragment{
    public static String TAG = "ProvinceArea";
    private MapView mMapView;
    GraphicsLayer ProvinceAreaGraphicLayer = new GraphicsLayer();
    private Button mPosition;
    private Button mSave;
    private Button mUndo;

    private ImageView bt_back;

    String codeIdTemp;
    String addAreaName;

    ProgressDialog progress;
    File upfile;

    SaveArea SaveAreaDialog;
    String m_CropkindsStr="";//作物种类
    String m_fieldName="";//地块名称
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aera_item_province, container, false);


        Bundle args = getArguments();
        if(args!=null)
        {
            codeIdTemp = args.getString("codeIdTemp");
            addAreaName = args.getString("addAreaName");
            //Toast.makeText(getActivity(), codeIdTemp+","+addAreaName, Toast.LENGTH_SHORT).show();
        }

        mPosition = (Button)view.findViewById(R.id.positionofprovince);
        mSave = (Button)view.findViewById(R.id.saveofprovince);

        mMapView = (MapView) view.findViewById(R.id.mapofprovince);

        mUndo = (Button)view.findViewById(R.id.undoofprovince);

        bt_back = (ImageView) view.findViewById(R.id.bt_back);
        /*后退按钮*/
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.switchToFragment(getActivity(),new AreaFragment(),R.id.id_content);
            }
        });

        mPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void onStatusChanged(Object source, STATUS status) {
                if (source == mMapView && status == STATUS.INITIALIZED) {
                    try{
                        //mMapView.addLayer(ProvinceAreaGraphicLayer);

                        //进行行政区域缩略图生成操作
                        String targetLayer = Variables.targetServerURL.concat("/0");
                        String[] queryArray = {targetLayer, "CODE="+codeIdTemp};
                        AsyncQueryTask ayncQuery = new AsyncQueryTask();
                        ayncQuery.execute(queryArray);

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SaveAreaDialog.show(getFragmentManager(), "SaveAreaDialog");
                //实例化进度条对话框（ProgressDialog）
            //final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Please wait....save task is executing");
//            pd.setTitle("请稍等");
//            //设置对话进度条样式为水平
//            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            //设置提示信息
//            pd.setMessage("正在保存中......");
//            //设置对话进度条显示在屏幕顶部（方便截图）
//            pd.getWindow().setGravity(Gravity.CENTER);
//            pd.setMax(100);
//            pd.show();//调用show方法显示进度条对话框

//            Bitmap bitmap= Util.getViewBitmap(mMapView);
//
//            String FileDirectory = Util.saveMyBitmap(codeIdTemp,bitmap);//保存缩略图，并返回文件路径
//
//            Log.d(TAG,FileDirectory);
//
//            bitmap.recycle();
//            // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
//            upfile = new File(FileDirectory);
//            new Thread(runnable).start();

            //pd.dismiss();





            }
        });
        SaveAreaDialog=new SaveArea();
        SaveAreaDialog.setOnButtonClickListener(new SaveArea.savearea_Listener(){
            @Override
            //点击 保存按钮
            public void savearea_sureClick(String fieldname,ArrayList<String> Cropkinds)
            {

                for(int i = 0;i < Cropkinds.size(); i ++){
                    m_CropkindsStr+=(Cropkinds.get(i))+"/";
                }
                m_fieldName=fieldname;

                Bitmap bitmap= Util.getViewBitmap(mMapView);

                String FileDirectory = Util.saveMyBitmap(codeIdTemp,bitmap);//保存缩略图，并返回文件路径

                Log.d(TAG,FileDirectory);

                bitmap.recycle();
                // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
                upfile = new File(FileDirectory);
                generatePicFromMapviewAndUpLoadInfo m_generatePic=new generatePicFromMapviewAndUpLoadInfo(
                        getActivity(),
                        upfile,
                        codeIdTemp,
                        codeIdTemp,
                        m_fieldName,
                        m_CropkindsStr
                );
                new Thread(m_generatePic.runnable).start();

                SaveAreaDialog.dismiss();
            }
            @Override
            //点击取消按钮
            public void savearea_cancleClick()
            {
                SaveAreaDialog.dismiss();
            }

        });

        return view;
    }

    /**
     *
     * Query Task executes asynchronously.
     *
     */
    private class AsyncQueryTask extends AsyncTask<String, Void, FeatureResult> {

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getActivity(), "", "Please wait....query task is executing");

        }

        /**
         * First member in string array is the query URL; second member is the where
         * clause.
         */
        @Override
        protected FeatureResult doInBackground(String... queryArray) {
            if (queryArray == null || queryArray.length <= 1)
                return null;

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
            ProvinceAreaGraphicLayer.setRenderer(sr);
            mMapView.addLayer(ProvinceAreaGraphicLayer);



            String message = "No result comes back";
            if (results != null) {
                int i = 0;
                Geometry[] GeosforExtent = new Geometry[(int)results.featureCount()];

                for (Object element : results) {
                    if (element instanceof Feature) {
                        Feature feature = (Feature) element;
                        // turn feature into graphic
                        Graphic graphic = new Graphic(feature.getGeometry(), feature.getSymbol(), feature.getAttributes());

                        GeosforExtent[i] = feature.getGeometry();

                        // add graphic to layer
                        ProvinceAreaGraphicLayer.addGraphic(graphic);
                        i++;
                    }
                }
                // update message with results
                message = String.valueOf(results.featureCount()) + " results have returned from query.";

                Geometry geometryUnion = GeometryEngine.union(GeosforExtent,mMapView.getSpatialReference());

                Envelope env = new Envelope();
                geometryUnion.queryEnvelope(env);

                Log.d(TAG,env.toString());
                mMapView.setExtent(env);
            }


            //mMapView.setExtent(newExtent);

            /**
             * upload image file

             */
//            Bitmap bitmap= Util.getViewBitmap(mMapView);
//
//            String FileDirectory = Util.saveMyBitmap(codeIdTemp,bitmap);//保存缩略图，并返回文件路径
//
//            Log.d(TAG,FileDirectory);
//
//            bitmap.recycle();
//            // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
//            upfile = new File(FileDirectory);
//            new Thread(runnable).start();


            progress.dismiss();

//            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
//            toast.show();



        }

    }




}
