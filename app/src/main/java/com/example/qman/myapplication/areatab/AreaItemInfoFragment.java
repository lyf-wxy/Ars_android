package com.example.qman.myapplication.areatab;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import android.widget.Toast;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISImageServiceLayer;
import com.esri.core.map.MosaicRule;
import com.esri.core.map.RasterFunction;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;
import com.example.qman.myapplication.utils.GalleryAdapter;
import com.example.qman.myapplication.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class AreaItemInfoFragment extends Fragment
{
    private String codeidStr = "";
    private Bundle savedState;//临时数据保存

    private ImageView legendPic;

    private MapView mMapView;

    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
    private List<String> mDatas;
    private List<String> mdatetime;

    private String mField;
    private String mSelectedClass;

    private ArcGISImageServiceLayer mArcGISImageServiceLayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.area_item_info_fragment, container, false);

        String geometry = ActivityUtil.getParam(getActivity(),"geometry");

        Bundle args = getArguments();
        if(args!=null)
        {
            mField = args.getString("field");
            mSelectedClass = args.getString("selectedClass");

        }

        legendPic = (ImageView) view.findViewById(R.id.legendPic);
        mMapView = (MapView)view.findViewById(R.id.mapofAreaItemInfo);


        //得到控件
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_horizontal);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        // load map
        new LoadMapAsyncTask().execute();

        return view ;
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

        //getMapLegend
        legendPic.setImageResource(R.drawable.legend);

    }
    private void initDatesListView()
    {
        mDatas = new ArrayList<String>(Arrays.asList(
                "http://10.2.3.222:6080/arcgis/rest/services/TRSQ_20170101/ImageServer",
                "http://10.2.3.222:6080/arcgis/rest/services/TRSQ_20170108/ImageServer",
                "http://10.2.3.222:6080/arcgis/rest/services/TRSQ_20170115/ImageServer",
                "http://10.2.3.222:6080/arcgis/rest/services/TRSQ_20170122/ImageServer",
                "http://10.2.3.222:6080/arcgis/rest/services/TRSQ_20170129/ImageServer"));
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
            addLayer2MapView(mSelectedClass);
            addLegend2MapView(mSelectedClass);

            initDatesListView();

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

}
