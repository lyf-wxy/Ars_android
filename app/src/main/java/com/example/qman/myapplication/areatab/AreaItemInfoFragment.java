package com.example.qman.myapplication.areatab;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISImageServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.map.MosaicRule;
import com.esri.core.map.RasterFunction;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.lyf.drawarea.ProvinceArea;
import com.example.qman.myapplication.lyf.drawarea.SaveDrawArea;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;
import com.example.qman.myapplication.utils.GalleryAdapter;
import com.example.qman.myapplication.utils.ListViewUtil;
import com.example.qman.myapplication.utils.RequestUtil;
import com.example.qman.myapplication.utils.Util;
import com.example.qman.myapplication.utils.Variables;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.qman.myapplication.areatab.AreaFragment.TAG;
import static com.example.qman.myapplication.lyf.drawarea.generatePicFromMapviewAndUpLoadInfo.codeIdTemp;

public class AreaItemInfoFragment extends Fragment
{
    private String codeidStr = "";
    private Bundle savedState;//临时数据保存

    private ImageView legendPic;

    private MapView mMapView;
    private TextView mTitleArea;
    private TextView mTitle;
    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
    private List<String> mDatas;
    private List<String> mdatetime;

    private String mField;
    private String mSelectedClass;
    String m_geometryStr;
    private String codeid;
    private String id;
    private List<HashMap<String,Object>> list = null;

    private ArcGISImageServiceLayer mArcGISImageServiceLayer;

    private ImageView bt_back;
    JSONObject ajsonObject = new JSONObject();
    private List<String> productTypeList;
    private String productType;

    private ImageView bt_x;

    private ImageView bt_bch;
    private ImageView bt_fl;
    private ImageView bt_gz;
    private ImageView bt_sq;
    private ImageView bt_zs;
    private ImageView bt_zwfl;

    private int ifHide = View.VISIBLE;

    GraphicsLayer AreaGraphicLayer = new GraphicsLayer();
    ProgressDialog progress;
    public static String TAG = "AreaItemInfoFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.area_item_info_fragment, container, false);

        bt_back = (ImageView) view.findViewById(R.id.bt_back);
        /*后退按钮*/
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.switchToFragment(getActivity(),new AreaFragment(),R.id.id_content);
            }
        });
        m_geometryStr = ActivityUtil.getParam(getActivity(),"geometry");
        productType = ActivityUtil.getParam(getActivity(),"producttype");
        codeid = ActivityUtil.getParam(getActivity(),"codeid");
        id = ActivityUtil.getParam(getActivity(),"id");

//        Bundle args = getArguments();
//        if(args!=null)
//        {
//            mField = args.getString("field");
//            mSelectedClass = args.getString("selectedClass");
//
//        }

        //Toast.makeText(getActivity(), mField+","+mSelectedClass+","+geometry, Toast.LENGTH_SHORT).show();

        legendPic = (ImageView) view.findViewById(R.id.legendPic);
        mMapView = (MapView)view.findViewById(R.id.mapofAreaItemInfo);
        mTitle = (TextView)view.findViewById(R.id.current_service_title);
        mTitleArea = (TextView)view.findViewById(R.id.TitleOfAreaItemInfo);
        //list = ListViewUtil.initSplitData(id, codeid);
        //mTitleArea.setText(list.get(0).toString());
        new GetAreaCodeInfoThreadTask().execute();
        //得到控件
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_horizontal);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        initDatesListView();
        productTypeList = new ArrayList<String>();

        bt_x = (ImageView) view.findViewById(R.id.bt_hide);
        bt_bch = (ImageView) view.findViewById(R.id.bch);
        bt_fl = (ImageView) view.findViewById(R.id.fl);
        bt_gz = (ImageView) view.findViewById(R.id.gz);
        bt_sq = (ImageView) view.findViewById(R.id.sq);
        bt_zs = (ImageView) view.findViewById(R.id.zs);
        bt_zwfl = (ImageView) view.findViewById(R.id.zwfl);
        new LoadCropkindsAsyncTask().execute();

        bt_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt_x.getDrawable().getCurrent().getConstantState().equals(getResources().getDrawable(R.drawable.x).getConstantState())){
                    //当image1的src为R.drawable.A时，设置image1的src为R.drawable.B
                    if(ifHide == View.VISIBLE)
                        ifHide = View.GONE;
                    bt_x.setImageResource(R.drawable.x_add);
                }else{
                    if(ifHide == View.GONE)
                        ifHide = View.VISIBLE;
                    bt_x.setImageResource(R.drawable.x);
                }
                new LoadCropkindsAsyncTask().execute();
            }
        });

        // load map
        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void onStatusChanged(Object source, STATUS status) {
                if (source == mMapView && status == STATUS.INITIALIZED) {
                    try{

                        //new LoadMapAsyncTask().execute();
//                        mArcGISImageServiceLayer = new ArcGISImageServiceLayer("http://192.168.8.101:6080/arcgis/rest/services/NYBCHJC/ImageServer",null);
//
//                        mMapView.addLayer(mArcGISImageServiceLayer);

//                        mSelectedClass="农业病虫害监测";
//
//                        addLayer2MapView(mSelectedClass);
//                        addLegend2MapView(mSelectedClass);
                        mMapView.addLayer(AreaGraphicLayer);

                        String[] geometryStr = {m_geometryStr};
                        if(m_geometryStr.length()==6)//行政区划选择产生
                        {
                            //进行行政区域
                            String targetLayer = Variables.targetServerURL.concat("/0");
                            String[] queryArray = {targetLayer, "CODE="+m_geometryStr};
                            AsyncQueryTask ayncQuery = new AsyncQueryTask();
                            ayncQuery.execute(queryArray);
                        }
                        else{//手动绘制产生
                            new AsyncLoadGraphic().execute(geometryStr);
                        }

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        });


        return view ;
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
            AreaGraphicLayer.setRenderer(sr);
            mMapView.addLayer(AreaGraphicLayer);



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
                        AreaGraphicLayer.addGraphic(graphic);
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
    class AsyncLoadGraphic extends AsyncTask<String,Void,Geometry>{
        @Override
        protected Geometry doInBackground(String... geometryStr)
        {

            if (geometryStr == null || geometryStr.length < 1)
            {
                return null;
            }



            try{

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

            //Envelope newEnv = new Envelope(env.getCenter(),env.getWidth()*2,env.getHeight()*2);

//            Log.d(TAG,env.toString());
//            Log.d(TAG,newEnv.toString());

            mMapView.setExtent(env);
        }

    }
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if(!restoreStateFromArguments()){
//            //第一次进入做一些初始化操作
//        }
//
//    }
//
//
//    @Override
//    public void onSaveInstanceState(Bundle outState){
//        //super.onSaveInstanceState(outState);
//        //可能再次保存临时数据
//        saveStateToArguments();
//    }
//
//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        //也有可能再次保存临时数据
//        saveStateToArguments();
//    }
//    //保存临时数据
//    private void saveStateToArguments(){
//        savedState = saveState();
//
//        if(savedState != null){
//            getActivity().getIntent().getExtras();
//            Bundle b = getActivity().getIntent().getExtras();//getArguments();
//
//            b.putBundle("codeidStr",savedState);
//        }
//    }
//    //取出临时数据
//    private boolean restoreStateFromArguments(){
//        Bundle b = getArguments();
//        if(b!=null)
//            savedState = b.getBundle("codeidStr");
//        if(savedState != null){
//            restoreState();
//            return true;
//        }
//        return false;
//    }
//
//    private void restoreState(){
//        if(savedState != null){
//            codeidStr = savedState.getString("codeidStr");
//        }
//    }
//
//    private Bundle saveState(){
//        Bundle state = new Bundle();
//        state.putString("codeidStr",codeidStr);
//        Log.v("saveState()",codeidStr);
//        return state;
//    }
    private void addLayer2MapView(String productType)
    {


        String MapType = CheckBoxUtil.getLetterName(productType);

        String MapLayer = Util.getMapLayerUrl(MapType);
        if(MapLayer!=null)
        {
            mArcGISImageServiceLayer = new ArcGISImageServiceLayer(MapLayer,null);

            mMapView.addLayer(mArcGISImageServiceLayer);
        }




    }
    private void addLegend2MapView(String productType)
    {

        //mTitle.setText(productType);
        //getMapLegend
        legendPic.setImageResource(R.drawable.legend);

    }
    private void initDatesListView()
    {
        mDatas = new ArrayList<String>(Arrays.asList(
                "http://192.168.8.101:6080/arcgis/rest/services/TRSQ_20170101/ImageServer",
                "http://192.168.8.101:6080/arcgis/rest/services/TRSQ_20170108/ImageServer",
                "http://192.168.8.101:6080/arcgis/rest/services/TRSQ_20170115/ImageServer",
                "http://192.168.8.101:6080/arcgis/rest/services/TRSQ_20170122/ImageServer",
                "http://192.168.8.101:6080/arcgis/rest/services/TRSQ_20170129/ImageServer"));
        mdatetime = new ArrayList<String>(Arrays.asList("2017-01-01","2017-01-08","2017-01-15","2017-01-22","2017-01-029"));

        //设置适配器
        mAdapter = new GalleryAdapter(getActivity(), mDatas,mdatetime);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {

                //view.setEnabled(true);
                //mAdapter.setSelectIndex(pos);

                // Log.d("AreaItemInfoFragment","onItemClick");
                String mapLayerUrl = mDatas.get(pos);

                mMapView.removeLayer(mArcGISImageServiceLayer);

                mArcGISImageServiceLayer = new ArcGISImageServiceLayer(mapLayerUrl,null);

                mMapView.addLayer(mArcGISImageServiceLayer);

                //Toast.makeText(getActivity().getApplicationContext(),pos+"",Toast.LENGTH_SHORT).show();
            }
        });
    }
    class GetAreaCodeInfoThreadTask extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params) {
            //查询订购区域代码codeid
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userid",id);
                jsonObject.put("codeid",codeid);
                String str = RequestUtil.request(jsonObject.toString(),"AndroidService/cityInfoService");
                JSONObject jsonObjectResult = new JSONObject(str);
                String result = jsonObjectResult.getString("result");//解析json查询结果

                if (result.equals("success")) {
                    String dataStr = jsonObjectResult.getString("data");
                    JSONArray areaLists = new JSONArray(dataStr);
                    if (areaLists.length()>0) {
                        for (int i=0;i<areaLists.length();i++) {
                            JSONArray aArea = new JSONArray(areaLists.get(i).toString());
                            return aArea.get(0).toString();
                        }
                    }
                } else {
                    return  "error";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  "error";
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String s) {
            mTitleArea.setText(s);
        }
    }
    public class LoadMapAsyncTask extends AsyncTask<String,Integer,byte[]>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }
        @Override
        protected byte[] doInBackground(String... params)
        {
            mSelectedClass="农业病虫害监测";

            addLayer2MapView(mSelectedClass);
            addLegend2MapView(mSelectedClass);

            //initDatesListView();

            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values)
        {

        }
        @Override
        protected void onPostExecute(byte[] result)
        {

        }
    }

    public class LoadCropkindsAsyncTask extends AsyncTask<String, Integer, String[]>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }
        @Override
        protected String[] doInBackground(String... params) {
            String[] strs = productType.split("/");
            return  strs;
        }
        @Override
        protected void onPostExecute(String[] strs)
        {
            for(String str : strs){
                if(str.equals("TRSQ")) {//土壤墒情监测
                    bt_sq.setVisibility(ifHide);
                    bt_sq.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTitle.setText("土壤墒情监测");
                        }
                    });
                }
                if(str.equals("NZWJXFL")) {//农作物精细分类
                    bt_zwfl.setVisibility(ifHide);
                    bt_zwfl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTitle.setText("农作物精细分类");
                        }
                    });
                }
                if(str.equals("YMJZSJC")) {//叶面积指数监测,去掉了？
                    bt_zs.setVisibility(ifHide);
                    bt_zs.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTitle.setText("叶面积指数监测");
                        }
                    });
                }
                if(str.equals("NYBCHJC")) {//农业病虫害监测
                    bt_bch.setVisibility(ifHide);
                    bt_bch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTitle.setText("农业病虫害监测");
                        }
                    });
                }
                if(str.equals("NZWZSJC")) {//农作物长势监测
                    bt_zs.setVisibility(ifHide);
                    bt_zs.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTitle.setText("农作物长势监测");
                        }
                    });
                }
                if(str.equals("NZWGC")){//农作物估产
                    bt_gz.setVisibility(ifHide);
                    bt_gz.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTitle.setText("农作物估产");
                        }
                    });
                }
                if(str.equals("TRFL")){//土壤肥力
                    bt_fl.setVisibility(ifHide);
                    bt_fl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTitle.setText("土壤肥力");
                        }
                    });
                }
            }


        }
    }
}
